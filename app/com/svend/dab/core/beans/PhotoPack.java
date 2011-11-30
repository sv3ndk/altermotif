package com.svend.dab.core.beans;

import java.util.LinkedList;
import java.util.List;

import com.svend.dab.core.beans.profile.Photo;

public class PhotoPack {

	private final List<Photo> pack;

	public PhotoPack(int size, List<Photo> realList) {
		pack = new LinkedList<Photo>();

		int numberOfPhotosFoundInProfile = 0;

		if (realList != null && !realList.isEmpty()) {
			for (; numberOfPhotosFoundInProfile < size && numberOfPhotosFoundInProfile < realList.size(); numberOfPhotosFoundInProfile++) {
				pack.add(realList.get(numberOfPhotosFoundInProfile));
			}
		}

		// filling up to 10 (if required)
		for (; numberOfPhotosFoundInProfile < size; numberOfPhotosFoundInProfile++) {
			pack.add(new Photo());
		}

	}

	public List<Photo> getPack() {
		return pack;
	}

}
