package com.ddz.ms.service;

import com.ddz.ms.model.Game;
import com.ddz.ms.service.base.BaseService;

public interface GameService extends BaseService<String,Game> {
	/**
	 * 记录游戏日志
	 * @param table
	 */
	void saveLog(Game table);
}
