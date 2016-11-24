package com.ddz.ms.msg;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

/**
 * WebSocket用户的消息推送器
 * 
 * @author admin
 * 
 */
public class WsMsgPrintWriter {
	/**
	 * 用户的消息推送器
	 */
	private static Map<String, Session> outs = new HashMap<String, Session>();

	/**
	 * 设置用户的消息推送器
	 * 
	 * @param userId
	 * @param out
	 */
	public static void set(String userId, Session out) {
		outs.put(userId, out);
	}

	/**
	 * 获取用户的消息推送器
	 * 
	 * @param userId
	 * @return
	 */
	public static Session get(String userId) {
		return outs.get(userId);
	}

	/**
	 * 删除用户的消息推送器
	 * 
	 * @param userId
	 */
	public static void remove(String userId) {
		outs.remove(userId);
	}
}
