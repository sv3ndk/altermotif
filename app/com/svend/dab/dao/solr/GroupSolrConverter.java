package com.svend.dab.dao.solr;

import static com.svend.dab.core.CoreTool.getSolrFieldDate;
import static com.svend.dab.core.CoreTool.getSolrFieldInteger;
import static com.svend.dab.core.CoreTool.getSolrFieldString;

import java.util.Date;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.groups.GroupOverview;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.projects.SelectedTheme;

@Service
public class GroupSolrConverter {
	
	public static String ID_PREFIX = "grp_";
	public static String GROUP_DATA_TYPE = "group";

	
	// workaround a bug in Solr (or, more likely, my own incompetence...)
	// when looking for project date and tags, sometimes the expected projects are not found
	// if we add this systematic search term in every project and include it in every search query, it seems to work...
	public static String SEARCH_TERM_IN_EVERY_GROUP = "IDg9rancXn8Z4qhsB0uu";


	public SolrInputDocument toSolrInputDocument(ProjectGroup group) {
		
		SolrInputDocument grpDoc = new SolrInputDocument();
		grpDoc.addField("id", ID_PREFIX + group.getId());
		grpDoc.addField("data_type", GROUP_DATA_TYPE);
		grpDoc.addField("grp_fake_field", SEARCH_TERM_IN_EVERY_GROUP);

		grpDoc.addField("grp_name", group.getName());
		grpDoc.addField("grp_description", group.getDescription());
		grpDoc.addField("grp_creation_date", group.getCreationDate());
		
		grpDoc.addField("grp_user_members", group.getNumberOfParticipants());
		grpDoc.addField("grp_prj_members", group.getNumberOfProjects());

		if (group.getTags() != null) {
			for (String tag : group.getTags()) {
				grpDoc.addField("grp_tags", tag);
			}
		}
		
		if (group.getThemes() != null ) {
			for (SelectedTheme sTheme : group.getThemes()) {
				grpDoc.addField("grp_themes", sTheme.buildStringRepresentation());
			}
		}

		if (group.getPhotoAlbum().getMainPhoto().getThumbLink() != null) {
			grpDoc.addField("grp_thumb_bucket", group.getPhotoAlbum().getMainPhoto().getThumbLink().getS3BucketName());
			grpDoc.addField("grp_thumb_key", group.getPhotoAlbum().getMainPhoto().getThumbLink().getS3Key());
		}
		
		if (group.getLocation() != null) {
			for (Location location: group.getLocation()) {
				grpDoc.addField("grp_location", location.getLatitude() + "," + location.getLongitude());
				grpDoc.addField("grp_location_name", location.getLocation());
			}
		}

		
		return grpDoc;
	}


	public GroupOverview toGroupOverview(SolrDocument solrGroup, Date expirationdate) {
		
		GroupOverview groupOverview = new GroupOverview();
		
		String fullId = (String) solrGroup.getFieldValue("id");

		if (fullId != null && fullId.length() > 4) {
			groupOverview.setGroupId(fullId.substring(4));
		}
		
		groupOverview.setName(getSolrFieldString(solrGroup, "grp_name", ""));
		groupOverview.setDescription(getSolrFieldString(solrGroup, "grp_description", ""));

		groupOverview.setCreationDate(getSolrFieldDate(solrGroup, "grp_creation_date", (Date) null));

		groupOverview.setNumberOfUserMembers(getSolrFieldInteger(solrGroup, "grp_user_members", 0));
		groupOverview.setNumberOfGroupMembers(getSolrFieldInteger(solrGroup, "grp_prj_members", 0));

		String s3BucketName = getSolrFieldString(solrGroup, "grp_thumb_bucket", null);
		String s3Key = getSolrFieldString(solrGroup, "grp_thumb_key", null);
		
		if (s3BucketName != null && s3Key != null) {
			Photo thumb = new Photo("", null, new S3Link("", s3Key, s3BucketName));
			groupOverview.setMainThumb(thumb);
			groupOverview.generatePhotoLinks(expirationdate);
		}
		
		return groupOverview;
	}

}
