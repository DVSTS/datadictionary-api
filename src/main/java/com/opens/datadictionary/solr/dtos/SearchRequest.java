package com.opens.datadictionary.solr.dtos;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class SearchRequest implements Serializable {

	private static final long serialVersionUID = 4757960866089546166L;

	private String searchText;

	private Map<String, Set<String>> selectedFacets;

	public Map<String, Set<String>> getSelectedFacets() {
		return selectedFacets;
	}

	public void setSelectedFacets(Map<String, Set<String>> selectedFacets) {
		this.selectedFacets = selectedFacets;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

}
