package com.ddz.ms.rdata;

import redis.clients.jedis.Jedis;

import com.ddz.common.redis.RedisClient;
import com.ddz.ms.model.Msg;
import com.ddz.ms.util.ObjectUtil;

/**
 * redis消息队列
 *  FIXME 报错。在闹钟线程执行时，会报以下错误
 *  redis.clients.jedis.exceptions.JedisDataException: ERR Protocol error: expected '$', got ' '
 *  Caused by: java.net.SocketException: Connection reset
 *  判断错误可能：
 *  1、jedis同时读写，报错。--每次使用时都新建连接。使用完后关闭？
 *  2、jedis未关闭（可能性小）
 *  3、http请求和jedis同时发起，报错。
 *  4、jedis使用不当，读和写不应该都使用Jedis.读应该使用shardedJedis
 * @author admin
 * 
 */
public class RedisMsgQuene {
	private static final Jedis jedis = RedisClient.getJedis();

	private static final byte[] REDISMSGQUENE = "redis_msg_quene".getBytes();

	private RedisMsgQuene() {

	}

	/**
	 * 添加消息
	 * 
	 * @param msg
	 */
	public  static void push(Msg msg) {
		try {
			jedis.lpush(REDISMSGQUENE, ObjectUtil.objectToBytes(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取出消息
	 * 
	 * @return
	 */
	public  static Msg pop() {
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
