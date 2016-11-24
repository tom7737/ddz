package com.ddz.ms.msg;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ddz.ms.rdata.RedisMsgQuene;
import com.ddz.ms.rdata.UserAutoData;
import com.ddz.ms.rdata.UserGameData;

public class MsgListener implements ServletContextListener {

	public void contextDestroyed(final ServletContextEvent arg0) {
		// 清空redis内的 user_game数据、redis_msg_quene数据、user_auto数据
		System.out.println("关闭服务器，清空redis数据");
		RedisMsgQuene.empty();
		UserGameData.empty();
		UserAutoData.empty();
	}

	public void contextInitialized(final ServletContextEvent arg0) {
		System.out.println("初始消息队列服务");
		Thread t = new Thread(new WsMsgThread());
		try {
			Thread.sleep(500);//等待系统完成其他初始化工作
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t.start();

	}


}
