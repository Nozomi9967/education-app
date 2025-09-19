package com.education.service;

import com.education.pojo.vo.PostVO;

import java.util.List;
import java.util.Set;

public interface SynonymSearchService {

    List<PostVO> search(String query, int topN);

    Set<String> expandKeywords(List<String> words);
}
