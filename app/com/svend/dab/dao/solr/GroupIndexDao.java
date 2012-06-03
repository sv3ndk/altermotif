package com.svend.dab.dao.solr;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.groups.GroupOverview;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IGroupIndexDao;

import controllers.BeanProvider;

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
		
		List<GroupOverview> groupOverviews = new LinkedList<GroupOverview>();

		try {
			SolrQuery solrQuery = buildSolrQuery(request);
			solrQuery.set(CommonParams.QT, "/groups");
			QueryResponse response = solr.query(solrQuery);

			if (response != null && response.getResults() != null) {

				Date expirationdate = new Date();
				expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getCvExpirationDelayInMillis());

				for (SolrDocument doc : response.getResults()) {
					groupOverviews.add(converter.toGroupOverview(doc, expirationdate));
				}
			}

		} catch (SolrServerException e) {

			// TODO: this assumes that all SOLR errors are recoverable (i.e. "retryable"), but only those related to netword or storage issues should be retried
			// the errors related to schema or any incorrect input should not be retried.
			throw new SolrAccessException("Problem while trying to retrieve a project", e);
		}

		return groupOverviews;


	}

	/**
	 * Converts this {@link SearchQuery} into a project-specific Solr query (search a group would be done with the same {@link SearchQuery} but involves different Solr parameters)
	 * 
	 * @param request
	 * @return
	 */
	protected SolrQuery buildSolrQuery(SearchQuery request) {

		StringBuffer userQuery = new StringBuffer();

		// white spaces get replaced by "+" at some point => using "," instead
		if (request.getSearchTerm() != null) {
			userQuery = userQuery.append(request.getSearchTerm().replace(" ", ","));
		}

		userQuery.append(" ");
		userQuery.append(GroupSolrConverter.SEARCH_TERM_IN_EVERY_GROUP);

		if (request.getThemes() != null && !request.getThemes().isEmpty()) {
			userQuery.append(" AND ( ");
			int i = 0;
			for (SelectedTheme sTheme : request.getThemes()) {
				userQuery.append(" ");
				userQuery.append("grp_themes:");
				userQuery.append(sTheme.buildStringRepresentation());
				if (i ++ < request.getThemes().size()-1) {
					userQuery.append(" OR ");
				}
			}
			userQuery.append(" ) ");
		}
		
		if (request.getTags() != null && !request.getTags().isEmpty()) {
			userQuery.append(" AND ( ");
			int i = 0;
			for (String tag : request.getTags()) {
				userQuery.append(" ");
				userQuery.append("grp_tags:");
				userQuery.append(tag);
				if (i ++ < request.getTags().size()-1) {
					userQuery.append(" OR ");
				}
			}
			userQuery.append(" ) ");
		}
		SolrQuery solrQuery = new SolrQuery(userQuery.toString());

		
		if (request.getInGeographicRegion() != null) {
			//{!geofilt pt=45.15,-93.85 sfield=store d=5}
			StringBuffer fq = new StringBuffer("{!geofilt pt=");
			fq.append(request.getInGeographicRegion().getCenter().getLatitude().toString());
			fq.append(",");
			fq.append(request.getInGeographicRegion().getCenter().getLongitude().toString());
			fq.append(" sfield=grp_location d=");
			fq.append(request.getInGeographicRegion().getRadiusInKm());
			fq.append("}");
			solrQuery.addFilterQuery(fq.toString());
		}

		
		return solrQuery;
	}
}
