package com.education.service;

import java.util.List;

/**
 * 分词服务接口
 */
public interface SegmentService {

    /**
     * 对文本进行分词
     * @param text 要分词的文本
     * @return 分词结果列表
     */
    List<String> segment(String text);

    /**
     * 对文本进行分词，过滤停用词
     * @param text 要分词的文本
     * @return 过滤停用词后的分词结果列表
     */
    List<String> segmentWithStopWordFilter(String text);
}