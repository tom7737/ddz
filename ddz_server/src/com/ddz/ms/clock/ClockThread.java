package com.ddz.ms.clock;

import com.ddz.ms.util.HttpRequest;

/**
 * 时钟线程抽象类.每个实现类中实现具体要做的事情
 * 
 * TODO 1、自动退出桌子。2、自动不叫地主3、自动出牌
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
		try {
			sleep(task.getTimeDelay()); // 延迟时间
		} catch (InterruptedException e) {
			System.out.println("hehe:" + e.getMessage());
		}
		System.out.println(task.getFullUrl() + task.getParmsString());
		System.out.println(ClockTaskControl.map.size());
		if (needRun) {
			System.out.println("开始工作");
			someThing();
		} else {
			System.out.println("已经不需要我这个闹钟工作了");
		}
	}

	private void someThing() {
		try {
			HttpRequest.sendPost(task.getFullUrl(), task.getParmsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}