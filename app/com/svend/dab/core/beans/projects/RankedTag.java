package com.svend.dab.core.beans.projects;

/**
 * 
 * 
 * @author svend
 * 
 */
public class RankedTag {

	private String tag;

	// 0 is high rank (very frequent tag), 4 is low rank (very infrequent tag)
	private int rank;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public RankedTag() {
		super();
	}

	public RankedTag(String tag, int rank) {
		super();
		this.tag = tag;
		this.rank = rank;
	}

}
