package com.svend.dab.core.beans.projects;

import java.util.LinkedList;
import java.util.List;

public class ParticipantList {

	private List<Participant> confirmedParticipants = new LinkedList<Participant>();

	private List<Participant> unconfirmedParticipants = new LinkedList<Participant>();
	
	public void addApplication(Participant applicant) {
		unconfirmedParticipants.add(applicant);
	}

	public void addParticipant(Participant participant) {
		confirmedParticipants.add(participant);
	}
	

	public List<Participant> getConfirmedParticipants() {
		return confirmedParticipants;
	}

	public void setConfirmedParticipants(List<Participant> confirmedParticipants) {
		this.confirmedParticipants = confirmedParticipants;
	}

	public List<Participant> getUnconfirmedParticipants() {
		return unconfirmedParticipants;
	}

	public void setUnconfirmedParticipants(List<Participant> unconfirmedParticipants) {
		this.unconfirmedParticipants = unconfirmedParticipants;
	}
	
	

}
