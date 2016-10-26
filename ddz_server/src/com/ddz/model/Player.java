package com.ddz.model;

import java.io.Serializable;
import java.net.Socket;
import java.util.List;
/**
 *游戏者 * 
* 
* @author tom 
* @date 2016-10-22
 */
public class Player implements Serializable{

	private static final long serialVersionUID = 5732930012273200036L;

	private int id;
	private String name;//名字
	private List<Poker> pokers;//手牌
	private Socket socket;
	private boolean island;//是否是地主
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Player(int id, String name, List<Poker> pokers, boolean island) {
		super();
		this.id = id;
		this.name = name;
		this.pokers = pokers;
		this.island = island;
	}
	
	public Player(int id, String name, List<Poker> pokers, Socket socket,
			boolean island) {
		super();
		this.id = id;
		this.name = name;
		this.pokers = pokers;
		this.socket = socket;
		this.island = island;
	}
	public Player() {
		super();
	}
	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", pokers=" + pokers
				+ ", island=" + island + "]";
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
