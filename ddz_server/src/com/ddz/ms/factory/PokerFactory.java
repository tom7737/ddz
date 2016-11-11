package com.ddz.ms.factory;

import java.util.ArrayList;
import java.util.List;

import com.ddz.ms.model.Poker;

/**
 * 扑克工厂类*
 * 
 * @author tom
 * @date 2016-10-22
 */
public class PokerFactory {

	private PokerFactory() {
	}

	/**
	 * 生产一副牌
	 * 
	 * @return
	 */
	public static List<Poker> getInstance() {
		List<Poker> list = new ArrayList<Poker>();
		for (int i = 3; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				list.add(new Poker(i * 10 + j, i, j, false));
			}
		}
		list.add(new Poker(160, 16, 0, false));// 小王
		list.add(new Poker(171, 17, 1, false));// 大王
		return list;
	}
}
