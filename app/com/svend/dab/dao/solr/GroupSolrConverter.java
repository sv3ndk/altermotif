package com.svend.dab.dao.solr;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.groups.ProjectGroup;
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
		
		return grpDoc;
	}

}
