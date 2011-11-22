package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.data.annotation.Transient;

import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant.ROLE;

/**
 * @author Svend
 * 
 */
public class Project {

	public enum PROJECT_VISIBILITY {
		everybody, loggedin, members, admins, owner;
	}

	public enum STATUS {
		started("projectStatusStarted"), cancelled("projectStatusCancelled"), done("projectStatusDone");

		private final String label;

		private STATUS(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}

	}

	private String id;

	private ProjectData pdata = new ProjectData();

	private List<Participant> participants;

	private List<Photo> photos;
	
	private List<String> links;
	
	private STATUS status;
	
	// cachedData
	
	@Transient
	private Participant cachedInitiator;
	
	@Transient
	private List<Participant> cachedConfirmedParticipants;

	@Transient
	private List<Participant> cachedUnconfirmedParticipants;
	
	// TODO
	@Transient
	private String noTasks = "(todo...)";

	@Transient
	private String noAssets = "(todo...)";

	@Transient
	private String noThemes = "(todo...)";

	@Transient
	private String noTags = "(todo...)";

	private static Logger logger = Logger.getLogger(Project.class.getName());
	
	// --------------------
	//
	
	public void addParticipant(ROLE role, UserProfile user) {
		if (participants == null) {
			participants = new LinkedList<Participant>();
		}

		// TODO: we could make sure here that nobody tries to set a creator after the creation...

		participants.add(new Participant(role, user));
	}

	/**
	 * @return
	 */
	public Photo getMainPhoto() {

		if (photos != null && !photos.isEmpty()) {
			return photos.get(0);
		}

		return new Photo();
	}
	
	
	public Participant getInitiator() {
		
		if (participants == null) {
			// this should never happen: we should always have one participant: the initiator!
			return null;
		}

		if (cachedInitiator == null) {
			for (Participant participant : participants) {
				if (participant.getRole() == ROLE.initiator) {
					cachedInitiator = participant;
					break;
				}
			}
		}
		
		return cachedInitiator;
	}
	
	public int getNumberOfParticipants() {
		if (getConfirmedParticipants() == null) {
			return 0;
		}
		
		return getConfirmedParticipants().size(); 
	}
	
	public int getNumberOfApplications() {
		if (getUnconfirmedParticipants() == null) {
			return 0;
		}
		
		return getUnconfirmedParticipants().size(); 
	}
	
	

	public void generatePhotoLinks(Date expirationdate) {
		if (getMainPhoto() != null) {
			getMainPhoto().generatePresignedLinks(expirationdate, false, true);
		}
		
		if (participants != null) {
			for (Participant participant : participants) {
				participant.generatePhotoLinks(expirationdate);
			}
		}
	}
	
	
	public List<Participant> getConfirmedParticipants() {
		if (cachedConfirmedParticipants == null) {
			categorizeParticipants();
		}
		
		
		return cachedConfirmedParticipants;
	}

	
	public List<Participant> getUnconfirmedParticipants() {
		if (cachedUnconfirmedParticipants == null) {
			categorizeParticipants();
		}
		return cachedUnconfirmedParticipants;
	}
	
	
	protected void categorizeParticipants() {
		cachedConfirmedParticipants = new LinkedList<Participant>();
		cachedUnconfirmedParticipants = new LinkedList<Participant>();
		
		for (Participant participant : participants)  {
			if (participant.isAccepted()) {
				cachedConfirmedParticipants.add(participant);
			} else {
				cachedUnconfirmedParticipants.add(participant);
			}
		}
	}

	/**
	 * @param loggedInUserProfileId
	 * @return true if this user is already part of this project or has applied for it
	 */
	public boolean isUserAlreadyMemberOrApplicant(String loggedInUserProfileId) {
		if (participants == null || loggedInUserProfileId == null) {
			return false;
		}
		
		for (Participant participant : participants) {
			if (loggedInUserProfileId.equals(participant.getUser().getUserName())) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// ------------------------------------------------------------------------------
	//

	public ProjectData getPdata() {
		return pdata;
	}

	public void setPdata(ProjectData pdata) {
		this.pdata = pdata;
	}

	@Override
	public String toString() {
		return "pdata = " + pdata + ", links = " + links.toArray();
	}

	public String getNoTasks() {
		return noTasks;
	}

	public void setNoTasks(String noTasks) {
		this.noTasks = noTasks;
	}

	public String getNoAssets() {
		return noAssets;
	}

	public void setNoAssets(String noAssets) {
		this.noAssets = noAssets;
	}

	public String getNoThemes() {
		return noThemes;
	}

	public void setNoThemes(String noThemes) {
		this.noThemes = noThemes;
	}

	public String getNoTags() {
		return noTags;
	}

	public void setNoTags(String noTags) {
		this.noTags = noTags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public List<String> getLinks() {
		return links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}





}
