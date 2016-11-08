package com.ddz.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.ddz.model.Msg;
import com.ddz.model.Player;
import com.ddz.model.Poker;
import com.ddz.model.Table;
import com.ddz.util.IsBigger;
import com.ddz.util.PokerType;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.Render;
import com.mongodb.util.JSON;

import freemarker.template.utility.StringUtil;

/**
 * 桌子控制类*
 * 
 * @author tom
 * @date 2016-10-23
 */
public class DdzController extends Controller {

	/**
	 * 注册
	 */
	public void reg() {

	}

	/**
	 * 桌子集合
	 */
	private static Map<String, Table> tables = new HashMap<String, Table>();
	/**
	 * 用户编号集合，模拟凑桌人数
	 */
	private static List<String> userIds = new LinkedList<String>();
	/**
	 * 当前桌子的编号
	 */
	private String tableKey;
	/**
	 * 保存用户和桌子的关系
	 */
	private static Map<String, String> user_table = new HashMap<String, String>();

	/**
	 * 玩家上桌，如果凑齐3人，则创建新桌子。
	 * 
	 * @param userId
	 */
	private void inTable(String userId) {
		if (userIds.size() >= 2) {
			// 取出userIds的值，并把userIds初始化
			String userId1 = userIds.get(0);
			String userId2 = userIds.get(1);
			userIds = new LinkedList<String>();
			// 初始化桌子
			Table table = new Table();
			Player p1 = new Player();
			p1.setName(userId1);
			Player p2 = new Player();
			p2.setName(userId2);
			Player p3 = new Player();
			p3.setName(userId);
			table.inTable(p1);
			table.inTable(p2);
			table.inTable(p3);
			tableKey = UUID.randomUUID().toString();
			tables.put(tableKey, table);
			// 添加用户和桌子的关系
			user_table.put(userId, tableKey);
			user_table.put(userId1, tableKey);
			user_table.put(userId2, tableKey);
			// 启动线程执行后续的工作
			Thread handler = new Thread(new HandlerThread());
			handler.start();
		} else {
			userIds.add(userId);
		}
	}

	/**
	 * 处理牌桌初始化工作
	 * 
	 * @author tom
	 * @date 2016-10-22
	 */
	private class HandlerThread implements Runnable {
		@Override
		public void run() {
			System.out.println(tableKey);
			Table table = tables.get(tableKey);
			List<Player> players = randomPoker(table);
			// 通知前端
			for (Player player : players) {
				startMsg.put(player.getName(), tableKey);
			}
		}
	}

	private List<Player> randomPoker(Table table) {
		List<Poker> pokers = new Msg().getList();
		List<Player> players = table.getPlayers();
		// 发牌
		// 打乱顺序
		Collections.shuffle(pokers);
		Iterator<Poker> iterator = pokers.iterator();
		for (Player player : players) {
			List<Poker> ppk = new ArrayList<Poker>();
			for (int i = 0; i < 17; i++) {
				ppk.add(iterator.next());
			}
			// 根据name排序
			ppk = Poker.sortById(ppk);
			player.setPokers(ppk);
		}
		List<Poker> lands = new ArrayList<Poker>();
		lands.add(iterator.next());
		lands.add(iterator.next());
		lands.add(iterator.next());
		table.setLands(lands);
		// table.show();
		// 随机一个用户先叫牌
		table.randomActionPlayerId();
		// 初始叫牌记录和当前叫牌人
		table.setLandvs(new HashMap<String, Integer>());
		table.setCallPalyer(null);
		// 状态改为叫地主中
		table.setStatus(1);
		return players;
	}

	/**
	 * 准备接口的消息列表
	 */
	private static Map<String, String> startMsg = new HashMap<String, String>();

