package com.ddz.ms.service.impl;

import java.util.List;
import java.util.Map;

import com.ddz.ms.model.Player;
import com.ddz.ms.model.Poker;
import com.ddz.ms.model.Game;
import com.ddz.ms.service.GameService;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class GameServiceImpl implements GameService {

	private DBCollection collection = MongoKit.getCollection("game");

	@Override
	public Integer add(Game obj) {
		BasicDBObject saveObj = new BasicDBObject();
		saveObj.put("gameId", obj.getGameId());
		saveObj.put("tableNum", obj.getTableNum());
		saveObj.put("lands", Poker.pokerFormatLtoI(obj.getLands()));
		saveObj.put("status", obj.getStatus());
		saveObj.put("landId", obj.getLandId());
		saveObj.put("initPoints", obj.getInitPoints());
		saveObj.put("results", obj.getResults());
		// ***************
		// saveObj.put("players", saveObj.getPlayers());
		// saveObj.put("folds", saveObj.getResults());
		// saveObj.put("actionPlayerId", saveObj.getActionPlayerId());
		// saveObj.put("landvs", saveObj.getLandvs());
		// saveObj.put("callPalyer", saveObj.getCallPalyer());
		// saveObj.put("outPokerLog", saveObj.getOutPokerLog());
		WriteResult save = collection.save(saveObj);
		return save.getN();
	}

	@Override
	public Integer update(Game obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Game> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Game get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveLog(Game table) {
		// 保存牌局信息
		BasicDBObject saveObj = new BasicDBObject();
		saveObj.put("gameId", table.getGameId());
		saveObj.put("tableNum", table.getTableNum());
		saveObj.put("lands", Poker.pokerFormatLtoI(table.getLands()));
		saveObj.put("status", table.getStatus());
		saveObj.put("landId", table.getLandId());
		saveObj.put("initPoints", table.getInitPoints());
		saveObj.put("results", table.getResults());
		// ***************
		List<Player> players = table.getPlayers();
		BasicDBList db_players = new BasicDBList();
		for (Player player : players) {
			BasicDBObject db_player = new BasicDBObject();
			db_player.put("userId", player.getUserId());
			db_player.put("pokers", Poker.pokerFormatLtoI( player.getPokers()));
			db_player.put("island", player.isIsland());
			db_players.add(db_player);
		}
		saveObj.put("players", db_players);
		saveObj.put("folds", Poker.pokerFormatLtoI(table.getFolds()));
		// saveObj.put("actionPlayerId", table.getActionPlayerId());
		List<Map<String, Integer>> landvs = table.getLandvLog();
		BasicDBList db_landvLog  = new BasicDBList();
		for (Map<String, Integer> map : landvs) {
			BasicDBObject db_landvs = new BasicDBObject();
			String userId = map.keySet().iterator().next();
			db_landvs.put("userId",userId);
			db_landvs.put("landv",map.get(userId));
			db_landvLog.add(db_landvs);
		}
		saveObj.put("landvs", db_landvLog);
		 List<Map<String, Integer[]>> outPokerLog = table.getOutPokerLog();
		BasicDBList db_outPokerLog  = new BasicDBList();
		for (Map<String, Integer[]> map : outPokerLog) {
			BasicDBObject db_outPoker = new BasicDBObject();
			String userId = map.keySet().iterator().next();
			db_outPoker.put("userId",userId);
			db_outPoker.put("pokers",map.get(userId));
			db_outPokerLog.add(db_outPoker);
		}
		saveObj.put("outPokerLog", db_outPokerLog);
		collection.save(saveObj);
	}

}
