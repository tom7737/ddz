package com.ddz.ms.rdata;

import redis.clients.jedis.Jedis;

import com.ddz.common.redis.RedisClient;
import com.ddz.ms.model.Game;
import com.ddz.ms.util.ObjectUtil;

public class GamesData {
	private static final Jedis jedis = RedisClient.getJedis();

	private static final byte[] GAMES = "games".getBytes();

	private GamesData() {

	}

	public static void hset(String gameId, Game game) {
		try {
			jedis.hset(GAMES, gameId.getBytes(), ObjectUtil.objectToBytes(game));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Game hget(String gameId) {
		byte[] rpop = jedis.hget(GAMES, gameId.getBytes());
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
