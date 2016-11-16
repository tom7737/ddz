package com.ddz.ms.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.ddz.ms.clock.ClockTask;
import com.ddz.ms.clock.ClockTaskControl;
import com.ddz.ms.factory.PokerFactory;
import com.ddz.ms.init.InitListener;
import com.ddz.ms.model.Game;
import com.ddz.ms.model.Msg;
import com.ddz.ms.model.Player;
import com.ddz.ms.model.Poker;
import com.ddz.ms.rdata.RedisMsgQuene;
import com.ddz.ms.rdata.UserGameData;
import com.ddz.ms.service.GameService;
import com.ddz.ms.service.SeatService;
import com.ddz.ms.service.impl.GameServiceImpl;
import com.ddz.ms.service.impl.SeatServiceImpl;
import com.ddz.ms.util.PokerType;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 桌子控制类* FIXME
 * 换桌4ok，计时器5，实时记录叫地主和出牌日志3no暂时不做(牵扯到牌局信息的保存)，redis代替map存储数据1ok，消息队列添加延时功能2ok,断线重连7，监听断线6（使用WebSocket）
 * 
 * @author tom
 * @date 2016-10-23
 */
public class DdzController extends Controller {

	private GameService gameService = new GameServiceImpl();

	private SeatService seatService = new SeatServiceImpl();
	/**
	 * 牌局集合---因为游戏中牌局的操作最多，暂时先放入内存中
	 */
	private static Map<String, Game> games = new HashMap<String, Game>();

	/**
	 * 注册
	 */
	public void reg() {

	}

	/**
	 * 发牌
	 * 
	 * @param game
	 * @return
	 */
	private List<Player> randomPoker(Game game) {
		List<Poker> pokers = PokerFactory.getInstance();
		List<Player> players = game.getPlayers();
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
		game.setLands(lands);
		// 随机一个用户先叫牌
		game.randomActionPlayerId();
		// 初始叫牌记录
		game.setLandvLog(new ArrayList<Map<String, Integer>>());
		// 状态改为叫地主中
		game.setStatus(1);
		return players;
	}

	/**
	 * 准备
	 */
	public void ready() {
		String userId = this.getPara("userId");
		// FIXME 关闭用户的其他闹钟任务，最好写一个拦截器实现
		ClockTaskControl.sopClockTask(userId);
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
			// 初始化这一局游戏
			Game game = new Game();
			game.setTableNum(tableNum);
			String gameId = UUID.randomUUID().toString();
			game.setGameId(gameId);
			for (String string : userIds) {
				Player player = new Player();
				player.setUserId(string);
				game.inGame(player);
				// 添加用户和桌子的关系
				// user_game.put(string, gameId);
				UserGameData.hset(string, gameId);
			}
			games.put(gameId, game);
			// 启动线程执行后续的工作
			Thread handler = new Thread(new HandlerThread(gameId));
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
		if (UserGameData.exists(userId)/* user_game.containsKey(userId) */) {
			renderNull();
			return;
		}
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
		String gameId = UserGameData.hget(userId);// user_game.get(userId);
		Game game = games.get(gameId);
		if (!game.getActionPlayerId().equals(userId)) {
			renderText("不是当前行动的玩家");
			return;
		}
		// 记录叫分人和叫分值
		List<Map<String, Integer>> landvLog = game.getLandvLog();
		Map<String, Integer> landvs_temp = new HashMap<String, Integer>();
		landvs_temp.put(userId, landv);
		landvLog.add(landvs_temp);
		game.setInitPoints(landv);
		// 如果当前用户叫了3分，则直接开始
		if (game.getInitPoints() == 3) {// 结束叫牌（开始游戏）
			startGame(game);
		} else if (game.getLandvLog().size() == 3) {// 如果这是第三个玩家--且三个玩家中有一个叫过分，则开始游戏。如果三个玩家都未叫分，则重新发牌
			if (game.getInitPoints() > 0) {// 结束叫牌（开始游戏）
				startGame(game);
			} else {
				// 行动玩家置空
				game.setActionPlayerId(null);
				// 重新发牌
				Thread handler = new Thread(new AgainRandomPokerThread(gameId));
				handler.start();
			}
		} else {
			// 行动人按顺序延后
			game.nextActionPlayerId();
		}
		// System.out.println("通知前端");
		// 通知前端
		Record re = new Record();
		re.set("actionPlayerId", game.getActionPlayerId());// 下一个行动者
		re.set("callPalyer", userId);// 当前叫地主的玩家ID
		re.set("landv", landv);// 叫分值
		re.set("initPoints", game.getInitPoints());// 底分
		re.set("status", game.getStatus());// 状态
		// 开始游戏
		// 判断桌子的状态是否为游戏中,显示地主牌和谁是地主
		if (game.getStatus() == 2) {
			re.set("lands", Poker.pokerFormatLtoI(game.getLands()));// 地主牌
			re.set("landId", game.getLandId());// 地主ID
		}
		String json = JsonKit.toJson(re);
		for (Player player : game.getPlayers()) {
			RedisMsgQuene.push(new Msg(player.getUserId(), Msg.SELECT_LAND,
					json));
		}
		renderNull();
	}

