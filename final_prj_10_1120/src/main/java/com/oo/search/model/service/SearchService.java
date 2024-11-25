package com.oo.search.model.service;

import java.util.List;

import com.oo.search.model.dto.SearchResult;


public interface SearchService {

	List<SearchResult> searchAllTables(String query);

}
