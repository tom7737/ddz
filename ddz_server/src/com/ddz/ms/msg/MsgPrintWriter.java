package com.ddz.ms.msg;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MsgPrintWriter {
	/**
	 * 用户的消息推送器
	 */
	private static Map<String, PrintWriter> outs = new HashMap<String, PrintWriter>();

	/**
	 * 设置用户的消息推送器
	 * 
	 * @param userId
	 * @param out
	 */
	public static void setPrintWriter(String userId, PrintWriter out) {
		outs.put(userId, out);
	}

	/**
	 * 获取用户的消息推送器
	 * 
	 * @param userId
	 * @return
	 */
	public static PrintWriter getPrintWriter(String userId) {
		return outs.get(userId);
	}
}
