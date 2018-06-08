package com.opens.datadictionary.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.opens.datadictionary.service.SearchService;
import com.opens.datadictionary.solr.dtos.SearchRequest;

import io.swagger.models.Swagger;

@Service
public class SolrSearchServiceImpl implements SearchService {

	private static final Logger logger = LoggerFactory.getLogger(SolrSearchServiceImpl.class);

	@Autowired
	private HttpSolrClient httpSolrClient;

	@Value("${solr.facet.fields}")
	private String facetFields;

	@Override
	public Map<String, Set<String>> getFacets() {
		Map<String, Set<String>> facets = new TreeMap<>();
		try {
			SolrQuery query = new SolrQuery();
			query.set(CommonParams.ROWS, Integer.MAX_VALUE);
			String queryText = "*:*";
			query.set("q", queryText);
			query.setFacet(true);
			query.setFields("id");
			query.addFacetField(facetFields.split(","));
			QueryResponse response = httpSolrClient.query(query);
			List<FacetField> facetFields = response.getFacetFields();
			for (FacetField facet : facetFields) {
				if (facet.getValueCount() > 0) {
					if (facets.get(facet.getName()) == null) {
						facets.put(facet.getName(), new TreeSet<String>());
					}
					facets.get(facet.getName())
							.addAll(facet.getValues().stream().map(obj -> obj.getName()).collect(Collectors.toSet()));
				}
			}
			logger.info("Total Records returned =  {} Records filenameIdMap = {}  ", facets.size(), facets);
		} catch (Exception e) {
			logger.error("Exception occured while searching in Solr = {} ", e);
		}
		return facets;
	}

	@Override
	public Map<String, List<String>> search(SearchRequest searchRequest) {
		logger.info("Search request is received in service = {} ", searchRequest);
		Map<String, List<String>> filenameIdMap = new HashMap<String, List<String>>();
		try {
			SolrQuery query = new SolrQuery();
			query.set(CommonParams.ROWS, Integer.MAX_VALUE);
			String queryText = "active:true ";
			if (searchRequest != null) {
				if (!StringUtils.isEmpty(searchRequest.getSearchText())) {
					searchRequest.setSearchText(searchRequest.getSearchText().toLowerCase());
					queryText = "AND (title:" + searchRequest.getSearchText();
					queryText += " OR description:" + searchRequest.getSearchText();
					queryText += " OR apiResource.methodName_tags:" + searchRequest.getSearchText();
					queryText += " OR apiResource.summary:" + searchRequest.getSearchText();
					queryText += " OR apiResource.description:" + searchRequest.getSearchText();
					queryText += " OR apiResource.metadata:" + searchRequest.getSearchText();
					queryText += ") ";
				}
			}
			String facetSearchQ = null;
			if (searchRequest.getSelectedFacets() != null && searchRequest.getSelectedFacets().size() > 0) {
				for (Entry<String, Set<String>> filterNameValue : searchRequest.getSelectedFacets().entrySet()) {
					if(filterNameValue.getValue() != null && filterNameValue.getValue().size() > 0) {
						if (facetSearchQ ==null || facetSearchQ.length() == 0) {
							facetSearchQ = filterNameValue.getKey()+":"+Joiner.on(" AND ").join(filterNameValue.getValue());
						} else {
							facetSearchQ += (" OR " + filterNameValue.getKey()+":"+Joiner.on(" AND ").join(filterNameValue.getValue()));
						}
					}
				}
			}
			queryText += " AND ("+facetSearchQ+")";
			query.set("q", queryText);
			QueryResponse response = httpSolrClient.query(query);
			SolrDocumentList docList = response.getResults();
			Integer count = 0;

			for (SolrDocument doc : docList) {
				String fileName = doc.get("fileName").toString();
				if (fileName != null) {
					if (filenameIdMap.containsKey(fileName)) {
						filenameIdMap.get(fileName).add(doc.get("id").toString());
					} else {
						filenameIdMap.put(fileName, new ArrayList<>());
						filenameIdMap.get(fileName).add(doc.get("id").toString());
					}
				}
				count++;
			}
			logger.info("Total Records returned =  {} Records filenameIdMap = {}  ", count, filenameIdMap);
		} catch (Exception e) {
			logger.error("Exception occured while searching in Solr = {} ", e);
		}
		return filenameIdMap;
	}

	@Override
	public Map<String, String> uploadedFiles() {
		Map<String, String> filenameIdMap = new HashMap<String, String>();
		try {
			SolrQuery query = new SolrQuery();
			query.set(CommonParams.ROWS, Integer.MAX_VALUE);
			String queryText = "*:*";
			query.set("q", queryText);
			query.setFields("fileName", "id");
			QueryResponse response = httpSolrClient.query(query);
			SolrDocumentList docList = response.getResults();
			for (SolrDocument doc : docList) {
				String fileName = doc.get("fileName").toString();
				if (fileName != null && !filenameIdMap.containsKey(fileName)) {
					filenameIdMap.put(fileName, doc.get("id").toString());
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured while getting uploaded swaggers = {} ", e);
		}
		return filenameIdMap;
	}

	@Override
	public boolean deactivateExisting(Swagger swagger) {
		String title = swagger.getInfo().getTitle();
		logger.info("Deactivating existing swagger = {} ", title);
		Map<String, List<String>> filenameIdMap = new HashMap<String, List<String>>();
		try {
			Integer count = 0;
			SolrQuery query = new SolrQuery();
			query.set(CommonParams.ROWS, Integer.MAX_VALUE);
			String queryText = "name:" + title;
			queryText += " active:true";

			query.set("q", queryText);
			QueryResponse response = httpSolrClient.query(query);
			SolrDocumentList docList = response.getResults();
			for (SolrDocument doc : docList) {
				SolrInputDocument solrDocument = new SolrInputDocument();
				solrDocument.setField("id", doc.get("id").toString());
				Map<String, Object> fieldModifier = new HashMap<>(1);
				fieldModifier.put("set", false);
				solrDocument.setField("active", fieldModifier);
				count++;
				httpSolrClient.add(solrDocument);
				httpSolrClient.commit();
			}
			logger.info("Total Records returned =  {} Records filenameIdMap = {}  ", count, filenameIdMap);
		} catch (Exception e) {
			logger.error("Exception occured while searching in Solr = {} ", e);
		}
		return true;
	}

}
