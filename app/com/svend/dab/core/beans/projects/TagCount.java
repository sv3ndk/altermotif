package com.svend.dab.core.beans.projects;

import org.springframework.data.annotation.Id;

/**
 * 
 * Tag count for the {@link Project}, used to be able to display the tag size correctly in the tag cloud showed in the project search page
 * 
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
