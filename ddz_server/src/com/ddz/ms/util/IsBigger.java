package com.ddz.ms.util;

import java.util.List;
import java.util.Map;

import com.ddz.ms.model.Poker;

/**
 * 判断出牌是否符合规则
 * 
 * @author tom
 * 
 */
public class IsBigger {
	/**
	 * 判断出的牌是否符合规则
	 * 
	 * @param outPokerLog
	 *            出牌记录
	 * @param pokerIds
	 *            当前要出的牌
	 * @return
	 */
	public static boolean isBigger(List<Map<String, Integer[]>> outPokerLog,
			Integer[] pokerIds) {
		List<Poker> leftList = null;// 上家出的牌
		List<Poker> rightList = null;// 下家出的牌
		List<Poker> choose = null;// 本家出的牌
		choose = Poker.pokerFormatItoL(pokerIds);
		// if (IsTruePoker.ERROR.equals(IsTruePoker.isTruePoker(Poker
		// .pokerFormatItoL(pokerIds)))) {//错误牌型
		// return false;
		// }
		if (outPokerLog != null && outPokerLog.size() > 0) {
			Map<String, Integer[]> map = outPokerLog
					.get(outPokerLog.size() - 1);
			Integer[] integers = map.get(map.keySet().iterator().next());
			if (integers != null && integers.length > 0)
				leftList = Poker.pokerFormatItoL(integers);
			if (outPokerLog.size() > 1) {
				Map<String, Integer[]> map2 = outPokerLog.get(outPokerLog
						.size() - 2);
				Integer[] integers2 = map2.get(map2.keySet().iterator().next());
				if (integers2 != null && integers2.length > 0)
					rightList = Poker.pokerFormatItoL(integers2);
			}
		} else {// 第一把出牌
			return true;
		}

		// 如果上家不要
		if (leftList == null || leftList.size() == 0) {
			if (rightList == null || rightList.size() == 0) {
				// 两家都不要
				return true;
			} else {
				if (isRealBigger(rightList, choose)) {
					return true;
				}
				return false;
			}
		} else {
			if (isRealBigger(leftList, choose)) {
				return true;
			}
			return false;
		}

	}

	private static boolean isRealBigger(List<Poker> leftList, List<Poker> choose) {
		// 首先判断牌型是不是一样
		String leftPaiXing = PokerType.pokerType(leftList);
		String myPaiXing = PokerType.pokerType(choose);
		if (leftPaiXing.equals(myPaiXing)) {// 根据牌型来判断大小
			if (PokerType.DANZHANG.equals(leftPaiXing)
					&& isBiggerLast(leftList, choose)) {// 单张
				return true;
			} else if (PokerType.DUIZI.equals(leftPaiXing)
					&& isBiggerLast(leftList, choose)) {// 对子
				return true;
			} else if (PokerType.SANGETOU.equals(leftPaiXing)
					&& isBiggerLast(leftList, choose)) {// 三张
				return true;
			} else if (PokerType.SANDAIYI.equals(leftPaiXing)
					&& isBiggerSan(leftList, choose)) {// 三带一
				return true;
			} else if (PokerType.SANDAIYIDUI.equals(leftPaiXing)
					&& isBiggerSan(leftList, choose)) {// 三带一对
				return true;
			} else if (PokerType.ZHADAN.equals(leftPaiXing)
					&& isBiggerLast(leftList, choose)) {// 炸弹
				return true;
			} else if (PokerType.SHUNZI.equals(leftPaiXing)
					&& isBiggerLast(leftList, choose)) {// 顺子
				return true;
			} else if (PokerType.LIANDUI.equals(leftPaiXing)
					&& isBiggerLast(leftList, choose)) {// 连对
				return true;
			} else if (PokerType.SANSHUN.equals(leftPaiXing)
					&& isBiggerSan(leftList, choose)) { // 三顺
				return true;
			} else if (PokerType.FEIJI.equals(leftPaiXing)
					&& isBiggerSan(leftList, choose)) {// 飞机（三带一顺）

				return true;
			} else if (PokerType.SHUANGFEI.equals(leftPaiXing)
					&& isBiggerSan(leftList, choose)) {// 双飞（三带二顺）

				return true;
			} else if (PokerType.SIDAIER.equals(leftPaiXing)
					&& isBiggerSi(leftList, choose)) {// 四带二

				return true;
			} else if (PokerType.SIDAIERDUI.equals(leftPaiXing)
					&& isBiggerSi(leftList, choose)) {// 四带二对

				return true;
			}
		} else if (PokerType.WANGZHA.equals(myPaiXing)) {
			// 判断是不是王炸
			return true;
		} else if (PokerType.ZHADAN.equals(myPaiXing)) {
			// 判断是不是炸弹
			return true;
		}
		return false;
	}

	/**
	 * 通过最后一张牌来判断大小
	 * 
	 * @param list
	 * @param choose
	 * @return
	 */
	private static boolean isBiggerLast(List<Poker> list, List<Poker> choose) {
		if (list.get(list.size() - 1).getName() < choose.get(choose.size() - 1)
				.getName()) {
			return true;
		}
		return false;
	}

	/**
	 * 3带N判断大小专用
	 * 
	 * @param list
	 * @param choose
	 * @return
	 */
	private static boolean isBiggerSan(List<Poker> list, List<Poker> choose) {
		int a = san(list);
		int b = san(choose);
		if (a == -1 || b == -1) {
			return false;
		}
		if (b > a) {
			return true;
		}
		return false;
	}

	/**
	 * 获取一手牌中，三张一样牌的大小。经过排序后的三顺，飞机，双飞会获取三张牌最大的数字
	 * 
	 * @param list
	 *            3444
	 * @return 4
	 */
	private static int san(List<Poker> list) {
		for (int i = 0; i < list.size() - 2; i++) {
			int a = list.get(i).getName();
			int b = list.get(i + 1).getName();
			int c = list.get(i + 2).getName();
			if (a == b && a == c) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * 4带N判断大小专用
	 * 
	 * @param list
	 * @param choose
	 * @return
	 */
	private static boolean isBiggerSi(List<Poker> list, List<Poker> choose) {
		int a = si(list);
		int b = si(choose);
		if (a == -1 || b == -1) {
			return false;
		}
		if (b > a) {
			return true;
		}
		return false;
	}

	/**
	 * 获取一手牌中，四张一样牌的大小。
	 * 
	 * @param list
	 *            344445
	 * @return 4
	 */
	private static int si(List<Poker> list) {
		for (int i = 0; i < list.size() - 3; i++) {
			int a = list.get(i).getName();
			int b = list.get(i + 1).getName();
			int c = list.get(i + 2).getName();
			int d = list.get(i + 3).getName();
			if (a == b && a == c && a == d) {
				return a;
			}
		}
		return -1;
	}
}
