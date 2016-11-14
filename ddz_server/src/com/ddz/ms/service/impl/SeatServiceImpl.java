package com.ddz.ms.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ddz.ms.model.Table;
import com.ddz.ms.service.SeatService;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class SeatServiceImpl implements SeatService {

	private DBCollection seat = MongoKit.getCollection("seat");

//	private static void initTest {
//		MongoClient mc = null;
//		try {
//			mc = new MongoClient("127.0.0.1", 27017);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		MongoKit.init(mc, "ddz");
//	}
	
	public static void main(String[] args) {
		SeatServiceImpl s = new SeatServiceImpl();
		s.inSeat("1");
	}
	/**
	 * 初始座位
	 */
	public void initSeat() {
		seat.drop();
		List<DBObject> list = new ArrayList<DBObject>();
		for (int i = 0; i < 300; i++) {
			BasicDBObject obj = new BasicDBObject();
			obj.put("seatId", i + 1);
			obj.put("userId", null);
			obj.put("tableNum", i / 3);
			list.add(obj);
		}
		seat.insert(list);
	}

	/**
	 * 玩家上桌---- mongoDB的update方法只修改第一个符合查询条件的数据，完美吻合玩家上桌的需求
	 * 
	 * @param userId
	 */
	public void inSeat(String userId) {
		BasicDBObject q = new BasicDBObject();
		q.put("userId", null);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject o2 = new BasicDBObject();
		o2.put("userId", userId);
		o.put("$set", o2);
		 seat.update(q, o);
	}

	

	@Override
	public Integer add(Table obj) {
		return null;
	}

	@Override
	public Integer update(Table obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Table> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
