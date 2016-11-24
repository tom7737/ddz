package com.ddz.ms.msg;

import java.io.PrintWriter;


/**
 * 
 * 发送延时消息
 * 
 * @author tom
 * @date 2016-11-15
 */
public class MsgThreadSendDelay implements Runnable {
	private Msg msg;

	public MsgThreadSendDelay(Msg msg) {
		this.msg = msg;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(msg.getTimeDelay());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		PrintWriter out = MsgPrintWriter.getPrintWriter(msg.getUserId());
		if (out != null) {
			//
			out.println("event:" + msg.getEvent());
			out.flush();
			out.println("data: " + msg.getData() + "\n");
			out.flush();
		}
	}
}