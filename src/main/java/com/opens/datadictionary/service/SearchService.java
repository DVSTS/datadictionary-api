package com.opens.datadictionary.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opens.datadictionary.solr.dtos.SearchRequest;

import io.swagger.models.Swagger;

public interface SearchService {

	Map<String, List<String>> search(SearchRequest searchRequest);
	
	Map<String, String> uploadedFiles();

	boolean deactivateExisting(Swagger swagger);

	Map<String, Set<String>> getFacets();
}
