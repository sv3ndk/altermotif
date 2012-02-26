package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IContactDao;

@Component
public class ContactRepoImpl implements IContactDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<Contact> findContactByBothUsers(String user1, String user2) {

		// this looks like a bug: we need to put "_id" and not "userName" ?!?!?!
		// Query query = query(where("requestedByUser.userName").is(fromUser).and("requestedToUser.userName").is(toUser));
		
		// TODO: put a "or" logic here with just one query..
		Query query1 = query(where("requestedByUser._id").is(user1).and("requestedToUser._id").is(user2));
		Query query2 = query(where("requestedByUser._id").is(user2).and("requestedToUser._id").is(user1));
				
		List<Contact> list1 = mongoTemplate.find(query1, Contact.class); 
		List<Contact> list2 = mongoTemplate.find(query2, Contact.class); 
		
		List<Contact> list = new LinkedList<Contact>();
		list.addAll(list1);
		list.addAll(list2);
		
		return list;

	}

	/* (non-Javadoc)
	 * @see com.svend.dab.dao.mongo.IContactDao#findContactsOneUser(java.lang.String)
	 */
	
	public List<Contact> findContactsOneUser(String username) {
		
		// TODO: put a "or" logic here with just one query..
		Query query1 = query(where("requestedByUser._id").is(username));
		Query query2 = query(where("requestedToUser._id").is(username));
				
		List<Contact> list1 = mongoTemplate.find(query1, Contact.class); 
		List<Contact> list2 = mongoTemplate.find(query2, Contact.class); 
		
		List<Contact> list = new LinkedList<Contact>();
		list.addAll(list1);
		list.addAll(list2);
		
		return list;
	}

	
	public void updateRequestedByUser(Contact contact, UserSummary updatedUserSummary) {
		Query query = query(where("_id").is(contact.getContactId()));
		Update update = new Update().set("requestedByUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, Contact.class);
		
	}

	
	public void updateRequestedToUser(Contact contact, UserSummary updatedUserSummary) {
		Query query = query(where("_id").is(contact.getContactId()));
		Update update = new Update().set("requestedToUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, Contact.class);
	}

	
	public void delete(Contact existingContact) {
		mongoTemplate.remove(existingContact);
	}

	
	public void save(Contact contact) {
		mongoTemplate.save(contact);
		
	}

	

}
