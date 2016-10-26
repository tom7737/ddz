package com.ddz.model;

import java.io.Serializable;
/**
 * 扑克类* 
* 
* @author tom 
* @date 2016-10-22
 */
public class Poker implements Serializable{

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
	public Poker() {
		super();
	}
	public String toString() {
		return "Poker [id=" + id + ", name=" + name + ", color=" + color
				+ ", isOut=" + isOut + "]"+"\n";
	}
	String[] names = { "", "", "", "3", "4", "5", "6", "7", "8", "9", "10",
			"J", "Q", "K", "A", "2", "X", "D" };
	String[] colors = { "黑", "红", "梅", "方" };
	public String show(){
		return colors[color]+names[name];
	}
	
	
}
