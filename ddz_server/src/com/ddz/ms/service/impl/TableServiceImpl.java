package com.ddz.ms.service.impl;

import java.util.List;

import com.ddz.ms.model.Poker;
import com.ddz.ms.model.TableOld;
import com.ddz.ms.service.TableService;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class TableServiceImpl implements TableService {

	private DBCollection collection = MongoKit.getCollection("talbe");

	@Override
	public Integer add(TableOld obj) {
		BasicDBObject saveObj = new BasicDBObject();
		saveObj.put("tableId", obj.getTableId());
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
	public Integer update(TableOld obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TableOld> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableOld get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
