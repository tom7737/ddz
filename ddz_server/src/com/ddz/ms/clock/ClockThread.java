package com.ddz.ms.clock;

import com.ddz.ms.rdata.UserAutoData;
import com.ddz.ms.util.HttpRequest;

/**
 * 闹钟线程类.正在执行闹钟任务的类
 * 
 *  1、自动退出桌子。2、自动不叫地主3、自动出牌
 * 
 * @author admin
 * 
 */
public class ClockThread extends Thread {

	private ClockTask task;

	private boolean needRun = true;

	public void notNeedRun() {
		needRun = false;
	}

	public ClockThread(ClockTask task) {
		this.task = task;
	}

	public void run() {
		System.out.println("开始计时");
		Boolean auto = UserAutoData.isAuto(task.getUserId());
		try {
			if (!auto) {
				sleep(task.getTimeDelay()); // 延迟时间
			}
		} catch (InterruptedException e) {
			System.out.println("hehe:" + e.getMessage());
		}
		 System.out.println(task.getFullUrl() + task.getParmsString());
		 System.out.println(ClockTaskControl.map.size());
		if (needRun) {
			System.out.println("开始工作");
			someThing();
			if (!auto) {
				UserAutoData.inc(task.getUserId());
			}
		} else {
			System.out.println("已经不需要我这个闹钟工作了");
		}
	}
	/**
	 * 对系统本身发送Post请求从而执行任务
	 */
	private void someThing() {
		try {
			HttpRequest.sendPost(task.getFullUrl(), task.getParmsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}