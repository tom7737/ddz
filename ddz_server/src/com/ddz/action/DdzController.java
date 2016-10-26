package com.ddz.action;

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

import com.ddz.model.Msg;
import com.ddz.model.Player;
import com.ddz.model.Poker;
import com.ddz.model.Table;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
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
			List<Player> players = new ArrayList<Player>();
			players.add(p1);
			players.add(p2);
			players.add(p3);
			table.setPlayers(players);
			tableKey = UUID.randomUUID().toString();
			tables.put(tableKey, table);
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
			Random r = new Random();
			int nextInt = r.nextInt(3);
			String actionPlayerId = players.get(nextInt).getName();
			table.setActionPlayerId(actionPlayerId);
			// 通知前端
			for (Player player : players) {
				user_talbe.put(player.getName(), tableKey);
			}
		}
	}

	/**
	 * 标记用户与桌子的关系
	 */
	private static Map<String, String> user_talbe = new HashMap<String, String>();

	/**
	 * 准备
	 */
	public void start() {
		String userId = this.getPara("userId");
		if (userId == null)
			userId = UUID.randomUUID().toString();
		inTable(userId);
		while (true) {
			if (user_talbe.containsKey(userId)) {
				// 获取并从MAP中删除
				String tableKey = user_talbe.get(userId);
				user_talbe.remove(userId);
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
				renderJson(re);
				break;
			}
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
	public void selectLand(){
		//获取用户ID，叫分值
		//验证用户ID是否为当前
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
		String userId = this.getPara("userId");
		if (userIds.contains(userId)) {
			// 从桌上退出
			userIds.remove(userId);
		}
		// 通知客户端
		user_talbe.put(userId, "out");
		renderNull();
	}

}