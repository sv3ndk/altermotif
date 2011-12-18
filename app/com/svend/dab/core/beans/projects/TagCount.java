package com.svend.dab.core.beans.projects;

import org.springframework.data.annotation.Id;

/**
 * @author svend
 * 
 */
public class TagCount {

	@Id
	private String tag;

	private int count;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
