package com.ddz.ms.util;

import java.net.UnknownHostException;

import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class Test {
	public static void main(String[] args) throws UnknownHostException {
		MongoKit.init(new MongoClient(), "ddz");
		DBCollection collection = MongoKit.getCollection("users");
		BasicDBObject dbobj = new BasicDBObject();
		dbobj.put("username", "tom");
		collection.insert(dbobj);
	}
}
