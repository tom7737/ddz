package com.ddz.ms.rdata;

import com.ddz.common.redis.JedisUtil;

/**
 * 用户和牌局的关系
 * 
 * @author admin
 * 
 */
public class UserGameData {
	// private static Jedis jedis = RedisClient.getJedis();
	// private static ShardedJedis shardedJedis = RedisClient.getShardedJedis();
	private static final JedisUtil jedis = new JedisUtil();
	private static final byte[] USER_GAME = "user_game".getBytes();

	private UserGameData() {

	}

	/**
	 * 设置用户和牌局的关系
	 * 
	 * @param userId
	 * @param gameId
	 */
	public static void hset(String userId, String gameId) {
		try {
			jedis.setHSet(USER_GAME, userId.getBytes(), gameId.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据用户ID获取牌局ID
	 * 
	 * @param userId
	 * @return
	 */
	public static String hget(String userId) {
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
	 * 判断用户是否在牌局中
	 * 
	 * @param userId
	 * @return
	 */
	public static Boolean exists(String userId) {
		return jedis.existsHSet(USER_GAME, userId.getBytes());
	}

	/**
	 * 解除用户和牌局的关系
	 * 
	 * @param userId
	 */
	public static void hdel(String userId) {
		jedis.delHSet(USER_GAME, userId.getBytes());
	}

	/**
	 * 清空用户和牌局的关系
	 */
	public static void empty() {
		jedis.del(USER_GAME);
	}
}
