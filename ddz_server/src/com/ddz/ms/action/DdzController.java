package com.ddz.ms.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.ddz.ms.factory.PokerFactory;
import com.ddz.ms.init.InitListener;
import com.ddz.ms.init.RedisMsgQuene;
import com.ddz.ms.model.Msg;
import com.ddz.ms.model.Player;
import com.ddz.ms.model.Poker;
import com.ddz.ms.model.Table;
import com.ddz.ms.service.SeatService;
import com.ddz.ms.service.TableService;
import com.ddz.ms.service.impl.SeatServiceImpl;
import com.ddz.ms.service.impl.TableServiceImpl;
import com.ddz.ms.util.PokerType;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 桌子控制类*
 *  FIXME 换桌，计时器
 * @author tom
 * @date 2016-10-23
 */
public class DdzController extends Controller {

	private TableService tableService = new TableServiceImpl();
	private SeatService seatService = new SeatServiceImpl();

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
			p1.setUserId(userId1);
			Player p2 = new Player();
			p2.setUserId(userId2);
			Player p3 = new Player();
			p3.setUserId(userId);
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
				Record re = new Record();
				String[] userIds = new String[3];
				int i = 0;
				int myindex = -1;
				// 获取手牌，当前出牌者，标记用户的位置，便于区分上家及下家
				re.set("pokers", Poker.pokerFormatLtoI(player.getPokers()));
				for (Player p2 : players) {
					userIds[i++] = p2.getUserId();
					if (player.getUserId().equals(p2.getUserId())) {
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
				re.set("userId", player.getUserId());
				String json = JsonKit.toJson(re);
				// 添加到消息队列
				RedisMsgQuene
						.push(new Msg(player.getUserId(), Msg.READY, json));
			}
		}
	}

	/**
	 * 发牌
	 * 
	 * @param table
	 * @return
	 */
	private List<Player> randomPoker(Table table) {
		List<Poker> pokers = PokerFactory.getInstance();
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
		// 初始叫牌记录
		table.setLandvLog(new ArrayList<Map<String, Integer>>());
		// 状态改为叫地主中
		table.setStatus(1);
		return players;
	}

	/**
	 * 准备
	 */
	public void ready() {
		String userId = this.getPara("userId");
		seatService.inSeat(userId);
		seatService.ready(userId);
		DBObject dbObject = seatService.getByUserId(userId);
		if (dbObject != null && dbObject.get("tableNum") != null) {
			Integer tableNum = Integer.valueOf(dbObject.get("tableNum")
					.toString());
			DBCursor dbCursor = seatService.getByTableNum(tableNum);
			List<String> userIds = new ArrayList<String>();
			while (dbCursor.hasNext()) {// 判断桌上玩家是否都已准备，如果有未准备的则直接返回
				DBObject next = dbCursor.next();
				if (next.get("userId") == null) {
					renderNull();
					return;
				}
				Object isReady = next.get("isReady");
				if (isReady == null || !(Boolean) isReady) {
					renderNull();
					return;
				}
				userIds.add(next.get("userId").toString());
			}
			if (userIds.size() != 3) {
				renderNull();
				return;
			}
			// 初始化桌子
			Table table = new Table();
			tableKey = UUID.randomUUID().toString();
			table.setTableId(tableKey);
			for (String string : userIds) {
				Player player = new Player();
				player.setUserId(string);
				table.inTable(player);
				// 添加用户和桌子的关系
				user_table.put(string, tableKey);
			}
			tables.put(tableKey, table);
			// 启动线程执行后续的工作
			Thread handler = new Thread(new HandlerThread());
			handler.start();
		}

		renderNull();
		return;
	}

	/**
	 * 建立连接
	 */
	public void start() {
		String userId = this.getPara("userId");
		if (userId == null)
			userId = UUID.randomUUID().toString();
		if (userIds.contains(userId) || user_table.containsKey(userId)) {
			renderNull();
			return;
		}
		// inTable(userId);
		// 初始化HTML5消息推送器
		HttpServletResponse res = this.getResponse();
		res.setContentType("text/event-stream");
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = res.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InitListener.setPrintWriter(userId, out);

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

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
		List<Map<String, Integer>> landvLog = table.getLandvLog();
		Map<String, Integer> landvs_temp = new HashMap<String, Integer>();
		landvs_temp.put(userId, landv);
		landvLog.add(landvs_temp);
		table.setInitPoints(landv);
		// 如果当前用户叫了3分，则直接开始
		if (table.getInitPoints() == 3) {// 结束叫牌（开始游戏）
			startGame(table);
		} else if (table.getLandvLog().size() == 3) {// 如果这是第三个玩家--且三个玩家中有一个叫过分，则开始游戏。如果三个玩家都未叫分，则重新发牌
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
		// System.out.println("通知前端");
		// 通知前端
		Record re = new Record();
		re.set("actionPlayerId", table.getActionPlayerId());// 下一个行动者
		re.set("callPalyer", userId);// 当前叫地主的玩家ID
		re.set("landv", landv);// 叫分值
		re.set("initPoints", table.getInitPoints());// 底分
		re.set("status", table.getStatus());// 状态
		// 开始游戏
		// 判断桌子的状态是否为游戏中,显示地主牌和谁是地主
		if (table.getStatus() == 2) {
			re.set("lands", Poker.pokerFormatLtoI(table.getLands()));// 地主牌
			re.set("landId", table.getLandId());// 地主ID
		}
		String json = JsonKit.toJson(re);
		for (Player player : table.getPlayers()) {
			RedisMsgQuene.push(new Msg(player.getUserId(), Msg.SELECT_LAND,
					json));
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
		List<Map<String, Integer>> landvs = table.getLandvLog();
		for (Map<String, Integer> map : landvs) {
			String landvs_userId = map.keySet().iterator().next();
			if (table.getInitPoints() == map.get(landvs_userId)) {
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
			if (player.getUserId().equals(landId)) {
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
		// 判断是否为当前行动人
		String tableKey = user_table.get(userId);
		Table table = tables.get(tableKey);
		if (!table.getActionPlayerId().equals(userId)) {
			renderText("不是当前行动的玩家");
			return;
		}
		String pokerType = PokerType.pokerType(Poker.pokerFormatItoL(pokerIds));
		// 判断出牌是否符合规则
		// if (PokerType.ERROR.equals(pokerType)) {
		// renderText("出的牌不符合规则");
		// return;
		// }
		// if (!IsBigger.isBigger(table.getOutPokerLog(), pokerIds)) {
		// renderText("出的牌不符合规则");
		// return;
		// }

		List<Player> players = table.getPlayers();
		// 验证要出的牌是否在手牌中
		for (Player player : players) {
			if (player.getUserId().equals(userId)) {// 找到出牌者
				List<Integer> ltoI = Arrays.asList(Poker.pokerFormatLtoI(player
						.getPokers()));// 手牌
				for (Integer poker : l_pokerIds) {
					if (!ltoI.contains(poker)) {
						renderText("没有相应的手牌");// 如果循环对比下来没有找到对应的手牌，则说明手牌中没有这张要出的牌
						return;
					}
				}
				break;
			}
		}
		// 判断是不是炸弹，如果是则炸弹数加一
		if (PokerType.ZHADAN.equals(pokerType)
				|| PokerType.WANGZHA.equals(pokerType)) {
			table.incBombCount();
		}
		// 将出的牌从手牌中转移到弃牌区
		for (Player player : players) {
			if (player.getUserId().equals(userId)) {
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
			if (player.getUserId().equals(userId)
					&& player.getPokers().size() == 0) {
				table.setActionPlayerId(null);// 行动人制空
				table.setStatus(0);// 牌桌状态改为空闲
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
		Record re = new Record();
		re.set("outPokerMan", userId);// 出牌人
		re.set("outPoker", pokerIds);// 出的牌
		re.set("actionPlayerId", table.getActionPlayerId());// 下一个行动者
		re.set("status", table.getStatus());// 牌桌状态
		String json = JsonKit.toJson(re);
		for (Player player : table.getPlayers()) {
			// outPokerMsg.put(player.getName(), tableKey);
			RedisMsgQuene
					.push(new Msg(player.getUserId(), Msg.OUT_POKER, json));
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
		List<Map<String, Integer[]>> outPokerLog = table.getOutPokerLog();
		// 判断是否必须得出牌（上家下家都不出，或是第一个出牌的。必须出牌）
		if (outPokerLog == null || outPokerLog.size() == 0) {
			renderText("你必须出牌");
			return;
		} else if (outPokerLog.size() > 2) {
			Map<String, Integer[]> map = outPokerLog
					.get(outPokerLog.size() - 1);
			Integer[] integers = map.get(map.keySet().iterator().next());
			Map<String, Integer[]> map2 = outPokerLog
					.get(outPokerLog.size() - 2);
			Integer[] integers2 = map2.get(map2.keySet().iterator().next());
			if (integers == null && integers2 == null) {
				renderText("你必须出牌");
				return;
			}
		}

		// 记录出牌结果
		Map<String, Integer[]> userId_outPoker = new HashMap<String, Integer[]>();
		userId_outPoker.put(userId, null);
		outPokerLog.add(userId_outPoker);
		// 顺延行动人
		table.nextActionPlayerId();
		// 通知前端
		Record re = new Record();
		re.set("outPokerMan", userId);// 出牌人
		re.set("outPoker", null);// 出的牌
		re.set("actionPlayerId", table.getActionPlayerId());// 下一个行动者
		re.set("status", table.getStatus());// 牌桌状态
		String json = JsonKit.toJson(re);
		for (Player player : table.getPlayers()) {
			RedisMsgQuene
					.push(new Msg(player.getUserId(), Msg.OUT_POKER, json));
		}
		renderNull();
	}

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
			Table table = tables.get(tableKey);
			List<Player> players = table.getPlayers();
			// 取消玩家的准备状态
			for (Player player : players) {
				seatService.cancelReady(player.getUserId());
				// 解除用户和桌子的关系
				user_table.remove(player.getUserId());
			}
			// 保存对局记录
			tableService.saveLog(table);
			// 通知前端
			int result = table.getResults();
			Record re = new Record();
			re.set("result", result);// 结果
			re.set("initPoints", table.getInitPoints());// 底分
			re.set("bombCount", table.getBombCount());// 炸弹数
			String json = JsonKit.toJson(re);
			for (Player player : table.getPlayers()) {
				RedisMsgQuene.push(new Msg(player.getUserId(), Msg.GAME_OVER,
						json));
			}
			// 从牌局列表中删除牌局
			tables.remove(tableKey);
		}
	}

	/**
	 * 离开桌子
	 */
	public void outTable() {
		// 正在游戏时不能退出
		String userId = this.getPara("userId");
		String tableKey = user_table.get(userId);
		if (tableKey == null) {
			renderNull();
			return;
		}
		Table table = tables.get(tableKey);
		if (table == null || table.getStatus() != 0) {
			renderNull();
			return;
		}
		seatService.exit(userId);
		System.out.println(userId + "退出");
		// 通知客户端
		renderNull();
	}
}