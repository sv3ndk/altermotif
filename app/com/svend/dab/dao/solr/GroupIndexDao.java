package com.svend.dab.dao.solr;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.groups.GroupOverview;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IGroupIndexDao;

/**
 * @author svend
 * 
 */
@Service
public class GroupIndexDao implements IGroupIndexDao {

	private static Logger logger = Logger.getLogger(GroupIndexDao.class.getName());

	@Resource
	private IGroupDao groupDao;

	@Autowired
	private HttpSolrServer solr;

	@Autowired
	private GroupSolrConverter converter;

	@Autowired
	private Config config;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.dao.IGroupIndexDao#updateIndex(java.lang.String, boolean)
	 */
	public void updateIndex(String groupId, boolean immediate) {
		if (!immediate) {
			// increasing the chances to actually catch the updated data in db (you know, eventual consistency thingy...)
			// TODO: clean this up: mongo driver can be configured not to return until we are sure to have written something
			Utils.waitABit();
		}

		
		ProjectGroup group = groupDao.retrieveGroupById(groupId);
		

		if (group == null) {
			logger.log(Level.WARNING, "refusing to index a null group or group with null pdata");
		} else {
			try {
				logger.log(Level.INFO, "indexing " + group.getName());
				UpdateResponse response = solr.add(converter.toSolrInputDocument(group), config.getSolrCommitWithin());
				logger.log(Level.INFO, "response status: " + response.getStatus());
				solr.commit();
			} catch (Exception e) {
				// TODO: this assumes that all SOLR errors are recoverable (i.e. "retryable"), but only those related to network or storage issues should be retried
				// the errors related to schema or any incorrect input should not be retried.
				throw new SolrAccessException("Problem while trying to index project", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.dao.IGroupIndexDao#searchForGroups(com.svend.dab.core.beans.projects.SearchQuery)
	 */
	public List<GroupOverview> searchForGroups(SearchQuery request) {
		// TODO Auto-generated method stub
		return null;
	}

}
