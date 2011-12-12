package models.altermotif.projects;


/**
 * @author svend
 * 
 */
public class RemovedParticpantsResponse {

	private String removedConfirmedParticipants[];

	private String removedUnconfirmedParticipants[];

	public String[] getRemovedConfirmedParticipants() {
		return removedConfirmedParticipants;
	}

	public void setRemovedConfirmedParticipants(String[] removedConfirmedParticipants) {
		this.removedConfirmedParticipants = removedConfirmedParticipants;
	}

	public String[] getRemovedUnconfirmedParticipants() {
		return removedUnconfirmedParticipants;
	}

	public void setRemovedUnconfirmedParticipants(String[] removedUnconfirmedParticipants) {
		this.removedUnconfirmedParticipants = removedUnconfirmedParticipants;
	}

	public RemovedParticpantsResponse(String[] removedConfirmedParticipants, String[] removedUnconfirmedParticipants) {
		super();
		this.removedConfirmedParticipants = removedConfirmedParticipants;
		this.removedUnconfirmedParticipants = removedUnconfirmedParticipants;
	}

	public RemovedParticpantsResponse() {
		super();
	}

}
