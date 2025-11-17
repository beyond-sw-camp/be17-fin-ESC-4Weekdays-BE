package com.fourweekdays.fourweekdays.search.controller;

import com.fourweekdays.fourweekdays.search.model.dto.request.SearchRequest;
import com.fourweekdays.fourweekdays.search.model.dto.response.SearchResponse;
import com.fourweekdays.fourweekdays.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/unified-search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService unifiedSearchService;

    @PostMapping
    public ResponseEntity<SearchResponse> searchPost(
            @RequestBody SearchRequest condition
    ) {
        SearchResponse response = unifiedSearchService.search(condition);
        return ResponseEntity.ok(response);
    }
}
