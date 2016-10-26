package com.ddz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间类* 
* 
* @author tom 
* @date 2016-10-23
 */
public class Room  {

	/**
	 * 房间的桌子列表
	 */
	public List<Table> talbes = new ArrayList<Table>();
	
	public void initTables(int counts){
		for (int i = 0; i < counts; i++) {
			talbes.add(new Table());
		}
	}

	
}
