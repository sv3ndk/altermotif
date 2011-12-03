package com.svend.dab.eda;


/**
 * container where from all the event propagators may be found
 * 
 * @author svend
 *
 */
public interface IEventPropagatorsContainer {
	
	public IEventPropagator getPropagatorByName(String name);
	
	
//	public IEventPropagator<UserProfilePersonalDataUpdatedEvent> getUserProfileUpdateEventPropagator() ;
//	
//	public IEventPropagator<MessageWrittenEvent> getMessageWrittenEventPropagator();
//	
//	public IEventPropagator<UserReferenceWritten> getReferenceLeftEventPropagator();
//	
//	public IEventPropagator<UserReferenceRemovedEvent> getUserReferenceRemovedEventPropagator();
//	
//	public IEventPropagator<BinaryNoLongerRequiredEvent> getBinaryNoLongerRequiredPropagator();
//	
//	public IEventPropagator<UserLoggedInEvent> getUserLoggedInEventPropagator();
//	
//	public IEventPropagator<ContactRelationshipRequested> getContactRelationshipRequestedPropagator();
//	
//	public IEventPropagator<ContactRelationshipResponse> getContactRelationshipResponsePropagator();
//
//	public IEventPropagator<ContactRelationshipRemoved> getContactRelationshipRemovedPropagator();
//	
//	public IEventPropagator<UserPrivacySettingsUpdatedEvent> getUserPrivacySettingsUpdatedPropagator();
//	
//	public IEventPropagator<UserSummaryUpdated> getUserSummaryUpdatedPropagator();
//
//	public IEventPropagator<ProfileRefUpdated> getProfileRefUpdatedPropagator();
//	
//	public IEventPropagator<ProjectCreated> getProjectCreatedPropagator();
//	
//	public IEventPropagator<ProjectUpdated> getProjectUpdatedPropagator() ;


}
