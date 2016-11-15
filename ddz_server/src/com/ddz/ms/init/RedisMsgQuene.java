package com.ddz.ms.init;

import redis.clients.jedis.Jedis;

import com.ddz.common.redis.RedisClient;
import com.ddz.ms.model.Msg;
import com.ddz.ms.util.ObjectUtil;

public class RedisMsgQuene {
	private static final Jedis jedis = RedisClient.getJedis();

	private static final byte[] REDISMSGQUENE = "redis_msg_quene".getBytes();

	private RedisMsgQuene() {

	}
	/**
	 * 添加消息
	 * @param msg
	 */
	public static void push(Msg msg) {
		try {
			jedis.lpush(REDISMSGQUENE, ObjectUtil.objectToBytes(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 取出消息
	 * @return
	 */
	public static Msg pop() {
		byte[] rpop = jedis.rpop(REDISMSGQUENE);
		if (rpop == null) {
			return null;
		}
		try {
			return (Msg) ObjectUtil.bytesToObject(rpop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
