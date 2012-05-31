package com.svend.dab.dao.solr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.beans.projects.SearchQuery.SORT_KEY;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IProjectIndexDao;

import controllers.BeanProvider;

@Service
public class ProjectIndexDao implements IProjectIndexDao {

	private static Logger logger = Logger.getLogger(ProjectIndexDao.class.getName());

	@Autowired
	private HttpSolrServer solr;

	@Autowired
	private Config config;

	@Autowired
	private ProjectSolrConverter converter;

	@Autowired
	private IProjectDao projectDao;

	public void updateIndex(String projectId, boolean immediate) {

		if (!immediate) {
			// increasing the chances to actually catch the updated data in db (you know, eventual consistency thingy...)
			// TODO: clean this up: mongo driver can be configured not to return until we are sure to have written something
			Utils.waitABit();
		}

		Project project = projectDao.findOne(projectId);

		if (project == null || project.getPdata() == null) {
			logger.log(Level.WARNING, "refusing to index a null project or project with null pdata");
		} else {
			try {
				UpdateResponse response = solr.add(converter.toSolrInputDocument(project), config.getSolrCommitWithin());
				logger.log(Level.INFO, "response status: " + response.getStatus());
				solr.commit();
			} catch (Exception e) {
				// TODO: this assumes that all SOLR errors are recoverable (i.e. "retryable"), but only those related to network or storage issues should be retried
				// the errors related to schema or any incorrect input should not be retried.
				throw new SolrAccessException("Problem while trying to index project", e);
			}
		}
	}

	public List<ProjectOverview> searchForProjects(SearchQuery request) {

		List<ProjectOverview> projectOverviews = new LinkedList<ProjectOverview>();

		try {
			SolrQuery solrQuery = buildSolrQuery(request);
			solrQuery.set(CommonParams.QT, "/projects");
			QueryResponse response = solr.query(solrQuery);

//			System.out.println("response: " + response);
			
			if (response != null && response.getResults() != null) {

				Date expirationdate = new Date();
				expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getCvExpirationDelayInMillis());

				for (SolrDocument doc : response.getResults()) {
					projectOverviews.add(converter.toProjectOverview(doc, expirationdate));
				}
			}

		} catch (SolrServerException e) {

			// TODO: this assumes that all SOLR errors are recoverable (i.e. "retryable"), but only those related to netword or storage issues should be retried
			// the errors related to schema or any incorrect input should not be retried.
			throw new SolrAccessException("Problem while trying to retrieve a project", e);
		}

		return projectOverviews;

	}

	/**
	 * Converts this {@link SearchQuery} into a project-specific Solr query (search a group would be done with the same {@link SearchQuery} but involves different Solr parameters)
	 * 
	 * @param request
	 * @return
	 */
	public SolrQuery buildSolrQuery(SearchQuery request) {
		StringBuffer userQuery = new StringBuffer();

		// white spaces get replaced by "+" at some point => using "," instead
		if (request.getSearchTerm() != null) {
			userQuery = userQuery.append(request.getSearchTerm().replace(" ", ","));
		}
		
		userQuery.append(" ");
		userQuery.append(ProjectSolrConverter.SEARCH_TERM_IN_EVERY_PROJECT);

		if (request.getThemes() != null && !request.getThemes().isEmpty()) {
			userQuery.append(" AND ( ");
			int i = 0;
			for (SelectedTheme sTheme : request.getThemes()) {
				userQuery.append(" ");
				userQuery.append("prj_themes:");
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
				userQuery.append("prj_tags:");
				userQuery.append(tag);
				if (i ++ < request.getTags().size()-1) {
					userQuery.append(" OR ");
				}
			}
			userQuery.append(" ) ");
		}

		if (!Strings.isNullOrEmpty(request.getLanguage())) {
			userQuery.append(" +prj_language_code:");
			userQuery.append(request.getLanguage());
		}
		
		if (request.getDueDateBefore() != null) {
			userQuery.append(" AND prj_due_date:[ 1990-01-01T00:00:00.000Z TO ");
			userQuery.append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(request.getDueDateBefore()));
			userQuery.append("  ] ");
		}
		
		SolrQuery solrQuery = new SolrQuery(userQuery.toString());

		if (request.getInGeographicRegion() != null) {
			//{!geofilt pt=45.15,-93.85 sfield=store d=5}
			StringBuffer fq = new StringBuffer("{!geofilt pt=");
			fq.append(request.getInGeographicRegion().getCenter().getLatitude().toString());
			fq.append(",");
			fq.append(request.getInGeographicRegion().getCenter().getLongitude().toString());
			fq.append(" sfield=prj_location d=");
			fq.append(request.getInGeographicRegion().getRadiusInKm());
			fq.append("}");
			solrQuery.addFilterQuery(fq.toString());
		}

		if (request.getSortKey() ==SORT_KEY.duedate) {
			solrQuery.addSortField("prj_due_date", ORDER.asc);
		} 
		
		return solrQuery;
	}
}
