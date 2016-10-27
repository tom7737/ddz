package com.ddz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 牌堆（一副牌？）*
 * 
 * @author tom
 * @date 2016-10-22
 */
public class Msg implements Serializable {

	private static final long serialVersionUID = -3911518404188182583L;

	private int id;
	private List<Poker> list;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Poker> getList() {
		return list;
	}

	public void setList(List<Poker> list) {
		this.list = list;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Msg [id=" + id + ", list=" + list + "]";
	}

	public Msg(int id, List<Poker> list) {
		super();
		this.id = id;
		this.list = list;
	}

	public Msg() {
		super();
		list =new ArrayList<Poker>();
		for (int i = 3; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				list.add(new Poker(i * 10 + j, i, j, false));
			}
		}
		list.add(new Poker(160, 16, 0, false));//小王
		list.add(new Poker(171, 17, 1, false));//大王
	}
}
