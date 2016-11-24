package com.ddz.ms.rdata;

import com.ddz.common.redis.JedisUtil;
import com.ddz.ms.model.Game;
import com.ddz.ms.util.ObjectUtil;

/**
 * 保存牌局数据（暂未启用）
 * 
 * @author admin
 * 
 */
public class GamesData {
	// private static final Jedis jedis = RedisClient.getJedis();
	// private static final ShardedJedis shardedJedis =
	// RedisClient.getShardedJedis();
	private static final JedisUtil jedis = new JedisUtil();
	private static final byte[] GAMES = "games".getBytes();

	private GamesData() {

	}

	public static void hset(String gameId, Game game) {
		try {
			jedis.setHSet(GAMES, gameId.getBytes(),
					ObjectUtil.objectToBytes(game));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Game hget(String gameId) {
		byte[] rpop = jedis.getHSet(GAMES, gameId.getBytes());
		if (rpop == null) {
			return null;
		}
		try {
			return (Game) ObjectUtil.bytesToObject(rpop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
