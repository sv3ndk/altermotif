package com.svend.dab.dao.solr;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IProjectIndexDao;


@Service
public class ProjectIndexDao implements IProjectIndexDao {

	private static Logger logger = Logger.getLogger(ProjectIndexDao.class.getName());
	
	// prefixing the ids of the projects, because both groups and projects and indexed in the same index
	public static String ID_PREFIX = "prj_";
	
	@Autowired
	private HttpSolrServer server;
	

	public void updateIndex(Project project) {

//		server.setRequestWriter(new org.apache.solr.client.solrj.impl.BinaryRequestWriter());
		
		if (project == null || project.getPdata() == null) {
			logger.log(Level.WARNING, "refusing to index a null project or project with null pdata");
		}
		
		SolrInputDocument prjDoc = new SolrInputDocument();
		prjDoc.addField( "id", ID_PREFIX + project.getId());
		
		prjDoc.addField( "prj_name", project.getPdata().getName());
		prjDoc.addField( "prj_goal", project.getPdata().getGoal());
		prjDoc.addField( "prj_description", project.getPdata().getDescription());
		prjDoc.addField( "prj_reason", project.getPdata().getReason());
		prjDoc.addField( "prj_strategy", project.getPdata().getStrategy());
		prjDoc.addField( "prj_offer", project.getPdata().getOffer());
		
		if (project.getTags() != null && !project.getTags().isEmpty()) {
			// this will produce blasf, blabla, blwf, bla, which Solr will automatically parse for us... :-)
			String tags = project.getTags().toString();
			prjDoc.addField( "prj_tags", tags.substring(1, tags.length()-2));
		}
		
		try {
			UpdateResponse response = server.add(prjDoc);
			logger.log(Level.INFO, "response status: " + response.getStatus());
			server.commit();
		} catch (Exception e) {
			
			// TODO: this assumes that all SOLR errors are recoverable (i.e. "retryable"), but only those related to netword or storage issues should be retried
			// the errors related to schema or any incorrect input should not be retried.
			throw new SolrAccessException("Problem while trying to index project", e);
		}
		
	}

}
