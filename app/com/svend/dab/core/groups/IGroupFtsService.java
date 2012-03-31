package com.svend.dab.core.groups;

public interface IGroupFtsService {

	public abstract void updateGroupIndex(String groupId, boolean immediate);

	public abstract void ensureIndexOnLocation();

}