package com.education.pojo.vo;

import com.education.pojo.entity.SearchHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "用于展示用户搜索页面个性化信息的类")
public class SearchPersonalInfoVO implements Serializable {
    private List<SearchHistory>  searchHistoryList;
    private List<String> searchFoundList;
}
