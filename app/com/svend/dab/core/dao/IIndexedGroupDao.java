package com.svend.dab.core.dao;

import com.svend.dab.core.beans.groups.IndexedGroup;

public interface IIndexedGroupDao {

	void updateIndex(IndexedGroup indexedGroup);

	void ensureIndexOnLocation();

}
