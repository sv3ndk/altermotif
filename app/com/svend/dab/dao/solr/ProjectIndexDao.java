package com.svend.dab.dao.solr;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;
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
				
				// TODO: this assumes that all SOLR errors are recoverable (i.e. "retryable"), but only those related to netword or storage issues should be retried
				// the errors related to schema or any incorrect input should not be retried.
				throw new SolrAccessException("Problem while trying to index project", e);
			}
		}
		
	}
	
	
	public List<ProjectOverview> searchForProjects(SearchQuery request) {
		
		
		List<ProjectOverview> projectOverviews = new LinkedList<ProjectOverview>();
		
		try {
			
			String userQuery = "";
			
			// white spaces get replaced by "+" at some point => using "," instead
			if (request.getSearchTerm() != null) {
				userQuery = request.getSearchTerm().replace(" ", ",");
			}
			
			SolrQuery solrQuery = new SolrQuery(userQuery);
			solrQuery.set(CommonParams.QT, "/projects");
			
			QueryResponse response = solr.query(solrQuery);
			
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


}
