package com.ddz.ms.msg;

import java.io.IOException;

import javax.websocket.Session;

import com.ddz.ms.rdata.RedisMsgQuene;
import com.ddz.ms.util.ObjectUtil;

/**
 * 
 * WebSocket消息队列
 * 
 * @author tom
 * @date 2016-11-11
 */
public class WsMsgThread implements Runnable {

	@Override
	public void run() {
		while (true) {
			Msg msg = null;
			while ((msg = RedisMsgQuene.pop()) != null) {
				System.out.println(msg);
				Session session = WsMsgPrintWriter.get(msg.getUserId());
				if (session == null)
					continue;
				try {
					synchronized (session) {
						session.getBasicRemote().sendText(
								ObjectUtil.objectToJson(msg));
					}
				} catch (IOException e) {
					System.out
							.println("Chat Error: Failed to send message to client");
					WsMsgPrintWriter.remove(msg.getUserId());
					try {
						session.close();
					} catch (IOException e1) {
						// Ignore
					}
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