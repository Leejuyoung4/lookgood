package com.oo.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oo.search.model.dto.SearchResult;
import com.oo.search.model.service.SearchService;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class SearchController {
	
	@Autowired
	private SearchService searchService;
	
	@GetMapping()
	public ResponseEntity<?> search(@RequestParam(required = false) String query) {
	    if (query == null || query.trim().isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter is missing or empty");
	    }
	    try {
	    	System.out.println("Received query: " + query);

	        List<SearchResult> results = searchService.searchAllTables(query);
	        return ResponseEntity.ok(results);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("검색 중 오류 발생: " + e.getMessage());
	    }
	}
	
}