	/**
	 * 准备
	 */
	public void start() {
		String userId = this.getPara("userId");
		if (userId == null)
			userId = UUID.randomUUID().toString();
		if (userIds.contains(userId) || user_table.containsKey(userId)) {
			renderNull();
			return;
		}
		inTable(userId);
		HttpServletResponse res = this.getResponse();
		// content type must be set to text/event-stream
		res.setContentType("text/event-stream");
		// encoding must be set to UTF-8
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = res.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			// 推送准备接口的消息
			if (startMsg.containsKey(userId)) {
				// 获取tableKey 并从MAP中删除
				String tableKey = startMsg.get(userId);
				startMsg.remove(userId);
				if ("out".equals(tableKey)) {// 用户退出
					renderText("已经退出桌子");
					return;
				}
				Table table = tables.get(tableKey);
				System.out.println("当前用户：" + userId);
				table.show();
				List<Player> players = table.getPlayers();
				Record re = new Record();
				String[] userIds = new String[3];
				int i = 0;
				int myindex = -1;
				// 获取手牌，当前出牌者，标记用户的位置，便于区分上家及下家
				for (Player player : players) {
					userIds[i++] = player.getName();
					if (userId.equals(player.getName())) {
						re.set("pokers",
								Poker.pokerFormatLtoI(player.getPokers()));
						myindex = i - 1;
					}
				}
				if (myindex == 0) {
					re.set("lastUserId", userIds[2]);
					re.set("nextUserId", userIds[1]);
				} else if (myindex == 1) {
					re.set("lastUserId", userIds[0]);
					re.set("nextUserId", userIds[2]);
				} else if (myindex == 2) {
					re.set("lastUserId", userIds[1]);
					re.set("nextUserId", userIds[0]);
				}
				re.set("actionPlayerId", table.getActionPlayerId());
				re.set("userId", userId);
				String json = JsonKit.toJson(re);
				out.println("event:start");
				out.flush();
				out.println("data: " + json + "\n");
				out.flush();
			}
			// 推送叫地主接口的消息
			if (selectLandMsg.containsKey(userId)) {
				// 获取tableKey 并从MAP中删除
				String tableKey = selectLandMsg.get(userId);
				selectLandMsg.remove(userId);
				Table table = tables.get(tableKey);
				Record re = new Record();
				re.set("actionPlayerId", table.getActionPlayerId());// 下一个行动者
				re.set("callPalyer", table.getCallPalyer());// 当前叫地主的玩家ID
				re.set("landv", table.getLandvs().get(table.getCallPalyer()));// 叫分值
				re.set("initPoints", table.getInitPoints());// 低分
				re.set("status", table.getStatus());// 状态
				// 开始游戏
				// 判断桌子的状态是否为游戏中,显示地主牌和谁是地主
				if (table.getStatus() == 2) {
					re.set("lands", Poker.pokerFormatLtoI(table.getLands()));// 地主牌
					re.set("landId", table.getLandId());// 地主ID
				}
				String json = JsonKit.toJson(re);
				out.println("event:selectLand");
				out.flush();
				out.println("data: " + json + "\n");
				out.flush();

			}
			// 推送出牌接口的消息
			if (outPokerMsg.containsKey(userId)) {
				// 获取tableKey 并从MAP中删除
				String tableKey = outPokerMsg.get(userId);
				outPokerMsg.remove(userId);
				Table table = tables.get(tableKey);
				List<Map<String, Integer[]>> outPokerLog = table
						.getOutPokerLog();
				Map<String, Integer[]> map = outPokerLog
						.get(outPokerLog.size() - 1);
				Record re = new Record();
				String outPokerMan = map.keySet().iterator().next();
				re.set("outPokerMan", outPokerMan);// 出牌人
				re.set("outPoker", map.get(outPokerMan));// 出的牌
				re.set("actionPlayerId", table.getActionPlayerId());// 下一个行动者
				re.set("status", table.getStatus());// 牌桌状态
				String json = JsonKit.toJson(re);
				out.println("event:outPoker");
				out.flush();
				out.println("data: " + json + "\n");
				out.flush();

			}
			// 推送游戏结束的消息
			if (gameOverMsg.containsKey(userId)) {
				// 获取tableKey 并从MAP中删除
				String tableKey = gameOverMsg.get(userId);
				gameOverMsg.remove(userId);
				Table table = tables.get(tableKey);
				int result = table.getResults();
				Record re = new Record();
				re.set("result", result);// 结果
				String json = JsonKit.toJson(re);
				out.println("event:gameOver");
				out.flush();
				out.println("data: " + json + "\n");
				out.flush();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// String[] ints = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
		// };
		List<String> asList = new ArrayList<String>();
		asList.add("1");
		asList.add("2");
		asList.add("3");
		asList.add("4");
		asList.add("5");
		asList.add("6");
		asList.add("7");
		asList.add("8");
		asList.add("9");
		asList.add("10");
		List<String> delList = new ArrayList<String>();
		for (String integer : asList) {
			if (Integer.valueOf(integer) % 3 == 0) {
				delList.add(integer);
			}
		}
		asList.removeAll(delList);
		for (String string : asList) {
			System.out.println(string);
		}
		// byte[] b = { 65, 66, 67 };
		// for (int i = 0; i < b.length; i++) {
		// System.out.print(b[i]);
		// }
		// String s = new String(b);
		// System.out.println(s);
		// byte[] bytes = s.getBytes();
		// for (int i = 0; i < bytes.length; i++) {
		//
		// System.out.print(bytes[i]);
		// }
		// System.out.print("event：start\n");
		// System.out.print("data：  json \n\n");
	}

	/**
	 * 叫地主的消息列表
	 */
	private static Map<String, String> selectLandMsg = new HashMap<String, String>();

	/**
	 * 叫地主
	 */
	public void selectLand() {
		// 获取用户ID，叫分值
		String userId = this.getPara("userId");
		Integer landv = this.getParaToInt("landv");
		if (landv < 0 || landv > 3) {
			renderNull();
			return;
		}
		// 验证用户ID是否为当前行动的用户
		String tableKey = user_table.get(userId);
		Table table = tables.get(tableKey);
		if (!table.getActionPlayerId().equals(userId)) {
			renderText("不是当前行动的玩家");
			return;
		}
		// 记录叫分人和叫分值
		Map<String, Integer> landvs = table.getLandvs();
		landvs.put(userId, landv);
		table.setCallPalyer(userId);
		table.setInitPoints(landv);
		// 如果当前用户叫了3分，则直接开始
		if (table.getInitPoints() == 3) {// 结束叫牌（开始游戏）
			startGame(table);
		} else if (table.getLandvs().size() == 3) {// 如果这是第三个玩家--且三个玩家中有一个叫过分，则开始游戏。如果三个玩家都未叫分，则重新发牌
			if (table.getInitPoints() > 0) {// 结束叫牌（开始游戏）
				startGame(table);
			} else {
				// 行动玩家置空
				table.setActionPlayerId(null);
				// 设置牌桌编号
				this.tableKey = user_table.get(userId);
				// 重新发牌
				Thread handler = new Thread(new AgainRandomPokerThread());
				handler.start();
			}
		} else {
			// 行动人按顺序延后
			table.nextActionPlayerId();
		}
		System.out.println("通知前端");
		// 通知前端
		for (Player player : table.getPlayers()) {
			selectLandMsg.put(player.getName(), tableKey);
		}
		renderNull();
	}

	/**
	 * 开始打牌的准备工作
	 * 
	 * @param table
	 * @param landId
	 */
	private void startGame(Table table) {
		// 叫分最高的成为地主
		String landId = null;
		Set<String> landvs_userIds = table.getLandvs().keySet();
		for (String landvs_userId : landvs_userIds) {
			if (table.getInitPoints() == table.getLandvs().get(landvs_userId)) {
				landId = landvs_userId;
				break;
			}
		}
		// 状态改为游戏中
		table.setStatus(2);
		// 设置地主ID
		table.setLandId(landId);
		// 地主成为当前行动人
		table.setActionPlayerId(landId);
		// 地主牌发给地主
		List<Poker> lands = table.getLands();
		List<Player> players = table.getPlayers();
		for (Player player : players) {
			if (player.getName().equals(landId)) {
				player.setIsland(true);
				// 将地主牌发给地主
				player.getPokers().addAll(lands);
				// 地主手牌排序
				Collections.sort(player.getPokers(), new Comparator<Poker>() {
					public int compare(Poker o1, Poker o2) {
						return o2.getName() - o1.getName();
					}
				});
			} else {
				player.setIsland(false);
			}
		}
	}

	/**
	 * 处理重新发牌工作，主要是等叫地主信息发出后再重新发牌
	 * 
	 * @author tom
	 * @date 2016-11-3
	 */
	private class AgainRandomPokerThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("重新发牌");
			Thread handler = new Thread(new HandlerThread());
			handler.start();
		}
	}

	/**
	 * 出牌的消息列表
	 */
	private static Map<String, String> outPokerMsg = new HashMap<String, String>();

	/**
	 * 出牌
	 */
	public void outPoker() {
		String userId = this.getPara("userId");
		String[] pokerIds_temp = this.getPara("pokers").split(",");
		Integer[] pokerIds = new Integer[pokerIds_temp.length];
		for (int i = 0; i < pokerIds_temp.length; i++) {
			pokerIds[i] = Integer.valueOf(pokerIds_temp[i]);
		}
		pokerIds = Poker.sortById(pokerIds);// 排序，方便后续操作
		List<Integer> l_pokerIds = Arrays.asList(pokerIds);
		// List<Poker> pokers = Poker.pokerFormatItoL(pokerIds);
		// System.out.println(pokers);
		// 判断是否为当前行动人
		String tableKey = user_table.get(userId);
		Table table = tables.get(tableKey);
		if (!table.getActionPlayerId().equals(userId)) {
			renderText("不是当前行动的玩家");
			return;
		}
		String pokerType = PokerType.pokerType(Poker.pokerFormatItoL(pokerIds));
		// 判断出牌是否符合规则
		if (PokerType.ERROR.equals(pokerType)) {
			renderText("出的牌不符合规则");
			return;
		}
		if (!IsBigger.isBigger(table.getOutPokerLog(), pokerIds)) {
			renderText("出的牌不符合规则");
			return;
		}
		// 判断是不是炸弹，如果是炸弹则分数翻倍
		if (PokerType.ZHADAN.equals(pokerType)
				|| PokerType.WANGZHA.equals(pokerType)) {
			table.setInitPoints(table.getInitPoints() * 2);
		}
		// 将出的牌从手牌中转移到弃牌区
		List<Player> players = table.getPlayers();
		for (Player player : players) {
			if (player.getName().equals(userId)) {
				List<Poker> pokers_temp = player.getPokers();
				List<Poker> delpokers = new ArrayList<Poker>();// 需要删除的手牌
				for (Poker poker : pokers_temp) {
					if (l_pokerIds.contains(poker.getId())) {
						delpokers.add(poker);
					}
				}
				table.getFolds().addAll(delpokers);// 将要出的牌加入弃牌区
				pokers_temp.removeAll(delpokers);// 将要出的牌从手牌中删除
				break;
			}
		}
		// players.get(0).getPokers();
		// 记录出牌结果
		List<Map<String, Integer[]>> outPokerLog = table.getOutPokerLog();
		Map<String, Integer[]> userId_outPoker = new HashMap<String, Integer[]>();
		userId_outPoker.put(userId, pokerIds);
		outPokerLog.add(userId_outPoker);
		// 顺延行动人
		table.nextActionPlayerId();
		// 判断是否已将牌出完，出完则结束游戏
		for (Player player : players) {
			if (player.getName().equals(userId)
					&& player.getPokers().size() == 0) {
				table.setStatus(0);// 牌桌状态改为空闲
				table.setActionPlayerId(null);// 行动人制空
				// 记录游戏结果
				if (player.isIsland()) {// 判断出完牌的这个人是不是地主
					table.setResults(0);
				} else {
					table.setResults(1);
				}
				// 设置牌桌编号
				this.tableKey = tableKey;
				// 另起线程通知前端
				Thread t = new Thread(new GameOverThread());
				t.start();
				break;
			}
		}

		// 通知前端
		for (Player player : table.getPlayers()) {
			outPokerMsg.put(player.getName(), tableKey);
		}
		renderNull();
	}

	/**
	 * 不出
	 */
	public void notOutPoker() {
		String userId = this.getPara("userId");
		String tableKey = user_table.get(userId);
		Table table = tables.get(tableKey);
		//FIXME 判断是否必须得出牌（上家下家都不出则必须出牌）
		// 记录出牌结果
		List<Map<String, Integer[]>> outPokerLog = table.getOutPokerLog();
		Map<String, Integer[]> userId_outPoker = new HashMap<String, Integer[]>();
		userId_outPoker.put(userId, null);
		outPokerLog.add(userId_outPoker);
		// 顺延行动人
		table.nextActionPlayerId();
		// 通知前端
		for (Player player : table.getPlayers()) {
			outPokerMsg.put(player.getName(), tableKey);
		}
		renderNull();
	}

	/**
	 * 出牌的消息列表
	 */
	private static Map<String, String> gameOverMsg = new HashMap<String, String>();

	/**
	 * 处理游戏结束工作，主要是等最后出牌信息发出后再重新发牌
	 * 
	 * @author tom
	 * @date 2016-11-3
	 */
	private class GameOverThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("游戏结束");
			// 通知前端
			Table table = tables.get(tableKey);
			for (Player player : table.getPlayers()) {
				gameOverMsg.put(player.getName(), tableKey);
			}
		}
	}

	/**
	 * 离开桌子
	 */
	public void outTable() {
		// TODO 正在游戏时不能退出
		String userId = this.getPara("userId");
		if (userIds.contains(userId)) {
			// 从桌上退出
			userIds.remove(userId);
			System.out.println(userId + "退出");
		}
		// 通知客户端
		startMsg.put(userId, "out");
		renderNull();
	}
}