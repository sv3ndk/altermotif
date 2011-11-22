package com.svend.dab.core.beans.profile;




/**
 * @author Svend
 * 
 *         Simple javabean containing labels for displaying languages, in
 *         several langages
 * 
 */
public class Language {


	// name of the language: must match something present in messages.properties
	// (english, french, dutch, spanish, ...)
	private String name;

	int level;

	public Language(String name, int level) {
		super();
		this.name = name;
		this.level = level;
	}

	public Language(Language copied) {
		if (copied != null) {
			this.name = copied.name;
			this.level= copied.level;
		}
	}
	

	public Language() {
		super();
	}

	public String toDescription() {
		return new StringBuffer(name).append(":").append(level) .toString();
	}


	// -------------------------
	//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
