package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Transient;

import com.svend.dab.core.beans.PhotoPack;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Task.TASK_STATUS;

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
	
	public static final int MAX_NUMBER_OF_PHOTOS = 20;

	private String id;

	private ProjectData pdata = new ProjectData();

	private List<Participant> participants;
	
	private int mainPhotoIndex;
	
	private List<Photo> photos;

	private Set<String> links;

	private Set<String> tags;
	
	private Set<SelectedTheme> themes;

	private STATUS status;
	
	private Set<Task> tasks;
	private Set<Asset> assets;
	

	// ///////////////////////////
	// cachedData


	@Transient
	private Participant cachedInitiator;

	@Transient
	private Participant cachedProposedInitiator;

	@Transient
	private List<Participant> cachedConfirmedParticipants;
	
	@Transient
	private List<Participant> cachedConfirmedActiveParticipants;

	@Transient
	private List<UserSummary> cachedConfirmedActiveParticipantsSummaries;

	@Transient
	private List<Participant> cachedUnconfirmedActiveParticipants;

	// arrays of 20 links to the photos of the user (no matter how many actually present in DB)
	@Transient
	private PhotoPack cachedPhotosPack20;

	@Transient
	// just to make sure we do not resolve the user roles everytime 
	private HashMap<String, ROLE> cachedUserRoles = new HashMap<String, Participant.ROLE>();
	
	// TODO
	@Transient
	private String noTasks = "(todo...)";

	@Transient
	private String noAssets = "(todo...)";

	// --------------------
	//

	public void addParticipant(ROLE role, UserProfile user, String applicationText) {
		if (participants == null) {
			participants = new LinkedList<Participant>();
		}

		// TODO: we could make sure here that nobody tries to set a creator after the creation...

		participants.add(new Participant(role, user, applicationText));
	}
	
	// ------------------------------------------------
	// photos

	/**
	 * @return
	 */
	public Photo getMainPhoto() {

		if (photos != null && !photos.isEmpty()) {
			return photos.get(mainPhotoIndex);
		}

		return new Photo();
	}

	public List<Photo> getPhotosPack20() {
		if (cachedPhotosPack20 == null) {
			synchronized (this) {
				if (cachedPhotosPack20 == null) {
					cachedPhotosPack20 = new  PhotoPack(20, photos);
				}
			}
		}

		return cachedPhotosPack20.getPack();
	}
	
	
	public boolean isPhotoPackFull() {
		return photos != null && photos.size() > MAX_NUMBER_OF_PHOTOS;
	}

	public String getPhotoS3RootFolder() {
		return "/projects/" + id + "/photos/";
	}

	public String getThumbsS3RootFolder() {
		return "/projects/" + id + "/thumbs/";
	}
	
	public boolean isPhotoPackEmpty() {
		return photos == null || photos.size() == 0;
	}
	
	public Photo getPhoto(int photoIdx) {
		if (isPhotoPackEmpty() || photos.size() <= photoIdx) {
			return null;
		}
		return photos.get(photoIdx);
	}


	//_--------------------------------------
	// roles


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

	/**
	 * @return this method often returns null: it returns the {@link Participant} of a member of this projcet to who the owner has proposed to transfer the ownership (this only exist until this user either accept or refuse)
	 */
	public Participant getProposedInitiator() {
		
		if (participants == null) {
			// this should never happen: we should always have one participant: the initiator!
			return null;
		}
		
		if (cachedProposedInitiator == null) {
			for (Participant participant : participants) {
				if (participant.isOwnershipProposed()) {
					cachedProposedInitiator = participant;
					break;
				}
			}
		}
		return cachedProposedInitiator;
	}
	
	/**
	 * @param user
	 * @return the {@link ROLE} that this user plays in this project, or null of this user is not part of this project
	 */
	public ROLE findRoleOfUser(String user) {

		if (user == null) {
			return null;
		}
		
		if (cachedUserRoles.containsKey(user)) {
			return cachedUserRoles.get(user);
		}

		for (Participant participant : getConfirmedParticipants()) {
			if (user.equals(participant.getUser().getUserName())) {
				cachedUserRoles.put(user, participant.getRole());
				return participant.getRole();
			}
		}
		return null;
	}

	public int getNumberOfConfirmedParticipants() {
		if (getConfirmedParticipants() == null) {
			return 0;
		}
		return getConfirmedParticipants().size();
	}

	public int getNumberOfApplications() {
		if (getUnconfirmedActiveParticipants() == null) {
			return 0;
		}
		return getUnconfirmedActiveParticipants().size();
	}

	public List<Participant> getConfirmedParticipants() {
		if (cachedConfirmedParticipants == null) {
			categorizeParticipants();
		}

		return cachedConfirmedParticipants;
	}
	
	public List<Participant> getConfirmedActiveParticipants() {
		if (cachedConfirmedActiveParticipants == null) {
			categorizeParticipants();
		}
		return cachedConfirmedActiveParticipants;
	}
	
	public List<UserSummary> getConfirmedActiveParticipantsSummaries() {
		if (cachedConfirmedActiveParticipants == null) {
			categorizeParticipants();
		}
		return cachedConfirmedActiveParticipantsSummaries;
	}


	public List<Participant> getUnconfirmedActiveParticipants() {
		if (cachedUnconfirmedActiveParticipants == null) {
			categorizeParticipants();
		}
		return cachedUnconfirmedActiveParticipants;
	}

	protected void categorizeParticipants() {
		cachedConfirmedParticipants = new LinkedList<Participant>();
		cachedConfirmedActiveParticipants = new LinkedList<Participant>();
		cachedUnconfirmedActiveParticipants = new LinkedList<Participant>();
		cachedConfirmedActiveParticipantsSummaries = new LinkedList<UserSummary>();

		for (Participant participant : participants) {
			if (participant.isAccepted()) {
				cachedConfirmedParticipants.add(participant);
				if (participant.isAccepted()) {
					cachedConfirmedActiveParticipants.add(participant);
					cachedConfirmedActiveParticipantsSummaries.add(participant.getUser());
				}
			} else if (participant.getUser().isProfileActive()) {
				cachedUnconfirmedActiveParticipants.add(participant);
			}
		}
	}

	
	public Participant getConfirmedActiveParticipant(String userId) {
		if (userId == null) {
			return null;
		}
		
		for (Participant participant : getConfirmedActiveParticipants()) {
			if (userId.equals(participant.getUser().getUserName())) {
				return participant;
			}
		}
		return null;
	}
	
	public Participant getParticipant(String userId) {
		if (userId == null) {
			return null;
		}
		
		for (Participant participant : participants) {
			if (userId.equals(participant.getUser().getUserName())) {
				return participant;
			}
		}
		
		return null;
	}

	
	
	/**
	 * @param userId
	 * @return true if this user is already part of this project or has applied for it
	 */
	public boolean isUserAlreadyMember(String userId) {
		if (getConfirmedParticipants() == null || userId == null) {
			return false;
		}

		for (Participant participant : getConfirmedParticipants()) {
			if (userId.equals(participant.getUser().getUserName())) {
				return true;
			}
		}

		return false;
	}
	
	
	public boolean isUserApplying(String userId) {
		if (getUnconfirmedActiveParticipants() == null || userId == null) {
			return false;
		}
		
		for (Participant participant : getUnconfirmedActiveParticipants()) {
			if (userId.equals(participant.getUser().getUserName())) {
				return true;
			}
		}
		
		return false;
	}

	
	
	
	
	public boolean hasNoOtherActiveParticipantThanTheOwner() {
		return getConfirmedActiveParticipants() == null || getConfirmedActiveParticipants().size() < 2;
	}



	// ------------------------------------------------------------------------------
	//
	public void generatePhotoLinks(Date expirationdate) {
		if (photos != null) {
			for (Photo photo : photos) {
				photo.generatePresignedLinks(expirationdate, true, true);
			}
		}
		
		if (participants != null) {
			for (Participant participant : participants) {
				participant.generatePhotoLinks(expirationdate);
			}
		}
	}
	
	
	public void prepareTasksAndAssetsUsersummary() {
		if (tasks != null) {
			for (Task task : tasks) {
				task.computeAssigneeSummaries(this);
				task.getDueDateStr();
			}
		}
		if (assets != null) {
			for (Asset asset : assets) {
				asset.computeAssigneeSummaries(this);
				asset.getDueDateStr();
			}
		}
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

	public Set<String> getLinks() {
		return links;
	}

	public void setLinks(Set<String> links) {
		this.links = links;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public int getMainPhotoIndex() {
		return mainPhotoIndex;
	}

	public void setMainPhotoIndex(int mainPhotoIndex) {
		this.mainPhotoIndex = mainPhotoIndex;
	}

	public Set<SelectedTheme> getThemes() {
		return themes;
	}

	public void setThemes(Set<SelectedTheme> themes) {
		this.themes = themes;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Set<Asset> getAssets() {
		
		return assets;
	}

	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}


}