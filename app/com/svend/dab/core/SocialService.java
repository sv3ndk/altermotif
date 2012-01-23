package com.svend.dab.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * @author svend
 *
 */
@Component
public class SocialService implements ISocialService {

	@Autowired
	private MailSender mailSender;
	
	public void sendEmail(String recipient, String subject, String textContent) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(recipient);
		message.setSubject(subject);
		message.setText(textContent);
		
		mailSender.send(message);
		

	}

}
