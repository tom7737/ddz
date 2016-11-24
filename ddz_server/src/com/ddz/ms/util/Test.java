package com.ddz.ms.util;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws Exception {
		// MongoKit.init(new MongoClient(), "ddz");
		// DBCollection collection = MongoKit.getCollection("users");
		// BasicDBObject dbobj = new BasicDBObject();
		// dbobj.put("username", "tom");
		// collection.insert(dbobj);
//		Test t = new Test();
//		t.test();

	}
	//线程中断的测试程序
	@SuppressWarnings("deprecation")
	public void test() throws IOException, InterruptedException {
		ClockThread thread = new ClockThread();
		
		thread.start();
		System.out.println("在50秒之内按任意键中断线程!");
		System.in.read();
		thread.notNeedRun();
	}

	private class ClockThread extends Thread {

		private boolean needRun = true;

		public void notNeedRun() {
			needRun = false;
		}

		public void run() {
			System.out.println("开始计时");
			try {
				sleep(5000); // 延迟5秒
			} catch (InterruptedException e) {
				System.out.println("hehe:" + e.getMessage());
			}
			if (needRun) {
				System.out.println("开始工作");
			} else {
				System.out.println("已经不需要我工作了");
			}
		}

	}
}
