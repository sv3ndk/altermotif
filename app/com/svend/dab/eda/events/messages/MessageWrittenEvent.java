/**
 * 
 */
package com.svend.dab.eda.events.messages;

import java.util.Date;

import com.svend.dab.core.beans.message.UserMessage;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class MessageWrittenEvent extends Event {

	private String fromUserName;
	private String toUserName;
	private String subject;
	private String content;
	private Date creationDate;

	public MessageWrittenEvent(String fromUserName, String toUserName, String subject, String content) {
		super();
		this.fromUserName = fromUserName;
		this.toUserName = toUserName;
		this.subject = subject;
		this.content = content;
		this.creationDate = new Date();
	};

	public MessageWrittenEvent() {
		super();
	}

	@Override
	public IEventPropagator<MessageWrittenEvent> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("messageWrittenEventPropagator");
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
