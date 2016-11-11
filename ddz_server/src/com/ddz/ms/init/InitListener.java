package com.ddz.ms.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class InitListener implements ServletContextListener {

	 public void contextDestroyed(final ServletContextEvent arg0) {
		System.out.println("关闭服务器");
	}

	public void contextInitialized(final ServletContextEvent arg0) {
		System.out.println("初始服务器");
		
	}
}
