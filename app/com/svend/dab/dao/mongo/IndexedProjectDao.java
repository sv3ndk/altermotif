package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.svend.dab.core.beans.projects.IndexedProject;
import com.svend.dab.core.beans.projects.ProjectSearchQuery;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.dao.IIndexedProjectDao;

/**
 * @author svend
 * 
 */
@Service
public class IndexedProjectDao implements IIndexedProjectDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.dao.IIndexedProjectDao#updateIndex(com.svend.dab.core.beans.projects.IndexedProject)
	 */
	@Override
	public void updateIndex(IndexedProject ip) {
		mongoTemplate.save(ip);
	}

	@Override
	public List<IndexedProject> searchForProjects(ProjectSearchQuery request) {

		Criteria criteria = where ("_id").ne("lesPetitsPasDansLesPetisPlatsHalala");
		
		// search term
		if (!Strings.isNullOrEmpty(request.getSearchTerm())) {
			StringTokenizer st = new StringTokenizer(request.getSearchTerm());

			List<String> terms = new LinkedList<String>();
			while (st.hasMoreElements()) {
				terms.add(st.nextToken());
			}

			if (!terms.isEmpty()) {
				criteria.and("terms").all(terms.toArray());
			}
		}

		// tags
		if (request.getTags() != null && !request.getTags().isEmpty()) {
			criteria.and("tags").all(request.getTags().toArray());
		}

		// categories
		if (request.getThemes() != null && !request.getThemes().isEmpty()) {
			List<String> themesWithSubTheme = new LinkedList<String>();
			for (SelectedTheme st : request.getThemes()) {
				themesWithSubTheme.add(st.getThemeId() + "_" + st.getSubThemeId());
			}
			criteria.and("themesWithSubTheme").all(themesWithSubTheme.toArray());
		}
		
		// max due date
		if (request.getDueDateBefore() != null) {
			criteria.and("dueDate").lte(request.getDueDateBefore());
		}
		
		// project language
		if (request.getLanguage() != null) {
			criteria.and("language").is(request.getLanguage());
		}
		
		// project location close to 
		if (request.getInGeographicRegion() != null) {
			
			Point center = new Point(request.getInGeographicRegion().getCenter().getLatitude(), request.getInGeographicRegion().getCenter().getLongitude());
			
			criteria.and("location").near(center).maxDistance(request.getInGeographicRegion().getRadiusInDegrees());
		}
		

		return mongoTemplate.find(query(criteria), IndexedProject.class);

	}

	@Override
	public void ensureIndexOnLocation() {

		// "ensureIndex" seems to be missing in RC1 version of mongo Spring Data
		
		mongoTemplate.execute("indexedProject", new CollectionCallback<Long>() {
			@Override
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject indexDbo = new BasicDBObject();
				indexDbo.put("location", "2d");
				collection.ensureIndex(indexDbo);
				return 0l;
			}
		});
	}

}
