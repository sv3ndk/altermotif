package com.svend.dab.core.beans.projects;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Response to a query sent to the project service: what are the removed participants?
 * 
 * @author svend
 * 
 */
public class ParticpantsIdList {

	private List<String> confirmedParticipants = new LinkedList<String>();

	private List<String> unconfirmedParticipants = new LinkedList<String>();
	
	private boolean isCancelProjectLinkEffective;
	

	public void addApplicationUsername(String applicantUsername) {
		unconfirmedParticipants.add(applicantUsername);
	}

	public void addParticipant(String applicantUsername) {
		confirmedParticipants.add(applicantUsername);
	}

	public List<String> getConfirmedParticipants() {
		return confirmedParticipants;
	}

	public void setConfirmedParticipants(List<String> confirmedParticipants) {
		this.confirmedParticipants = confirmedParticipants;
	}

	public List<String> getUnconfirmedParticipants() {
		return unconfirmedParticipants;
	}

	public void setUnconfirmedParticipants(List<String> unconfirmedParticipants) {
		this.unconfirmedParticipants = unconfirmedParticipants;
	}

	public boolean isCancelProjectLinkEffective() {
		return isCancelProjectLinkEffective;
	}

	public void setCancelProjectLinkEffective(boolean isCancelProjectLinkEffective) {
		this.isCancelProjectLinkEffective = isCancelProjectLinkEffective;
	}

}