package com.ddz.ms.model;

import java.io.Serializable;
import java.util.List;
/**
 *游戏者 * 
* 
* @author tom 
* @date 2016-10-22
 */
public class Player /*extends BasicDBObject*/ implements Serializable{

	private static final long serialVersionUID = 5732930012273200036L;

	private String id;
	private String userId;//名字--userId
	private List<Poker> pokers;//手牌
	private boolean island;//是否是地主
	private String tableId;//桌子ID
	
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<Poker> getPokers() {
		return pokers;
	}
	public void setPokers(List<Poker> pokers) {
		this.pokers = pokers;
	}
	public boolean isIsland() {
		return island;
	}
	public void setIsland(boolean island) {
		this.island = island;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Player(String id, String userId, List<Poker> pokers, boolean island) {
		super();
		this.id = id;
		this.userId = userId;
		this.pokers = pokers;
		this.island = island;
	}
	
	public Player() {
		super();
	}
	@Override
	public String toString() {
		return "Player [id=" + id + ", userId=" + userId + ", pokers=" + pokers
				+ ", island=" + island + "]";
	}
	
}
