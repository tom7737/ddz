package com.ddz.ms.rdata;

import com.ddz.common.redis.JedisUtil;

/**
 * 用户的托管状态
 * 
 * @author admin
 * 
 */
public class UserAutoData {
	private static final JedisUtil jedis = new JedisUtil();
	private static final byte[] USER_GAME = "user_auto".getBytes();

	private UserAutoData() {

	}

	/**
	 * 设置用户的托管状态
	 * 
	 * @param userId
	 */
	public static void setAuto(String userId) {
		hset(userId, "2");
	}

	/**
	 * 初始化用户的托管状态
	 * 
	 * @param userId
	 */
	public static void init(String userId) {
		hset(userId, "0");
	}

	/**
	 * 累加用户的托管次数
	 * 
	 * @param userId
	 */
	public static void inc(String userId) {
		String autoCount = hget(userId);
		if (autoCount == null)
			return;
		Integer autoCounts = Integer.valueOf(autoCount);
		hset(userId, String.valueOf(++autoCounts));
	}

	/**
	 * 设置用户的托管次数
	 * 
	 * @param userId
	 * @param gameId
	 */
	private static void hset(String userId, String autoCount) {
		try {
			jedis.setHSet(USER_GAME, userId.getBytes(), autoCount.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据用户ID获取托管次数
	 * 
	 * @param userId
	 * @return
	 */
	private static String hget(String userId) {
		byte[] rpop = null;
		try {
			rpop = jedis.getHSet(USER_GAME, userId.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (rpop == null) {
			return null;
		}
		try {
			return new String(rpop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断用户是否为已托管状态
	 * 
	 * @param userId
	 * @return
	 */
	public static Boolean isAuto(String userId) {
		String autoCount = hget(userId);
		if (autoCount == null)
			return false;
		if (Integer.valueOf(autoCount) >= 2) {//托管次数超过1次则为托管状态
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 解除用户和托管状态的关系
	 * 
	 * @param userId
	 */
	public static void hdel(String userId) {
		jedis.delHSet(USER_GAME, userId.getBytes());
	}
	
	/**
	 * 清空用户和托管状态的关系
	 */
	public static void empty(){
		jedis.del(USER_GAME);
	}
}
