package com.ddz.ms.init;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import redis.clients.jedis.Jedis;

import com.ddz.common.redis.JedisUtil;
import com.ddz.common.redis.RedisClient;
import com.ddz.ms.model.Msg;
import com.ddz.ms.rdata.RedisMsgQuene;
import com.ddz.ms.rdata.UserAutoData;
import com.ddz.ms.rdata.UserGameData;

public class InitListener implements ServletContextListener {

	public void contextDestroyed(final ServletContextEvent arg0) {
		// 清空redis内的 user_game数据、redis_msg_quene数据、user_auto数据
		System.out.println("关闭服务器，清空redis数据");
		RedisMsgQuene.empty();
		UserGameData.empty();
		UserAutoData.empty();
	}

	public void contextInitialized(final ServletContextEvent arg0) {
		System.out.println("初始消息队列服务");
		Thread t = new Thread(new MsgThread());
		try {
			Thread.sleep(500);//等待系统完成其他初始化工作
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t.start();

	}

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
	 * 
	 * 消息队列
	 * 
	 * @author tom
	 * @date 2016-11-11
	 */
	private class MsgThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				Msg msg = null;
				while ((msg = RedisMsgQuene.pop()) != null) {
					System.out.println(msg);
					if (msg.getTimeDelay() > 0) {// 另起线程发送延时消息
						Thread t = new Thread(new SendDelayMsgThread(msg));
						t.start();
					} else {
						PrintWriter out = outs.get(msg.getUserId());
						if (out == null)
							continue;
						out.println("event:" + msg.getEvent());
						out.flush();
						out.println("data: " + msg.getData() + "\n");
						out.flush();
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * 发送延时消息
	 * 
	 * @author tom
	 * @date 2016-11-15
	 */
	private class SendDelayMsgThread implements Runnable {
		private Msg msg;

		public SendDelayMsgThread(Msg msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(msg.getTimeDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			PrintWriter out = outs.get(msg.getUserId());
			if (out != null) {
				//
				out.println("event:" + msg.getEvent());
				out.flush();
				out.println("data: " + msg.getData() + "\n");
				out.flush();
			}
		}
	}

}
