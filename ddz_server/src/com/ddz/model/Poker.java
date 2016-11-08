package com.ddz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 扑克类*
 * 
 * @author tom
 * @date 2016-10-22
 */
public class Poker implements Serializable {

	private static final long serialVersionUID = 489483938802054094L;

	private int id;
	private int name;
	private int color;
	private boolean isOut;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Poker(int id, int name, int color, boolean isOut) {
		super();
		this.id = id;
		this.name = name;
		this.color = color;
		this.isOut = isOut;
	}

	public Poker(int id) {
		super();
		this.id = id;
		this.name = id / 10;
		this.color = id % 10;
		this.isOut = false;
	}

	public Poker() {
		super();
	}

	public String toString() {
		return "Poker [id=" + id + ", name=" + name + ", color=" + color
				+ ", isOut=" + isOut + "]" + "\n";
	}

	String[] names = { "", "", "", "3", "4", "5", "6", "7", "8", "9", "10",
			"J", "Q", "K", "A", "2", "X", "D" };
	String[] colors = { "黑", "红", "梅", "方" };

	public String show() {
		return colors[color] + names[name];
	}

	/**
	 * 将pokerId列表转换成poker对象列表
	 * 
	 * @param pokers
	 * @return
	 */
	public static List<Poker> pokerFormatItoL(Integer[] pokerIds) {
		List<Poker> returnv = new ArrayList<Poker>();
		for (Integer pokerid : pokerIds) {
			returnv.add(new Poker(pokerid, pokerid / 10, pokerid % 10, false));
		}
		return returnv;
	}

	/**
	 * 将poker对象列表转换成pokerId列表
	 * 
	 * @param pokers
	 * @return
	 */
	public static Integer[] pokerFormatLtoI(List<Poker> pokers) {
		Integer[] rv = new Integer[pokers.size()];
		for (int i = 0; i < rv.length; i++) {
			rv[i] = pokers.get(i).getId();
		}
		return rv;
	}

	/**
	 * 将扑克列表根据Id排序
	 * 
	 * @param list
	 * @return
	 */
	public static List<Poker> sortById(List<Poker> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i; j < list.size(); j++) {
				int a = list.get(i).getId();
				int b = list.get(j).getId();
				if (a < b) {
					Poker temp = list.get(i);
					list.set(i, list.get(j));
					list.set(j, temp);
				}
			}
		}
		return list;
	}

	/**
	 * 将扑克列表根据Id排序
	 * 
	 * @param list
	 * @return
	 */
	public static Integer[] sortById(Integer[] list) {
		for (int i = 0; i < list.length; i++) {
			for (int j = i; j < list.length; j++) {
				if (list[i] < list[j]) {
					int temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
		return list;
	}
}
