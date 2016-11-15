package com.ddz.ms.service;

import com.ddz.ms.service.base.BaseService;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public interface SeatService extends BaseService<String, DBObject> {
	/**
	 * 初始座位
	 */
	void initSeat();

	/**
	 * 玩家上桌---- mongoDB的update方法只修改第一个符合查询条件的数据，完美吻合玩家上桌的需求
	 * 
	 * @param userId
	 */
	void inSeat(String userId);

	/**
	 * 玩家准备
	 * 
	 * @param userId
	 */
	void ready(String userId);

	/**
	 * 玩家退出桌子
	 */
	void exit(String userId);

	/**
	 * 根据用户ID查询上桌情况
	 * 
	 * @param userId
	 * @return
	 */
	DBObject getByUserId(String userId);

	/**
	 * 根据桌子编号查询上桌情况
	 * 
	 * @param userId
	 * @return
	 */
	DBCursor getByTableNum(Integer tableNum);

	/**
	 * 取消准备
	 */
	void cancelReady(String userId);
}
