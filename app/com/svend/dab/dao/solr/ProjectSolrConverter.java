package com.svend.dab.dao.solr;

import static com.svend.dab.core.CoreTool.getSolrFieldInteger;
import static com.svend.dab.core.CoreTool.getSolrFieldString;
import static com.svend.dab.core.CoreTool.getSolrFieldDate;
import static com.svend.dab.core.CoreTool.getSolrFieldCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SelectedTheme;


@Service
public class ProjectSolrConverter {

	// prefixing the ids of the projects, because both groups and projects and indexed in the same index
	public static String ID_PREFIX = "prj_";
	public static String PROJECT_DATA_TYPE = "project";

	
	// workaround a bug in Solr (or, more likely, my own incompetence...)
	// when looking for project date and tags, sometimes the expected projects are not found
	// if we add this systematic search term in every project and include it in every search query, it seems to work...
	public static String SEARCH_TERM_IN_EVERY_PROJECT = "fU0AIoiR1Stn7I8J7w8L";
	
	public SolrInputDocument toSolrInputDocument(Project project) {

		SolrInputDocument prjDoc = new SolrInputDocument();
		prjDoc.addField("id", ID_PREFIX + project.getId());
		prjDoc.addField("data_type", PROJECT_DATA_TYPE);
		prjDoc.addField("prj_fake_field", SEARCH_TERM_IN_EVERY_PROJECT);

		// these tag names must be aligned with the SOLR schema (cf SOLR config)
		prjDoc.addField("prj_name", project.getPdata().getName());
		prjDoc.addField("prj_goal", project.getPdata().getGoal());
		prjDoc.addField("prj_description", project.getPdata().getDescription());
		prjDoc.addField("prj_reason", project.getPdata().getReason());
		prjDoc.addField("prj_strategy", project.getPdata().getStrategy() );
		prjDoc.addField("prj_offer", project.getPdata().getOffer());
		prjDoc.addField("prj_language_code", project.getPdata().getLanguage());
		prjDoc.addField("prj_due_date", project.getPdata().getDueDate());
		prjDoc.addField("prj_creation_date", project.getPdata().getCreationDate());

		prjDoc.addField("prj_participants", project.getNumberOfConfirmedParticipants());
		
		if (project.getPhotoAlbum().getMainPhoto().getThumbLink() != null) {
			prjDoc.addField("prj_thumb_bucket", project.getPhotoAlbum().getMainPhoto().getThumbLink().getS3BucketName());
			prjDoc.addField("prj_thumb_key", project.getPhotoAlbum().getMainPhoto().getThumbLink().getS3Key());
		}

		if (project.getTags() != null) {
			for (String tag : project.getTags()) {
				prjDoc.addField("prj_tags", tag);
			}
		}

		if (project.getThemes() != null ) {
			for (SelectedTheme sTheme : project.getThemes()) {
				prjDoc.addField("prj_themes", sTheme.buildStringRepresentation());
			}
		}
		
		if (project.getPdata().getLocations() != null) {
			for (Location location: project.getPdata().getLocations()) {
				prjDoc.addField("prj_location", location.getLatitude() + "," + location.getLongitude());
				prjDoc.addField("prj_location_name", location.getLocation());
			}
		}

		return prjDoc;
	}

	public ProjectOverview toProjectOverview(SolrDocument solrProject, Date expirationdate) {
		ProjectOverview projectOverview = new ProjectOverview();

		String fullId = (String) solrProject.getFieldValue("id");

		if (fullId != null && fullId.length() > 4) {
			projectOverview.setProjectId(fullId.substring(4));
		}

		projectOverview.setName(getSolrFieldString(solrProject, "prj_name", ""));
		projectOverview.setGoal(getSolrFieldString(solrProject, "prj_goal", ""));
		projectOverview.setDescription(getSolrFieldString(solrProject, "prj_description", ""));
		projectOverview.setDueDate(getSolrFieldDate(solrProject, "prj_due_date", (Date) null));
		projectOverview.setCreationDate(getSolrFieldDate(solrProject, "prj_creation_date", (Date) null));
		
		Collection<String> locations = getSolrFieldCollection(solrProject, "prj_location_name",  Collections.EMPTY_SET);
		if (!locations.isEmpty()) {
			for (String location : locations) {
				projectOverview.addLocation(location);
			}
		}
		
		projectOverview.setNumberOfMembers(getSolrFieldInteger(solrProject, "prj_participants", 0));

		String s3BucketName = getSolrFieldString(solrProject, "prj_thumb_bucket", null);
		String s3Key = getSolrFieldString(solrProject, "prj_thumb_key", null);
		
		if (s3BucketName != null && s3Key != null) {
			Photo thumb = new Photo("", null, new S3Link("", s3Key, s3BucketName));
			projectOverview.setMainThumb(thumb);
			projectOverview.generatePhotoLinks(expirationdate);
		}

		return projectOverview;
	}

}
