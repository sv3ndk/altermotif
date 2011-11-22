package com.svend.dab.eda;

import com.svend.dab.eda.events.contacts.ContactRelationshipRemoved;
import com.svend.dab.eda.events.contacts.ContactRelationshipRequested;
import com.svend.dab.eda.events.contacts.ContactRelationshipResponse;
import com.svend.dab.eda.events.messages.MessageWrittenEvent;
import com.svend.dab.eda.events.profile.ProfileRefUpdated;
import com.svend.dab.eda.events.profile.UserLoggedInEvent;
import com.svend.dab.eda.events.profile.UserPrivacySettingsUpdatedEvent;
import com.svend.dab.eda.events.profile.UserProfilePersonalDataUpdatedEvent;
import com.svend.dab.eda.events.profile.UserReferenceRemovedEvent;
import com.svend.dab.eda.events.profile.UserReferenceWritten;
import com.svend.dab.eda.events.profile.UserSummaryUpdated;
import com.svend.dab.eda.events.projects.ProjectCreated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;

/**
 * container where from all the event propagators may be found
 * 
 * @author svend
 *
 */
public interface IEventPropagatorsContainer {
	
	public IEventPropagator<UserProfilePersonalDataUpdatedEvent> getUserProfileUpdateEventPropagator() ;
	
	public IEventPropagator<MessageWrittenEvent> getMessageWrittenEventPropagator();
	
	public IEventPropagator<UserReferenceWritten> getReferenceLeftEventPropagator();
	
	public IEventPropagator<UserReferenceRemovedEvent> getUserReferenceRemovedEventPropagator();
	
	public IEventPropagator<BinaryNoLongerRequiredEvent> getBinaryNoLongerRequiredPropagator();
	
	public IEventPropagator<UserLoggedInEvent> getUserLoggedInEventPropagator();
	
	public IEventPropagator<ContactRelationshipRequested> getContactRelationshipRequestedPropagator();
	
	public IEventPropagator<ContactRelationshipResponse> getContactRelationshipResponsePropagator();

	public IEventPropagator<ContactRelationshipRemoved> getContactRelationshipRemovedPropagator();
	
	public IEventPropagator<UserPrivacySettingsUpdatedEvent> getUserPrivacySettingsUpdatedPropagator();
	
	public IEventPropagator<UserSummaryUpdated> getUserSummaryUpdatedPropagator();

	public IEventPropagator<ProfileRefUpdated> getProfileRefUpdatedPropagator();
	
	public IEventPropagator<ProjectCreated> getProjectCreatedPropagator();

}
