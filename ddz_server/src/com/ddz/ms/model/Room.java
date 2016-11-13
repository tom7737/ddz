package com.ddz.ms.model;

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
	public List<TableOld> talbes = new ArrayList<TableOld>();
	
	public void initTables(int counts){
		for (int i = 0; i < counts; i++) {
			talbes.add(new TableOld());
		}
	}

	
}
