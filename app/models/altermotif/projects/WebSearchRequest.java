package models.altermotif.projects;

import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.GeoCoord;
import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.projects.GeographicCircle;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.beans.projects.SearchQuery.SORT_KEY;
import com.svend.dab.core.beans.projects.SelectedTheme;

/**
 * @author svend
 * 
 */
public class WebSearchRequest {

	private static Logger logger = Logger.getLogger(WebSearchRequest.class.getName());
	
	private String term;

	private String tag;

	// JSON format String containing a list of SelectedThemes
	private String themes;

	// max distance in km from "rl" below
	private Double maxDistance;
	
	private String maxDueDateStr;
	
	// ISO code of the filtered language
	private String lg;
	
	private String sortkey;
	
	// in case of sort by proximity and/or filter by proximity, we need a "reference location"
	private Location rl;
	
	private boolean filterProx;
	private boolean filterDate;
	private boolean filterLg;
	

	public SearchQuery toBackendRequest() {

		SearchQuery request = new SearchQuery();

		////////////////////////////////////
		// basic query (from the query page
		if (!Strings.isNullOrEmpty(term)) {
			request.setSearchTerm(term);
		}

		if (!Strings.isNullOrEmpty(tag)) {
			request.setTags(new LinkedList<String>());
			request.getTags().add(tag);
		}

		if (!Strings.isNullOrEmpty(themes)) {
			request.setThemes(new LinkedList<SelectedTheme>());
			Set<SelectedTheme> themeSet = Utils.jsonToSetOfStuf(themes, SelectedTheme[].class);
			request.getThemes().addAll(themeSet);
		}
		
		////////////////////////////////////
		// filtering logic (same as query actually)
		
		if (filterDate && !Strings.isNullOrEmpty(maxDueDateStr)) {
			request.setDueDateBefore(Utils.convertStringToDate(maxDueDateStr));
		}
		
		if (filterLg && ! Strings.isNullOrEmpty(lg)) {
			request.setLanguage(lg);
		}
		
		if (filterProx && rl != null && ! Strings.isNullOrEmpty(rl.getLatitude()) && ! Strings.isNullOrEmpty(rl.getLongitude()) && maxDistance != null) {
			GeographicCircle region = new GeographicCircle();
			region.setCenter(new GeoCoord());
			region.getCenter().setLatitude(Double.parseDouble(rl.getLatitude()));
			region.getCenter().setLongitude(Double.parseDouble(rl.getLongitude()));
			region.setRadiusInKm(maxDistance);
			request.setInGeographicRegion(region);
		}
		
		
		////////////////////////////////////
		// sorting logic
		if (!Strings.isNullOrEmpty(sortkey)) {
			try {
				request.setSortKey(SORT_KEY.valueOf(sortkey));
			} catch (Exception e) {
				logger.log(Level.WARNING, "unrecognized sort key: " + sortkey + ", ignoring");
			}
		}
		
		

		return request;
	}

	public boolean isEmpty() {
		return !hasSearchTerm() && !hasSearchTheme() && !hasSearchTag();
	}

	// ---------------------------------------
	
	public boolean hasSearchTerm() {
		return !Strings.isNullOrEmpty(term);
	}
	
	public boolean hasSearchTag() {
		return !Strings.isNullOrEmpty(tag);
	}
	
	public boolean hasSearchTheme() {
		return !Strings.isNullOrEmpty(themes);
	}
	
	// ---------------------------------------

	public String getTerm() {
		return term;
	}

	public void setTerm(String searchString) {
		this.term = searchString;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tags) {
		this.tag = tags;
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}

	public String getSortkey() {
		return sortkey;
	}

	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
	}

	public Location getRl() {
		return rl;
	}

	public void setRl(Location rl) {
		this.rl = rl;
	}

	public Double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public String getMaxDueDateStr() {
		return maxDueDateStr;
	}

	public void setMaxDueDateStr(String maxDueDateStr) {
		this.maxDueDateStr = maxDueDateStr;
	}

	public boolean isFilterProx() {
		return filterProx;
	}

	public void setFilterProx(boolean filterProx) {
		this.filterProx = filterProx;
	}

	public boolean isFilterDate() {
		return filterDate;
	}

	public void setFilterDate(boolean filterDate) {
		this.filterDate = filterDate;
	}

	public boolean isFilterLg() {
		return filterLg;
	}

	public void setFilterLg(boolean filterLg) {
		this.filterLg = filterLg;
	}

	public String getLg() {
		return lg;
	}

	public void setLg(String lg) {
		this.lg = lg;
	}

}
