package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.projects.TagCount;

public interface ITagCountDao {

	public abstract List<TagCount> getMostPopularTags(int maxResults);
	
	
}
