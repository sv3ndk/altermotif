package com.svend.dab.eda.events.projects;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectApplicationEvent extends Event {

	private String applyingUserId;
	private String projectId;
	private String applicationText;
	
	
	public ProjectApplicationEvent() {
		super();
	}

	public ProjectApplicationEvent(String applyingUserId, String projectId, String applicationText) {
		super();
		this.applyingUserId = applyingUserId;
		this.projectId = projectId;
		this.applicationText = applicationText;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectApplicationEventPropagator");
	}

	public String getApplyingUserId() {
		return applyingUserId;
	}

	public void setApplyingUserId(String applyingUserId) {
		this.applyingUserId = applyingUserId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getApplicationText() {
		return applicationText;
	}

	public void setApplicationText(String applicationText) {
		this.applicationText = applicationText;
	}

}
