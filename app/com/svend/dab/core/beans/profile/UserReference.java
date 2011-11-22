package com.svend.dab.core.beans.profile;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.svend.dab.core.beans.Config;

/**
 * @author Svend
 * 
 *         javabean for a "reference" that a user may leave to another user
 * 
 */
public class UserReference implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(UserReference.class.getName());

	// rem: I should have used two user summaries here, but anyway...

	// "id" of the reference
	// by convention, the id of a UserReference is the same in both profile where the reference is stored
	private String id;

	private UserSummary fromUser;
	private UserSummary toUser;

	private Date creationDate;

	private String text;
	
	
	

	// -----------------------------
	//

	/**
	 * 
	 */
	public UserReference() {
		super();
	}

	
	
	public UserReference(String id) {
		super();
		this.id = id;
	}



	/**
	 * @param copied
	 */
	public UserReference(UserReference copied) {
		this(copied.id, copied.fromUser, copied.toUser, copied.creationDate, copied.text);
	}

	/**
	 * @param id
	 * @param fromUserName
	 * @param formUserCityOfReference
	 * @param toUserName
	 * @param toUserCityOfReference
	 * @param creationDate
	 * @param text
	 * @param fromUserPhoto
	 * @param toUserPhoto
	 */
	public UserReference(String id, UserSummary fromUser, UserSummary toUser, Date creationDate, String text) {
		this.id = id;
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.text = text;
		this.creationDate = creationDate;
	}

	/**
	 * @param expirationdate
	 */
	public void generatePhotoLink(Date expirationdate) {

		// only generating the links for the thumb, not for the full size photo
		if (fromUser != null) {
			fromUser.generatePhotoLink(expirationdate);
		}

		if (toUser != null) {
			toUser.generatePhotoLink(expirationdate);
		}

	}
	
	
	public String getCreationDateStr() {
		return new SimpleDateFormat(Config.getDateDisplayFormat_Static()).format(creationDate);
	}

	// ----------------------
	// getters, setters

	public boolean isHasFromUserPhoto() {
		return fromUser != null && fromUser.getMainPhotoThumb() != null;
	}

	public boolean isHasToUserPhoto() {
		return toUser != null && toUser.getMainPhotoThumb() != null;
	}

	public boolean isWrittenTo(String toUserName) {
		return toUser != null && toUserName != null && toUserName.equals(toUser.getUserName());
	}
	
	public boolean isWrittenBy(String fromUserName) {
		return fromUser != null && fromUserName != null && fromUserName.equals(fromUser.getUserName());
	}
	


	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserSummary getFromUser() {
		return fromUser;
	}

	public void setFromUser(UserSummary fromUser) {
		this.fromUser = fromUser;
	}

	public UserSummary getToUser() {
		return toUser;
	}

	public void setToUser(UserSummary toUser) {
		this.toUser = toUser;
	}

	public String getId() {
		return id;
	}





}
