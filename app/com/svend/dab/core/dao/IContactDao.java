package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.UserSummary;

public interface IContactDao {

	public abstract List<Contact> findContactByBothUsers(String fromUser, String toUser);

	public abstract List<Contact> findContactsOneUser(String requestedByUserUsername);
	
	public void updateRequestedByUser(Contact contact, UserSummary updatedUserSummary);

	public void updateRequestedToUser(Contact contact, UserSummary updatedUserSummary);

	public abstract void delete(Contact existingContact);

	public abstract void save(Contact createdPendingContact);
	
	
}
