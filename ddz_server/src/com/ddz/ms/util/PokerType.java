package com.ddz.ms.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.ddz.ms.model.Poker;
/**
 * 牌型帮助类
 * 提供判断牌型的方法和所有牌型常量
 * @author tom
 *
 */
public class PokerType {

	public final static String DANZHANG = "DANZHANG";// 单张
	public final static String WANGZHA = "WANGZHA";// 王炸
	public final static String DUIZI = "DUIZI";// 对子
	public final static String SANGETOU = "SANGETOU";// 三张
	public final static String SANDAIYI = "SANDAIYI";// 三带一
	public final static String SANDAIYIDUI = "SANDAIYIDUI";// 三带一对
	public final static String ZHADAN = "ZHADAN";// 炸弹
	public final static String SHUNZI = "SHUNZI";// 顺子
	public final static String LIANDUI = "LIANDUI";// 连对
	public final static String SANSHUN = "SANSHUN";// 三顺
	public final static String FEIJI = "FEIJI";// 飞机
	public final static String SHUANGFEI = "SHUANGFEI";// 双飞
	public final static String SIDAIER = "SIDAIER";// 四带二
	public final static String SIDAIERDUI = "SIDAIERDUI";// 四带二对
	public final static String ERROR = "ERROR";// 错误
	/**
	 * 判断牌型
	 * @param list
	 * @return
	 */
	public static String pokerType(List<Poker> list) {
		int listCnt = list.size();
		list = Poker.sortById(list);
		if (listCnt == 1) {
			// 单张
			return DANZHANG;
		} else if (listCnt == 2) {
			if (isSame(list, listCnt)) {// 对子
				return DUIZI;
			}
			if (isWangZha(list)) {// 王炸
				return WANGZHA;
			}
			return ERROR;
		} else if (listCnt == 3) {
			if (isSame(list, listCnt)) {// 三个头（三张不带）
				return SANGETOU;
			}
			return ERROR;
		} else if (listCnt == 4) {
			if (isSame(list, listCnt)) {// 炸弹
				return ZHADAN;
			}
			if (isSanDaiYi(list)) {// 三带一
				return SANDAIYI;
			}
			return ERROR;
		} else if (listCnt >= 5) {
			if (isShunZi(list)) {// 顺子
				return SHUNZI;
			} else if (isSanDaiYiDui(list)) {// 三带一对
				return SANDAIYIDUI;
			} else if (isLianDui(list)) { // 连对
				return LIANDUI;
			} else if (isSanShun(list)) {// 三顺
				return SANSHUN;
			} else if (isFeiJi(list)) {// 飞机（三带一顺）
				return FEIJI;
			} else if (isShuangFei(list)) {// 双飞（三带二顺）
				return SHUANGFEI;
			} else if (isSiDaiEr(list)) {// 四带二
				return SIDAIER;
			} else if (isSiDaiErDui(list)) {// 四带二对
				return SIDAIERDUI;
			}
		}
		return ERROR;
	}

	private static boolean isWangZha(List<Poker> list) {
		if ((list.get(0).getId() == 171 && list.get(1).getId() == 160)
				|| list.get(0).getId() == 160 && list.get(1).getId() == 171) {
			return true;
		}
		return false;
	}

