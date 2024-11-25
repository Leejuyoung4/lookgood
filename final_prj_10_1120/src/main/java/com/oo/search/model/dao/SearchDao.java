package com.oo.search.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.oo.search.model.dto.SearchResult;

@Mapper
public interface SearchDao {
    List<Map<String, String>> getAllTables();
    List<SearchResult> searchAllTables(@Param("query") String query, @Param("tables") List<Map<String, String>> tables);
}

