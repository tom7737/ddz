package com.ddz.ms.service;

import com.ddz.ms.model.Table;
import com.ddz.ms.service.base.BaseService;

public interface SeatService extends BaseService<String,Table> {
	/**
	 * 初始座位
	 */
	void initSeat();
}
