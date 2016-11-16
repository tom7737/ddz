package com.ddz.ms.model;

import java.io.Serializable;

/**
 * 消息类
 * 
 * @author admin
 * 
 */
public class Msg implements Serializable {

	public static final String READY = "ready";
	public static final String SELECT_LAND = "selectLand";
	public static final String OUT_POKER = "outPoker";
	public static final String GAME_OVER = "gameOver";

	private static final long serialVersionUID = 1118923748923L;

	private String userId;

	private String event;

	private String data;

	private Integer timeDelay;

	public Integer getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(Integer timeDelay) {
		this.timeDelay = timeDelay;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


	@Override
	public String toString() {
		return "Msg [userId=" + userId + ", event=" + event + ", data=" + data
				+ ", timeDelay=" + timeDelay + "]";
	}

	public Msg(String userId, String event, String data) {
		super();
		this.userId = userId;
		this.event = event;
		this.data = data;
		this.timeDelay = 0;
	}

	public Msg(String userId, String event, String data, Integer timeDelay) {
		super();
		this.userId = userId;
		this.event = event;
		this.data = data;
		this.timeDelay = timeDelay;
	}

	public Msg() {
		super();
	}

}
