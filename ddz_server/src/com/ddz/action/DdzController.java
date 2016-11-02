package com.ddz.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.ddz.model.Msg;
import com.ddz.model.Player;
import com.ddz.model.Poker;
import com.ddz.model.Table;
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
			Table table = tables.get(tableKey);
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
				Collections.sort(ppk, new Comparator<Poker>() {
					public int compare(Poker o1, Poker o2) {
						return o2.getName() - o1.getName();
					}
				});
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
			//状态改为叫地主中
			table.setStatus(1);
			// 通知前端
			for (Player player : players) {
				startMsg.put(player.getName(), tableKey);
			}
		}
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
		if (userIds.contains(userId)/* ||user_table.containsKey(userId) */) {
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
						re.set("pokers", pokerFormat(player.getPokers()));
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
				re.set("actionPlayerId", table.getActionPlayerId());
				re.set("callPalyer", table.getCallPalyer());
				re.set("landv", table.getLandvs().get(table.getCallPalyer()));
				String json = JsonKit.toJson(re);
				out.println("event:selectLand");
				out.flush();
				out.println("data: " + json + "\n");
				out.flush();
				
				// TODO 开始游戏
				//判断桌子的状态是否为游戏中
				
				//将地主牌发给地主
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		byte[] b = {65,66,67};
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i]);
		}
		String s = new String(b);
		System.out.println(s);
		byte[] bytes = s.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			
			System.out.print(bytes[i]);
		}
		//		System.out.print("event：start\n");
		//		System.out.print("data：  json \n\n");
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
			//状态改为游戏中
			table.setStatus(1);
			//当前用户成为地主
			table.setLandId(userId);
			//当前用户（地主）成为当前行动人
			table.setActionPlayerId(userId);
		}else if (table.getLandvs().size() == 3) {// 如果这是第三个玩家--且三个玩家中有一个叫过分，则开始游戏。如果三个玩家都未叫分，则重新发牌
			if (table.getInitPoints() > 0) {// 结束叫牌（开始游戏）
				//状态改为游戏中
				table.setStatus(1);
				//当前用户成为地主
				table.setLandId(userId);
				//当前用户（地主）成为当前行动人
				table.setActionPlayerId(userId);
			} else {
				// TODO 重新发牌
			}
		}else{
			// 行动人按顺序延后
			table.nextActionPlayerId();
		}
		// 通知前端
		for (Player player : table.getPlayers()) {
			selectLandMsg.put(player.getName(), tableKey);
		}
		renderNull();
	}

	/**
	 * 将poker对象列表转换成pokerId列表
	 * 
	 * @param pokers
	 * @return
	 */
	private Integer[] pokerFormat(List<Poker> pokers) {
		Integer[] rv = new Integer[pokers.size()];
		for (int i = 0; i < rv.length; i++) {
			rv[i] = pokers.get(i).getId();
		}
		return rv;
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