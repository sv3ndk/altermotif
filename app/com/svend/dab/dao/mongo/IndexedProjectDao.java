package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.profile.UserProfile;
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

		List<Criteria> criterias = new LinkedList<Criteria>();

		if (!Strings.isNullOrEmpty(request.getSearchTerm())) {
			StringTokenizer st = new StringTokenizer(request.getSearchTerm());

			List<String> terms = new LinkedList<String>();
			while (st.hasMoreElements()) {
				terms.add(st.nextToken());
			}

			if (!terms.isEmpty()) {
				criterias.add(where("terms").all(terms.toArray()));
			}
		}

		if (request.getTags() != null && !request.getTags().isEmpty()) {
			criterias.add(where("tags").all(request.getTags().toArray()));
		}

		if (request.getThemes() != null && !request.getThemes().isEmpty()) {
			List<String> themesWithSubTheme = new LinkedList<String>();
			for (SelectedTheme st : request.getThemes()) {
				themesWithSubTheme.add(st.getThemeId() + "_" + st.getSubThemeId());
			}
			criterias.add(where("themesWithSubTheme").all(themesWithSubTheme.toArray()));
		}

		if (criterias.isEmpty()) {
			// if no search criteria: just refusing to search!
			return new LinkedList<IndexedProject>();
		} else {
			Query theQuery;
			if (criterias.size() == 1) {
				theQuery = query(criterias.get(0));
			} else {
				theQuery = query(new Criteria().andOperator(criterias.toArray(new Criteria[] {})));
			}
			return mongoTemplate.find(theQuery, IndexedProject.class);
		}

	}

}
