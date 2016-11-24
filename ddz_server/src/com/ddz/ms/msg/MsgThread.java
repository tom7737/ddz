package com.ddz.ms.msg;

import java.io.PrintWriter;

import com.ddz.ms.rdata.RedisMsgQuene;

/**
 * 
 * 消息队列
 * 
 * @author tom
 * @date 2016-11-11
 */
public class MsgThread implements Runnable {

	@Override
	public void run() {
		while (true) {
			Msg msg = null;
			while ((msg = RedisMsgQuene.pop()) != null) {
				System.out.println(msg);
				if (msg.getTimeDelay() > 0) {// 另起线程发送延时消息
					Thread t = new Thread(new MsgThreadSendDelay(msg));
					t.start();
				} else {
					PrintWriter out = MsgPrintWriter.getPrintWriter(msg
							.getUserId());
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