package com.ddz.ms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 桌子*
 * 
 * @author tom
 * @date 2016-10-22
 */
public class Table implements Serializable {

	private static final long serialVersionUID = 773123742983L;
	/**
	 * 标识列
	 */
	private String tableId;
	/**
	 * 桌子编号
	 */
	private Integer tableNum;
	/**
	 * 桌上的玩家
	 */
	private List<Player> players = new ArrayList<Player>();
	/**
	 * 弃牌区
	 */
	private List<Poker> folds = new ArrayList<Poker>();
	/**
	 * 地主牌
	 */
	private List<Poker> lands;
	/**
	 * 桌子状态 0空闲1叫地主中2游戏中
	 */
	private Integer status = 0;
	/**
	 * 行动的玩家编号
	 */
	private String actionPlayerId;
	/**
	 * 地主分记录
	 */
	private List<Map<String, Integer>> landvLog;// = new HashMap<String,
												// Integer>();
	/**
	 * 地主玩家编号
	 */
	private String landId;
	/**
	 * 底分
	 */
	private Integer initPoints;
	/**
	 * 出牌记录
	 */
	private List<Map<String, Integer[]>> outPokerLog = new ArrayList<Map<String, Integer[]>>();

	/**
	 * 游戏结果0地主胜利1农民胜利
	 */
	private Integer results;

	/**
	 * 炸弹数
	 */
	private Integer bombCount = 0;

	/**
	 * 桌子初始化
	 * 
	 * @param userId
	 * @return
	 */
	public String init(List<String> userId) {
		return null;
	}

	/**
	 * 获取当前桌上的人数
	 * 
	 * @return
	 */
	public int getPlayercount() {
		return players.size();
	}

	/**
	 * 玩家上桌
	 * 
	 * @param player
	 *            玩家对象
	 */
	public boolean inTable(Player player) {
		if (players.size() >= 3)
			return false;
		players.add(player);
		return true;
	}

	public Integer getBombCount() {
		return bombCount;
	}

	public void incBombCount() {
		this.bombCount++;
	}
	public void setBombCount(Integer bombCount) {
		this.bombCount = bombCount;
	}

	public Integer getResults() {
		return results;
	}

	public void setResults(Integer results) {
		this.results = results;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Poker> getFolds() {
		return folds;
	}

	public void setFolds(List<Poker> folds) {
		this.folds = folds;
	}

	public List<Poker> getLands() {
		return lands;
	}

	public void setLands(List<Poker> lands) {
		this.lands = lands;
	}

	public Integer getTableNum() {
		return tableNum;
	}

	public void setTableNum(Integer tableNum) {
		this.tableNum = tableNum;
	}

	public Table() {
		super();
	}

	public String getActionPlayerId() {
		return actionPlayerId;
	}

	public void setActionPlayerId(String actionPlayerId) {
		this.actionPlayerId = actionPlayerId;
	}

	public List<Map<String, Integer>> getLandvLog() {
		return landvLog;
	}

	public void setLandvLog(List<Map<String, Integer>> landvLog) {
		this.landvLog = landvLog;
	}

	public String getLandId() {
		return landId;
	}

	public void setLandId(String landId) {
		this.landId = landId;
	}

	public Integer getInitPoints() {
		return initPoints;
	}

	public void setInitPoints(Integer initPoints) {
		if (this.initPoints == null || this.initPoints < initPoints) {
			this.initPoints = initPoints;
		}
	}

	public List<Map<String, Integer[]>> getOutPokerLog() {
		return outPokerLog;
	}

	public void setOutPokerLog(List<Map<String, Integer[]>> outPokerLog) {
		this.outPokerLog = outPokerLog;
	}

	public void show() {
		System.out.println("当前牌况：");
		for (Player player : players) {
			System.out.println("用户" + player.getUserId() + "拥有手牌：");
			List<Poker> pokers = player.getPokers();
			for (Poker poker : pokers) {
				System.out.print(poker.show());
			}
			System.out.println();
		}
		System.out.println("地主牌：");
		for (Poker poker : lands) {
			System.out.print(poker.show());
		}
		System.out.println();
		System.out.println("弃牌：");
		for (Poker poker : folds) {
			System.out.print(poker.show());
		}
	}

	/**
	 * 
	 * 当前行动人改为下家
	 */
	public void nextActionPlayerId() {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getUserId().equals(this.actionPlayerId)) {
				if (i == players.size() - 1)
					this.actionPlayerId = players.get(0).getUserId();
				else
					this.actionPlayerId = players.get(i + 1).getUserId();
				break;
			}
		}

	}

	/**
	 * 随机选择一个人为当前行动人
	 */
	public void randomActionPlayerId() {
		Random r = new Random();
		int nextInt = r.nextInt(3);
		this.actionPlayerId = players.get(nextInt).getUserId();
	}

}
