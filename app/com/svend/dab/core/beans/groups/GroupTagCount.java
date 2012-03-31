package com.svend.dab.core.beans.groups;

import org.springframework.data.annotation.Id;

public class GroupTagCount {

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

	public void setValue(int value) {
		this.value = value;
	}

}
