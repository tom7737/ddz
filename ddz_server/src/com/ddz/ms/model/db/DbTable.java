package com.ddz.ms.model.db;

import java.io.Serializable;
import java.util.List;

import com.ddz.ms.model.Poker;

/**
 * 桌子*
 * 
 * @author tom
 * @date 2016-10-22
 */
public class DbTable implements Serializable/* extends BasicDBObject */{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2489712893L;
	/**
	 * 标识列
	 */
	private String tableId;
	/**
	 * 桌子编号
	 */
	private Integer tableNum;
	/**
	 * 地主牌
	 */
	private List<Poker> lands;
	/**
	 * 桌子状态 0空闲1叫地主中2游戏中
	 */
	private Integer status = 0;
	/**
	 * 地主玩家编号
	 */
	private String landId;
	/**
	 * 底分
	 */
	private Integer initPoints;
	/**
	 * 游戏结果0地主胜利1农民胜利
	 */
	private Integer results;

	public Integer getResults() {
		return results;
	}

	public void setResults(Integer results) {
		this.results = results;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public List<Poker> getLands() {
		return lands;
	}

	public void setLands(List<Poker> lands) {
		this.lands = lands;
	}

	public Integer getTableNum() {
		return tableNum;
	}

	public void setTableNum(Integer tableNum) {
		this.tableNum = tableNum;
	}

	public DbTable() {
		super();
	}

	public String getLandId() {
		return landId;
	}

	public void setLandId(String landId) {
		this.landId = landId;
	}

	public Integer getInitPoints() {
		return initPoints;
	}

	public void setInitPoints(Integer initPoints) {
		if (this.initPoints == null || this.initPoints < initPoints) {
			this.initPoints = initPoints;
		}
	}

}