	/**
	 * 判断list内扑克是否相同
	 * 
	 * @param list
	 * @param i
	 * @return
	 */
	private static boolean isSame(List<Poker> list, int i) {
		for (int j = 0; j < i - 1; j++) {
			int a = list.get(j).getName();
			int b = list.get(j + 1).getName();
			if (a != b) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是三带一
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isSanDaiYi(List<Poker> list) {
		List<Poker> temp = new ArrayList<Poker>();
		temp.addAll(list);
		if (isSame(temp, 3)) {
			return true;
		}
		temp.remove(0);
		if (isSame(temp, 3)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是三带一对
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isSanDaiYiDui(List<Poker> list) {
		if (list == null || list.size() != 5) {
			return false;
		}
		int name1 = 0;
		int count1 = 0;
		int name2 = 0;
		int count2 = 0;
		for (Poker poker : list) {
			int name = poker.getName();
			if (name1 == 0) {
				name1 = name;
				count1++;
			} else if (name1 == name) {
				count1++;
			} else if (name2 == 0) {
				name2 = name;
				count2++;
			} else if (name2 == name) {
				count2++;
			} else {
				return false;
			}
		}
		if ((count1 == 2 && count2 == 3) || (count1 == 3 && count2 == 2)) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		List<Poker> ppk = new ArrayList<Poker>();
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			ppk.add(new Poker((r.nextInt(15) + 3) * 10 + r.nextInt(4)));
		}
		// for (Poker poker : ppk) {
		// System.out.println(poker);
		// }
		long l1 = System.nanoTime();
		// 根据name排序
		// Collections.sort(ppk, new Comparator<Poker>() {
		// public int compare(Poker o1, Poker o2) {
		// return o2.getName() - o1.getName();
		// }
		// });
		Poker.sortById(ppk);
		long l2 = System.nanoTime();
		System.out.println(l2 - l1);
		for (Poker poker : ppk) {
			System.out.println(poker);
		}
	}

	/**
	 * 判断是否是顺子
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isShunZi(List<Poker> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			int a = list.get(i).getName();
			if (a == 16 || a == 17) {// 2和王
				return false;
			}
			int b = list.get(i + 1).getName();
			if (a - b != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是连对
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isLianDui(List<Poker> list) {
		int size = list.size();
		if (size < 6 && size % 2 != 0) {
			return false;
		}
		for (int i = 0; i < size; i = i + 2) {
			int a = list.get(i).getName();
			if (a == 16 || a == 17) {// 2和王
				return false;
			}
			int b = list.get(i + 1).getName();
			if (a != b) {
				return false;
			}
		}
		for (int i = 0; i < size; i = i + 2) {
			int a = list.get(i).getName();
			int b = list.get(i + 1).getName();
			if (a - b != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是三顺
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isSanShun(List<Poker> list) {
		int size = list.size();
		if (size < 9 && size % 3 != 0) {
			return false;
		}
		for (int i = 0; i < size; i = i + 3) {
			int a = list.get(i).getName();
			if (a == 16 || a == 17) {// 2和王
				return false;
			}
			int b = list.get(i + 1).getName();
			int c = list.get(i + 2).getName();
			if (a != b || b != c) {
				return false;
			}
		}
		for (int i = 0; i < size; i = i + 3) {
			int a = list.get(i).getName();
			int b = list.get(i + 3).getName();
			if (a - b != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是飞机(三带一顺)
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isFeiJi(List<Poker> list) {
		int size = list.size();
		if (size < 8 && size % 4 != 0) {// 数量
			return false;
		}
		List<Integer> cnt = feiCnt(list);
		if (cnt.size() != size / 4)// 三张数量
			return false;
		for (int i = 0; i < cnt.size() - 1; i++) {// 是否连续
			if (cnt.get(i).intValue() != cnt.get(i + 1).intValue())
				return false;
		}
		return true;
	}

	/**
	 * 判断是不是双飞（三带二顺）
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isShuangFei(List<Poker> list) {
		int size = list.size();
		if (size < 10 && size % 5 != 0) {// 数量
			return false;
		}
		int count = size / 5;
		Map<Integer, Integer> kv = new HashMap<Integer, Integer>();
		for (Poker poker : list) {
			Integer name = poker.getName();
			if (kv.containsKey(name)) {
				kv.put(name, kv.get(name) + 1);
			} else {
				kv.put(name, 1);
			}
		}
		if (kv.size() != count * 2) {// 组合数量
			return false;
		}
		Set<Integer> keySet = kv.keySet();
		int count2 = 0;
		int count3 = 0;
		List<Integer> cnt = new ArrayList<Integer>();
		for (Integer integer : keySet) {
			if (3 == kv.get(integer).intValue()) {
				count3++;
				cnt.add(integer);
			} else if (2 == kv.get(integer).intValue()) {
				count2++;
			}
		}
		if (count2 != count || count3 != count) {// 组合数量
			return false;
		}
		for (int i = 0; i < cnt.size() - 1; i++) {// 是否连续
			if (cnt.get(i).intValue() != cnt.get(i + 1).intValue())
				return false;
		}
		return true;
	}

	/**
	 * 判断三个头有几个
	 * 
	 * @param list
	 * @return
	 */
	private static List<Integer> feiCnt(List<Poker> list) {
		List<Integer> cnt = new ArrayList<Integer>();
		for (int i = 0; i < list.size() - 2; i++) {
			int a = list.get(i).getName();
			int b = list.get(i + 1).getName();
			int c = list.get(i + 2).getName();
			if (a == b && a == c) {
				cnt.add(a);
				i = i + 2;
			}
		}
		return cnt;
	}

	/**
	 * 判断是否是四带二
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isSiDaiEr(List<Poker> list) {
		if (list.size() != 6) {// 数量
			return false;
		}
		for (int i = 0; i < list.size() - 3; i++) {
			int a = list.get(i).getName();
			int b = list.get(i + 1).getName();
			if (a != b) {
				continue;
			}
			int c = list.get(i + 2).getName();
			if (b != c)
				continue;
			int d = list.get(i + 3).getName();
			if (c != d)
				continue;
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是四带二对
	 * 
	 * @param list
	 * @return
	 */
	private static boolean isSiDaiErDui(List<Poker> list) {
		if (list.size() != 8) {// 数量
			return false;
		}
		int zha = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			int a = list.get(i).getName();
			if (a == 17)
				return false;
			int b = list.get(i + 1).getName();
			if (a != b)
				return false;

			if (i == list.size() - 2 && zha == 1)
				return true;
			if (i == list.size() - 2 && zha == 0)
				return false;
			int c = list.get(i + 2).getName();
			if (b != c) {
				i++;
				continue;
			}
			int d = list.get(i + 3).getName();
			if (c != d)
				return false;
			if (i == list.size() - 4)
				return true;
			zha++;
			i = i + 3;
		}
		return true;
	}

}
