package com.ddz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 桌子*
 * 
 * @author tom
 * @date 2016-10-22
 */
public class Table implements Serializable {

	private static final long serialVersionUID = 334111123742983L;
	/**
	 * 标识列
	 */
	private Integer tableId;
	/**
	 * 桌子编号
	 */
	private Integer tableNum;
	/**
	 * 桌上的玩家
	 */
	private List<Player> players;
	/**
	 * 弃牌区
	 */
	private List<Poker> folds =new ArrayList<Poker>();
	/**
	 * 地主牌
	 */
	private List<Poker> lands;
	/**
	 * 桌子状态 0空闲1游戏中
	 */
	private int status = 0;
	/**
	 * 行动的玩家编号
	 */
	private String actionPlayerId;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getTableId() {
		return tableId;
	}

	public void setTableId(Integer tableId) {
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

	public void show() {
		System.out.println("当前牌况：");
		for (Player player : players) {
			System.out.println("用户" + player.getName() + "拥有手牌：");
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
		System.out.println("弃牌：");
		for (Poker poker : folds) {
			System.out.print(poker.show());
		}
	}

}
