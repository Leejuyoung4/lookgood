package com.oo.search.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oo.search.model.dao.SearchDao;
import com.oo.search.model.dto.SearchResult;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;

	@Override
	public List<SearchResult> searchAllTables(String query) {
		try {
			// 테이블과 컬럼 정보 가져오기
			List<Map<String, String>> tables = searchDao.getAllTables();
			if (tables == null || tables.isEmpty()) {
				throw new RuntimeException("검색 가능한 테이블 및 컬럼 정보가 없습니다.");
			}

			// 디버깅용 출력
			System.out.println("Tables and Columns: " + tables);

			// 검색 실행
			List<SearchResult> results = searchDao.searchAllTables(query, tables);
			if (results.isEmpty()) {
				System.out.println("Query [" + query + "]에 해당하는 결과가 없습니다.");
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace(); // 디버깅용
			throw new RuntimeException("검색 중 오류 발생: " + e.getMessage(), e);
		}
	}
}