	/**
	 * 开始打牌的准备工作
	 * 
	 * @param game
	 * @param landId
	 */
	private void startGame(Game game) {
		// 叫分最高的成为地主
		String landId = null;
		List<Map<String, Integer>> landvs = game.getLandvLog();
		for (Map<String, Integer> map : landvs) {
			String landvs_userId = map.keySet().iterator().next();
			if (game.getInitPoints() == map.get(landvs_userId)) {
				landId = landvs_userId;
				break;
			}
		}
		// 状态改为游戏中
		game.setStatus(2);
		// 设置地主ID
		game.setLandId(landId);
		// 地主成为当前行动人
		game.setActionPlayerId(landId);
		// 地主牌发给地主
		List<Poker> lands = game.getLands();
		List<Player> players = game.getPlayers();
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
		String gameId = UserGameData.hget(userId);// user_game.get(userId);
		Game game = games.get(gameId);
		if (!game.getActionPlayerId().equals(userId)) {
			renderText("不是当前行动的玩家");
			return;
		}
		String pokerType = PokerType.pokerType(Poker.pokerFormatItoL(pokerIds));
		// 判断出牌是否符合规则
		// if (PokerType.ERROR.equals(pokerType)) {
		// renderText("出的牌不符合规则");
		// return;
		// }
		// if (!IsBigger.isBigger(game.getOutPokerLog(), pokerIds)) {
		// renderText("出的牌不符合规则");
		// return;
		// }

		List<Player> players = game.getPlayers();
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
			game.incBombCount();
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
				game.getFolds().addAll(delpokers);// 将要出的牌加入弃牌区
				pokers_temp.removeAll(delpokers);// 将要出的牌从手牌中删除
				break;
			}
		}
		// 记录出牌结果
		List<Map<String, Integer[]>> outPokerLog = game.getOutPokerLog();
		Map<String, Integer[]> userId_outPoker = new HashMap<String, Integer[]>();
		userId_outPoker.put(userId, pokerIds);
		outPokerLog.add(userId_outPoker);
		// 顺延行动人
		game.nextActionPlayerId();
		// 判断是否已将牌出完，出完则结束游戏
		for (Player player : players) {
			if (player.getUserId().equals(userId)
					&& player.getPokers().size() == 0) {
				game.setActionPlayerId(null);// 行动人制空
				game.setStatus(0);// 牌桌状态改为空闲
				// 记录游戏结果
				if (player.isIsland()) {// 判断出完牌的这个人是不是地主
					game.setResults(0);
				} else {
					game.setResults(1);
				}
				// 另起线程完成游戏结束的工作
				Thread t = new Thread(new GameOverThread(gameId));
				t.start();
				break;
			}
		}

		// 通知前端
		Record re = new Record();
		re.set("outPokerMan", userId);// 出牌人
		re.set("outPoker", pokerIds);// 出的牌
		re.set("actionPlayerId", game.getActionPlayerId());// 下一个行动者
		re.set("status", game.getStatus());// 牌桌状态
		String json = JsonKit.toJson(re);
		for (Player player : game.getPlayers()) {
			// outPokerMsg.put(player.getName(), gameId);
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
		String gameId = UserGameData.hget(userId);// user_game.get(userId);
		Game game = games.get(gameId);
		List<Map<String, Integer[]>> outPokerLog = game.getOutPokerLog();
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
		game.nextActionPlayerId();
		// 通知前端
		Record re = new Record();
		re.set("outPokerMan", userId);// 出牌人
		re.set("outPoker", null);// 出的牌
		re.set("actionPlayerId", game.getActionPlayerId());// 下一个行动者
		re.set("status", game.getStatus());// 牌桌状态
		String json = JsonKit.toJson(re);
		for (Player player : game.getPlayers()) {
			RedisMsgQuene
					.push(new Msg(player.getUserId(), Msg.OUT_POKER, json));
		}
		renderNull();
	}

	/**
	 * 离开桌子
	 */
	public void outTable() {
		// 用户在牌局中时不能退出
		String userId = this.getPara("userId");
		String gameId = UserGameData.hget(userId);// user_game.get(userId);
		if (gameId != null) {
			renderNull();
			return;
		}
		seatService.exit(userId);
		System.out.println(userId + "退出");
		// 通知客户端
		renderNull();
	}

	/**
	 * 换桌
	 */
	public void changeTable() {
		// 用户在牌局中时不能换桌
		String userId = this.getPara("userId");
		// FIXME 关闭用户的其他闹钟任务，最好写一个拦截器实现
				ClockTaskControl.sopClockTask(userId);
		String gameId = UserGameData.hget(userId);// user_game.get(userId);
		if (gameId != null) {
			renderNull();
			return;
		}
		seatService.changeSeat(userId);
		// 通知客户端
		renderNull();
	}

	/**
	 * 处理游戏结束工作，主要是等最后出牌信息发出后再重新发牌
	 * 
	 * @author tom
	 * @date 2016-11-3
	 */
	private class GameOverThread implements Runnable {
		String gameId = null;

		public GameOverThread(String gameId) {
			this.gameId = gameId;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("游戏结束");
			Game game = games.get(gameId);
			List<Player> players = game.getPlayers();
			// 取消玩家的准备状态
			for (Player player : players) {
				seatService.cancelReady(player.getUserId());
				// 解除用户和桌子的关系
				UserGameData.hdel(player.getUserId());
			}
			// 保存对局记录
			gameService.saveLog(game);
			// 通知前端
			int result = game.getResults();
			Record re = new Record();
			re.set("result", result);// 结果
			re.set("initPoints", game.getInitPoints());// 底分
			re.set("bombCount", game.getBombCount());// 炸弹数
			String json = JsonKit.toJson(re);
			for (Player player : game.getPlayers()) {
				RedisMsgQuene.push(new Msg(player.getUserId(), Msg.GAME_OVER,
						json));
			}
			// 从牌局列表中删除牌局
			games.remove(gameId);
			// 添加定时器，30秒内未准备则退出牌桌
			for (Player player : players) {
				Map<String, String> parms2 = new HashMap<String, String>();
				parms2.put("userId", player.getUserId());
				ClockTaskControl.createClockTask(player.getUserId(), new ClockTask(
						ClockTask.URL_DDZ_OUTTABLE, parms2));
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
		String gameId = null;

		public AgainRandomPokerThread(String gameId) {
			this.gameId = gameId;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("重新发牌");
			Thread handler = new Thread(new HandlerThread(gameId));
			handler.start();
		}
	}

	/**
	 * 处理牌桌初始化工作
	 * 
	 * @author tom
	 * @date 2016-10-22
	 */
	private class HandlerThread implements Runnable {

		String gameId = null;

		public HandlerThread(String gameId) {
			this.gameId = gameId;
		}

		@Override
		public void run() {
			System.out.println(gameId);
			Game game = games.get(gameId);
			List<Player> players = randomPoker(game);
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
				re.set("actionPlayerId", game.getActionPlayerId());
				re.set("userId", player.getUserId());
				String json = JsonKit.toJson(re);
				// 添加到消息队列
				RedisMsgQuene
						.push(new Msg(player.getUserId(), Msg.READY, json));
			}
		}
	}
}