package com.ddz.ms.init;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import redis.clients.jedis.Jedis;

import com.ddz.common.redis.RedisClient;
import com.ddz.ms.model.Msg;

public class InitListener implements ServletContextListener {

	public void contextDestroyed(final ServletContextEvent arg0) {
		System.out.println("关闭服务器");
	}
	public void contextInitialized(final ServletContextEvent arg0) {
		System.out.println("初始服务器");
		Thread t = new Thread(new MsgThread());
		t.start();

	}
	/**
	 * 用户的消息推送器
	 */
	private static Map<String, PrintWriter> outs = new HashMap<String, PrintWriter>();
	/**
	 * 设置用户的消息推送器
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
				Msg pop = null;
				while ((pop = RedisMsgQuene.pop()) != null) {
					System.out.println(pop);
					PrintWriter out = outs.get(pop.getUserId());
					if (out == null)
						continue;
					out.println("event:" + pop.getEvent());
					out.flush();
					out.println("data: " + pop.getData() + "\n");
					out.flush();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Jedis jedis = RedisClient.getJedis();

	public static void main(String[] args) {
		InitListener l = new InitListener();
		l.test();
	}

	public void test() {
		// System.out.println(jedis.llen("1"));
		// jedis.lpush("1", "tom","tom1","tom2");
		// jedis.lpush("1", "tom1");
		// jedis.lpush("1", "tom2");
		// jedis.lpush("1", "tom3");
		// if (jedis.llen("1") > 0) {
		System.out.println(jedis.rpop("1"));
		// }
	}
}
