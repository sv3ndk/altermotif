package com.svend.dab.core.groups;

import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.groups.GroupOverview;
import com.svend.dab.core.beans.groups.IndexedGroup;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IIndexedGroupDao;

/**
 * @author svend
 * 
 */
@Service
public class QuickAndDirtyGroupFullTextSearch implements IGroupFtsService {

	private static Logger logger = Logger.getLogger(QuickAndDirtyGroupFullTextSearch.class.getName());

	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IIndexedGroupDao indexedGroupDao;
	
	public void updateGroupIndex(String groupId, boolean immediate) {

		if (!immediate) {
			// increasing the chances to actually catch the updated data in db (you know, eventual consistency thingy...)
			// ...I said this class was quick and dirty....
			Utils.waitABit();
		}

		ProjectGroup group = groupDao.retrieveGroupById(groupId);

		if (group == null) {
			logger.warning("cannot update full text search index for group with id=" + groupId + ": no group found!");
		} else {
			
			IndexedGroup indexedGroup = new  IndexedGroup(group);
			
			// indexing full search search based on content in the name, description and goal
			String textToIndex = new StringBuffer(group.getName()).append(" ").append(group.getDescription()).append(" ").toString();

			if (!Strings.isNullOrEmpty(textToIndex)) {
				StringTokenizer st = new StringTokenizer(textToIndex);
				while (st.hasMoreTokens()) {
					indexedGroup.addFtsTerm(st.nextToken());
				}
			}
			
			indexedGroupDao.updateIndex(indexedGroup);
			

		}

	}

	public void ensureIndexOnLocation() {
		indexedGroupDao.ensureIndexOnLocation();	
	}

	public List<GroupOverview> searchForGroups(SearchQuery request) {
		
		List<IndexedGroup> indexedGroups = indexedGroupDao.searchGroups(request);
		
		return null;
	}

}
