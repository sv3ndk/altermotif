package com.svend.dab.core.beans.projects;

import org.springframework.data.annotation.Id;

/**
 * @author svend
 * 
 */
public class TagCount {

	@Id
	private String tag;

	private int value;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int count) {
		this.value = count;
	}

}
