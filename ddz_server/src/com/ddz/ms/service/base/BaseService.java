package com.ddz.ms.service.base;

import java.util.List;

public interface BaseService<P, K> {

	Integer add(K obj);

	Integer update(K obj);

	Integer delete(P id);

	List<K> getAll();

	K get(P id);
}
