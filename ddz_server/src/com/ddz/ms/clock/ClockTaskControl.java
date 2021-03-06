package com.ddz.ms.clock;

import java.util.HashMap;
import java.util.Map;

/**
 * 闹钟任务控制类--创建闹钟，停止闹钟
 * FIXME *** 重做闹钟任务，使用WebSocket后，不能使用http连接方式调用闹钟任务了
 * 调用原有类的方法实现任务执行？如果用户断线怎么办？
 * 用户断线不销毁类，而是继续执行，直到这局游戏结束才销毁？用户断线重连怎么办？
 * 用户断线重连后立即销毁原有类。 
 * @author admin
 * 
 */
public class ClockTaskControl {

	public static Map<String, ClockThread> map = new HashMap<String, ClockThread>();

	public static void main(String[] args) {
		Map<String, String> parms = new HashMap<String, String>();
		parms.put("userId", "1");
		parms.put("count", "1");
		ClockTaskControl.createClockTask("1", new ClockTask(
				ClockTask.URL_DDZ_OUTTABLE, parms));
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		Map<String, String> parms2 = new HashMap<String, String>();
		parms2.put("userId", "1");
		parms2.put("count", "2");
		ClockTaskControl.createClockTask("1", new ClockTask(
				ClockTask.URL_DDZ_OUTTABLE, parms2));
	}

	/**
	 * 创建一个闹钟任务
	 */
	public static void createClockTask(String userId, ClockTask task) {
//		 stopClockTask(userId);// 先停止已有的闹钟
//		 task.setUserId(userId);
//		 ClockThread t = new ClockThread(task);
//		 map.put(userId, t);
//		 t.start();
	}

	/**
	 * 停止一个闹钟任务
	 */
	public static void stopClockTask(String userId) {
//		 if (map.containsKey(userId))
//		 map.get(userId).notNeedRun();
	}

}
