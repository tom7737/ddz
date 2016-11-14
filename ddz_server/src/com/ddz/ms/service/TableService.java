package com.ddz.ms.service;

import com.ddz.ms.model.Table;
import com.ddz.ms.service.base.BaseService;

public interface TableService extends BaseService<String,Table> {

	void saveLog(Table table);
}
