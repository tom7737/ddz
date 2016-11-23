package com.ddz.common.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
/**
 * redis客户端帮助类.提供redis连接
 * 这是关于jedis2.1.0.jar的帮助类。由于版本过老问题太多已经不使用
 * @author admin
 *
 */
public class RedisClient {
	private static Jedis jedis;// 非切片额客户端连接
	private static JedisPool jedisPool;// 非切片连接池
	private static ShardedJedis shardedJedis;// 切片额客户端连接
	private static ShardedJedisPool shardedJedisPool;// 切片连接池
	// radis服务器ip
	private static final String JEDIS_POOL = "127.0.0.1";
	// radis服务器端口号
	private static final Integer JEDIS_PORT = 6379;

	public static Jedis getJedis() {
		return jedis;
	}

	// public static JedisPool getJedisPool() {
	// return jedisPool;
	// }

	public static ShardedJedis getShardedJedis() {
		return shardedJedis;
	}

	// public static ShardedJedisPool getShardedJedisPool() {
	// return shardedJedisPool;
	// }

	static {
		initialPool();
		initialShardedPool();
		shardedJedis = shardedJedisPool.getResource();
		jedis = jedisPool.getResource();
	}

	private RedisClient() {
	}

	/**
	 * 初始化非切片池
	 */
	private static void initialPool() {
		// 池基本配置
		JedisPoolConfig config = new JedisPoolConfig();
		// config.setMaxActive(20);
		config.setMaxIdle(5);
		// config.setMaxWait(10000l);
		config.setTestOnBorrow(false);
		jedisPool = new JedisPool(config, JEDIS_POOL, JEDIS_PORT);
	}

	/**
	 * 初始化切片池
	 */
	private static void initialShardedPool() {
		// 池基本配置
		JedisPoolConfig config = new JedisPoolConfig();
		// config.setMaxActive(20);
		config.setMaxIdle(5);
		// config.setMaxWait(10000l);
		config.setTestOnBorrow(false);
		// slave链接
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo(JEDIS_POOL, JEDIS_PORT, "master"));
		// 构造池
		shardedJedisPool = new ShardedJedisPool(config, shards);
	}

}