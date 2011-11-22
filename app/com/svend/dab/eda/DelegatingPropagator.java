package com.svend.dab.eda;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
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
 * This is a simple container/dispatcher: all event propagation pass here, but the event has then the possibility to choose the propagator 
 * which is actually going to process it.
 * 
 * @author svend
 * 
 */
@Service
public class DelegatingPropagator implements IEventPropagator<Event>, IEventPropagatorsContainer {

	private static Logger logger = Logger.getLogger(DelegatingPropagator.class.getName());

	// -----------------------------------------------------------------
	// set of specific propagators, for each event type

	@Autowired
	@Qualifier("userProfilePersonalDataEventPropagator")
	private IEventPropagator<UserProfilePersonalDataUpdatedEvent> userProfileUpdateEventPropagator;

	@Autowired
	@Qualifier("messageWrittenEventPropagator")
	private IEventPropagator<MessageWrittenEvent> messageWrittenEventPropagator;

	@Autowired
	@Qualifier("userReferenceWrittenEventPropagator")
	private IEventPropagator<UserReferenceWritten> referenceLeftEventPropagator;

	@Autowired
	@Qualifier("userReferenceRemovedEventPropagator")
	private IEventPropagator<UserReferenceRemovedEvent> userReferenceRemovedEventPropagator;
	
	@Autowired
	@Qualifier("binaryNoLongerRequiredPropagator")
	private IEventPropagator<BinaryNoLongerRequiredEvent> binaryNoLongerRequiredPropagator;
	
	@Autowired
	@Qualifier("userLoggedInEventPropagator")
	private IEventPropagator<UserLoggedInEvent> userLoggedInEventPropagator;
	
	@Autowired
	@Qualifier("contactRelationshipRequestedPropagator")
	private IEventPropagator<ContactRelationshipRequested> contactRelationshipRequestedPropagator;

	@Autowired
	@Qualifier("contactRelationshipResponsePropagator")
	private IEventPropagator<ContactRelationshipResponse> contactRelationshipResponsePropagator;

	@Autowired
	@Qualifier("contactRelationshipRemovedPropagator")
	private IEventPropagator<ContactRelationshipRemoved> contactRelationshipRemovedPropagator;

	@Autowired
	@Qualifier("userPrivacySettingsUpdatedPropagator")
	private IEventPropagator<UserPrivacySettingsUpdatedEvent> userPrivacySettingsUpdatedPropagator;
	
	@Autowired
	@Qualifier("userSummaryUpdatedPropagator")
	private IEventPropagator<UserSummaryUpdated> userSummaryUpdatedPropagator;

	@Autowired
	@Qualifier("profileRefUpdatedPropagator")
	private IEventPropagator<ProfileRefUpdated> profileRefUpdatedPropagator;

	@Autowired
	@Qualifier("projectCreatedPropagator")
	private IEventPropagator<ProjectCreated> projectCreatedPropagator;
	
	// -----------------------------------------------------------------
	// -----------------------------------------------------------------
	// -----------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.IEventPropagator#propagate(com.svend.dab.eda.events.Event)
	 */
	@Override
	public void propagate(Event event) throws DabException {
		if (event == null) {
			logger.log(Level.WARNING, "Refusing to propagate a null event => not doing anything");
		} else {
			event.selectEventProcessor(this).propagate(event);
		}
	}

	// -----------------------------------------------------------------
	// interface IEventPropagatorsContainer
	// -----------------------------------------------------------------

	@Override
	public IEventPropagator<UserProfilePersonalDataUpdatedEvent> getUserProfileUpdateEventPropagator() {
		return userProfileUpdateEventPropagator;
	}

	@Override
	public IEventPropagator<MessageWrittenEvent> getMessageWrittenEventPropagator() {
		return messageWrittenEventPropagator;
	}

	@Override
	public IEventPropagator<UserReferenceWritten> getReferenceLeftEventPropagator() {
		return referenceLeftEventPropagator;
	}

	@Override
	public IEventPropagator<UserReferenceRemovedEvent> getUserReferenceRemovedEventPropagator() {
		return userReferenceRemovedEventPropagator;
	}

	@Override
	public IEventPropagator<BinaryNoLongerRequiredEvent> getBinaryNoLongerRequiredPropagator() {
		return binaryNoLongerRequiredPropagator;
	}

	@Override
	public IEventPropagator<UserLoggedInEvent> getUserLoggedInEventPropagator() {
		return userLoggedInEventPropagator;
	}

	@Override
	public IEventPropagator<ContactRelationshipRequested> getContactRelationshipRequestedPropagator() {
		return contactRelationshipRequestedPropagator;
	}

	@Override
	public IEventPropagator<ContactRelationshipResponse> getContactRelationshipResponsePropagator() {
		return contactRelationshipResponsePropagator;
	}

	@Override
	public IEventPropagator<ContactRelationshipRemoved> getContactRelationshipRemovedPropagator() {
		return contactRelationshipRemovedPropagator;
	}

	@Override
	public IEventPropagator<UserPrivacySettingsUpdatedEvent> getUserPrivacySettingsUpdatedPropagator() {
		return userPrivacySettingsUpdatedPropagator;
	}

	@Override
	public IEventPropagator<UserSummaryUpdated> getUserSummaryUpdatedPropagator() {
		return userSummaryUpdatedPropagator;
	}

	@Override
	public IEventPropagator<ProfileRefUpdated> getProfileRefUpdatedPropagator() {
		return profileRefUpdatedPropagator;
	}

	@Override
	public IEventPropagator<ProjectCreated> getProjectCreatedPropagator() {
		return projectCreatedPropagator;
	}

}
