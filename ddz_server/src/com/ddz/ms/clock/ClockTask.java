package com.ddz.ms.clock;

import java.util.Map;
import java.util.Set;

/**
 * 闹钟任务实体类
 * 
 * @author admin
 * 
 */
public class ClockTask {
	/**
	 * FIXME 配置项，执行任务的接口服务器地址
	 */
	public static final String ADDRESS = "http://localhost:8080/ddz_server";
	/**
	 * 退出桌子
	 */
	public static final String URL_DDZ_OUTTABLE = "/ddz/outTable";
	public static final String URL_DDZ_SELECTLAND = "/ddz/selectLand";
	public static final String URL_DDZ_OUTPOKER = "/ddz/outPoker";
	public static final String URL_DDZ_NOTOUTPOKER = "/ddz/notOutPoker";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	
	private String userId;//用户ID
	
	private Integer timeDelay = 30000;// FIXME 延时时间，默认30秒.测试时为5秒

	private String address = ADDRESS;// 执行任务的接口服务器地址

	private String url;// 执行任务的接口地址

	private Map<String, String> parms;// 参数集

	private String method = METHOD_POST;// 请求方式，默认POST

	public String getFullUrl() {
		return address + url;
	}

	public String getParmsString() {
		StringBuffer rv = new StringBuffer();
		Set<String> keySet = parms.keySet();
		for (String key : keySet) {
			rv.append(key + "=" + parms.get(key) + "&");
		}
		rv.deleteCharAt(rv.length() - 1);
		return rv.toString();
	}

	public ClockTask(String url, Map<String, String> parms, Integer timeDelay) {
		super();
		this.timeDelay = timeDelay;
		this.url = url;
		this.parms = parms;
	}

	public ClockTask(String url, Map<String, String> parms) {
		super();
		this.url = url;
		this.parms = parms;
	}

	public ClockTask() {
		super();
	}

	public ClockTask(Integer timeDelay, String address, String url,
			Map<String, String> parms, String method) {
		super();
		this.timeDelay = timeDelay;
		this.address = address;
		this.url = url;
		this.parms = parms;
		this.method = method;
	}

	public Integer getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(Integer timeDelay) {
		this.timeDelay = timeDelay;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParms() {
		return parms;
	}

	public void setParms(Map<String, String> parms) {
		this.parms = parms;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
