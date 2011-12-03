package com.svend.dab.eda.events.projects;

import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectMainPhotoUpdated extends Event {

	private String projectId;
	
	private Photo mainPhoto;

	public ProjectMainPhotoUpdated(Photo mainPhoto, String projectId) {
		super();
		this.mainPhoto = mainPhoto;
		this.projectId = projectId;
	}

	public ProjectMainPhotoUpdated() {
		super();
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectMainPhotoUpdatedPropagator");
	}

	public Photo getMainPhoto() {
		return mainPhoto;
	}

	public void setMainPhoto(Photo mainPhoto) {
		this.mainPhoto = mainPhoto;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	
}
