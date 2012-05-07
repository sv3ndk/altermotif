package com.svend.dab.dao.solr;

import java.util.Date;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectOverview;

@Service
public class ProjectSolrConverter {

	
	// prefixing the ids of the projects, because both groups and projects and indexed in the same index
	public static String ID_PREFIX = "prj_";
	public static String PROJECT_DATA_TYPE = "project";

	
	public SolrInputDocument  toSolrInputDocument (Project project) {
		
		SolrInputDocument prjDoc = new SolrInputDocument();
		prjDoc.addField( "id", ID_PREFIX + project.getId());
		prjDoc.addField( "data_type", PROJECT_DATA_TYPE);
		
		prjDoc.addField( "prj_name", project.getPdata().getName());
		prjDoc.addField( "prj_goal", project.getPdata().getGoal());
		prjDoc.addField( "prj_description", project.getPdata().getDescription());
		prjDoc.addField( "prj_reason", project.getPdata().getReason());
		prjDoc.addField( "prj_strategy", project.getPdata().getStrategy());
		prjDoc.addField( "prj_offer", project.getPdata().getOffer());
		
		prjDoc.addField( "prj_participants", project.getNumberOfConfirmedParticipants());
		if (project.getPhotoAlbum().getMainPhoto().getThumbLink() != null) {
			prjDoc.addField( "prj_thumb_bucket", project.getPhotoAlbum().getMainPhoto().getThumbLink().getS3BucketName());
			prjDoc.addField( "prj_thumb_key", project.getPhotoAlbum().getMainPhoto().getThumbLink().getS3Key());
		}

		
		if (project.getTags() != null && !project.getTags().isEmpty()) {
			// this will produce blasf, blabla, blwf, bla, which Solr will automatically parse for us... :-)
			String tags = project.getTags().toString();
			prjDoc.addField( "prj_tags", tags.substring(1, tags.length()-2));
		}

		
		return prjDoc;
	}
	
	
	public ProjectOverview toProjectOverview(SolrDocument solrProject, Date expirationdate) {
		ProjectOverview projectOverview = new ProjectOverview();

		
		String fullId = (String )solrProject.getFieldValue("id");
		
		if (fullId != null && fullId.length() > 4) {
			projectOverview.setProjectId(fullId.substring(4));
		}

		projectOverview.setName((String )solrProject.getFieldValue("prj_name"));
		projectOverview.setGoal((String )solrProject.getFieldValue("prj_goal"));
		projectOverview.setDescription((String )solrProject.getFieldValue("prj_description"));
		
		projectOverview.setNumberOfMembers((Integer)solrProject.getFieldValue("prj_participants") );
		
		String s3BucketName = (String )solrProject.getFieldValue("prj_thumb_bucket");
		String s3Key = (String )solrProject.getFieldValue("prj_thumb_key");
		
		
		Photo thumb = new Photo("", null, new S3Link("", s3Key, s3BucketName));
		thumb.generatePresignedLinks(expirationdate, false, true);
		projectOverview.setMainThumb(thumb);
		
		
		return projectOverview;
	}
	
	
}
