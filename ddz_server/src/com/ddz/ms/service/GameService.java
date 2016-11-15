package com.ddz.ms.service;

import com.ddz.ms.model.Game;
import com.ddz.ms.service.base.BaseService;

public interface GameService extends BaseService<String,Game> {

	void saveLog(Game table);
}
