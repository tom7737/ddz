package com.ddz.ms;

import java.net.UnknownHostException;

import com.ddz.ms.action.DdzController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.MongoClient;

import demo.HelloController;

public class DdzConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	@Override
	public void configRoute(Routes me) {
		System.out.println("初始化Jfinal");
		//初始化MongoKit
		MongoClient mc = null;
		try {
			mc = new MongoClient("127.0.0.1", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoKit.init(mc, "ddz");
		//初始Action
		me.add("/hello", HelloController.class);
		me.add("/ddz", DdzController.class);
	}

	@Override
	public void configPlugin(Plugins me) {
	}

	@Override
	public void configInterceptor(Interceptors me) {
	}

	@Override
	public void configHandler(Handlers me) {
	}

}
