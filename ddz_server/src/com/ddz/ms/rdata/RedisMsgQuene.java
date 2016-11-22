package com.ddz.ms.rdata;

import com.ddz.common.redis.JedisUtil;
import com.ddz.ms.model.Msg;
import com.ddz.ms.util.ObjectUtil;

/**
 * redis消息队列
 * 
 * @author admin
 * 
 */
public class RedisMsgQuene {
	// private static final Jedis jedis = RedisClient.getJedis();
	// private static final ShardedJedis shardedJedis =
	// RedisClient.getShardedJedis();
	private static final JedisUtil jedis = new JedisUtil();
	private static final byte[] REDISMSGQUENE = "redis_msg_quene".getBytes();

	private RedisMsgQuene() {

	}

	/**
	 * 添加消息
	 * 
	 * @param msg
	 */
	public static void push(Msg msg) {
		try {
			jedis.addList(REDISMSGQUENE, ObjectUtil.objectToBytes(msg));
			// jedis.lpush(REDISMSGQUENE, ObjectUtil.objectToBytes(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取出消息
	 * 
	 * @return
	 */
	public static Msg pop() {
		// byte[] rpop = shardedJedis.rpop(REDISMSGQUENE);
		byte[] rpop = jedis.getList(REDISMSGQUENE);
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
	/**
	 * 清空消息队列
	 */
	public static void empty(){
		jedis.del(REDISMSGQUENE);
	}
}
