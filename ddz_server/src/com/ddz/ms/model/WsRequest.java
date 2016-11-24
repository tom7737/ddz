package com.ddz.ms.model;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket请求数据类
 * 
 * @author admin
 * 
 */
public class WsRequest {

	private String method;
	private Map<String, String> parms;

	/**
	 * 参数类型method?parm1=xxx&parm2=xxx&parm3=xxx
	 * 
	 * @param message
	 */
	public WsRequest(String message) {
		super();
		int indexOf = message.indexOf("?");
		if (indexOf > -1) {
			method = message.substring(0, indexOf).trim();
			String parms_temp = message.substring(indexOf + 1);
			String[] parms_split = parms_temp.split("&");
			parms = new HashMap<String, String>();
			for (String parm : parms_split) {
				String[] split = parm.split("=");
				if (split.length >= 2) {
					parms.put(split[0].trim(), split[1].trim());
				}
			}
		} else {
			method = message.trim();
		}
	}

	public String getMethod() {
		return method;
	}

	public String getParm(String key) {
		return parms.get(key);
	}
}
