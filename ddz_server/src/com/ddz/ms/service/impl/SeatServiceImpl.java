package com.ddz.ms.service.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.ddz.ms.service.SeatService;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class SeatServiceImpl implements SeatService {

	private DBCollection seat = MongoKit.getCollection("seat");

	private static void initTest() {
		MongoClient mc = null;
		try {
			mc = new MongoClient("127.0.0.1", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoKit.init(mc, "ddz");
	}

	public static void main(String[] args) {
		initTest();
		SeatServiceImpl s = new SeatServiceImpl();
		s.initSeat();
		s.inSeat("1");
		s.ready("1");
		s.changeSeat("1");
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
			obj.put("isReady", false);
			obj.put("tableNum", (i / 3) + 1);
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
		if (getByUserId(userId) != null)
			return;
		BasicDBObject q = new BasicDBObject();
		q.put("userId", null);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject o2 = new BasicDBObject();
		o2.put("userId", userId);
		o.put("$set", o2);
		seat.update(q, o);
	}

	/**
	 * 玩家换桌
	 * 
	 * @param userId
	 */
	public void changeSeat(String userId) {
		DBObject byUserId = getByUserId(userId);
		if (byUserId == null) {
			inSeat(userId);
			ready(userId);
			return;
		}
		exit(userId);// 退出原来的桌子
		// 进入新的桌子并准备
		// { "$and" : [ { "userId" : null } , { "tableNum" : { "$ne" : 1}}]}
		BasicDBObject q = new BasicDBObject();
		BasicDBList ql = new BasicDBList();
		BasicDBObject q1 = new BasicDBObject();
		q1.put("userId", null);
		BasicDBObject q2 = new BasicDBObject();
		BasicDBObject q3 = new BasicDBObject();
		q3.put("$ne", Integer.valueOf(byUserId.get("tableNum").toString()));
		q2.put("tableNum", q3);
		ql.add(q1);
		ql.add(q2);
		q.put("$and", ql);
		// System.out.println(q);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject o2 = new BasicDBObject();
		o2.put("userId", userId);
		o2.put("isReady", true);
		o.put("$set", o2);
		seat.update(q, o);
	}

	/**
	 * 玩家准备
	 * 
	 * @param userId
	 */
	public void ready(String userId) {
		BasicDBObject q = new BasicDBObject();
		q.put("userId", userId);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject o2 = new BasicDBObject();
		o2.put("isReady", true);
		o.put("$set", o2);
		seat.update(q, o);
	}

	/**
	 * 玩家退出桌子
	 */
	public void exit(String userId) {
		BasicDBObject q = new BasicDBObject();
		q.put("userId", userId);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject o2 = new BasicDBObject();
		o2.put("isReady", false);
		o2.put("userId", null);
		o.put("$set", o2);
		seat.update(q, o);
	}

	@Override
	public Integer add(DBObject obj) {
		return null;
	}

	@Override
	public Integer update(DBObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBObject> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DBObject get(String id) {
		return null;
	}

	/**
	 * 根据用户ID查询上桌情况
	 * 
	 * @param userId
	 * @return
	 */
	public DBObject getByUserId(String userId) {
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("userId", userId);
		return seat.findOne(basicDBObject);
	}

	/**
	 * 根据桌子编号查询上桌情况
	 * 
	 * @param userId
	 * @return
	 */
	public DBCursor getByTableNum(Integer tableNum) {
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("tableNum", tableNum);
		return seat.find(basicDBObject);
	}

	/**
	 * 取消准备
	 */
	public void cancelReady(String userId) {
		BasicDBObject q = new BasicDBObject();
		q.put("userId", userId);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject o2 = new BasicDBObject();
		o2.put("isReady", false);
		o.put("$set", o2);
		seat.update(q, o);
	}
}
