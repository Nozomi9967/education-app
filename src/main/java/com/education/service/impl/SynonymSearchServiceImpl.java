package com.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.context.BaseContext;
import com.education.mapper.PostMapper;
import com.education.mapper.UserMapper;
import com.education.pojo.entity.Post;
import com.education.pojo.entity.Synonym;
import com.education.pojo.vo.PostVO;
import com.education.service.SegmentService;
import com.education.service.SynonymSearchService;
import com.education.service.SynonymService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SynonymSearchServiceImpl implements SynonymSearchService {

    // 同义词缓存前缀
    private static final String SYNONYM_CACHE_PREFIX = "synonym:keyword:";
    // 缓存过期时间（30分钟）
    private static final long CACHE_EXPIRE_MINUTES = 30;

    // 依赖注入：MyBatis-Plus Mapper
    @Autowired
    private SynonymService synonymService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;
//    @Autowired
//    private SearchLogMapper searchLogMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SegmentService segmentService;

    // 初始化时加载热门同义词到缓存
    @PostConstruct
    public void initSynonymCache() {
        // 查询热门关键词的同义词（例如搜索量前100的关键词）
        List<String> hotKeywords = Arrays.asList("编程", "Java", "数学", "英语", "物理");

        // 批量查询并缓存
        Map<String, List<String>> hotSynonyms = new HashMap<>();
        for (String keyword : hotKeywords) {
            List<Synonym> synonyms = synonymService.selectByKeyword(keyword);
            List<String> synonymWords = synonyms.stream()
                    .map(Synonym::getSynonymWord)
                    .collect(Collectors.toList());
            hotSynonyms.put(keyword, synonymWords);

            // 存入Redis缓存
            redisTemplate.opsForValue().set(
                    SYNONYM_CACHE_PREFIX + keyword,
                    synonymWords,
                    CACHE_EXPIRE_MINUTES,
                    java.util.concurrent.TimeUnit.MINUTES
            );
        }
    }

    /**
     * 核心搜索方法
     */
    @Override
    public List<PostVO> search(String query, int topN) {
        String userId = BaseContext.getCurrentId();
        // 1. 权限校验
        checkPermission(userId);

        // 2. 分词处理（过滤停用词）
        List<String> words = segmentService.segmentWithStopWordFilter(query);
        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 扩展同义词
        Set<String> expandedWords = expandKeywords(words);
        // 包含原始词和同义词
        expandedWords.addAll(words);

        // 4. 查询匹配的文档
        List<Post> documents = queryDocuments(expandedWords);
        if (documents.isEmpty()) {
            return Collections.emptyList();
        }

        // 5. 计算文档得分并排序
        List<Post> scoredDocs = calculateAndSortScores(documents, expandedWords);

//        // 6. 个性化排序（基于用户学习偏好）
//        List<Post> sortedDocs = personalizeSort(scoredDocs, userId);
//
//        // 7. 记录搜索日志
//        logSearch(query, userId, sortedDocs);

        // 8. 转换为DTO并返回前N条
        return convertToDTO(scoredDocs).stream()
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * 扩展关键词为包含同义词的集合
     */
    @Override
    public Set<String> expandKeywords(List<String> words) {
        Set<String> expandedWords = new HashSet<>();

        for (String word : words) {
            // 1. 先查缓存
            List<String> synonyms = (List<String>) redisTemplate.opsForValue().get(SYNONYM_CACHE_PREFIX + word);

            // 2. 缓存未命中则查数据库
            if (synonyms == null) {
                List<Synonym> dbSynonyms = synonymService.selectByKeyword(word);
                synonyms = dbSynonyms.stream()
                        .map(Synonym::getSynonymWord)
                        .collect(Collectors.toList());

                // 写入缓存
                redisTemplate.opsForValue().set(
                        SYNONYM_CACHE_PREFIX + word,
                        synonyms,
                        CACHE_EXPIRE_MINUTES,
                        java.util.concurrent.TimeUnit.MINUTES
                );
            }

            expandedWords.addAll(synonyms);
        }

        return expandedWords;
    }

    /**
     * 查询包含关键词的文档
     */
    private List<Post> queryDocuments(Set<String> keywords) {
        // 构建查询条件：标题或内容包含任一关键词
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wq -> {
            for (String keyword : keywords) {
                wq.or().like("title", keyword)
                        .or().like("content", keyword);
            }
        });
        // 只查询已发布的文档
        queryWrapper.eq("status", 1);

        return postMapper.selectList(queryWrapper);
    }

    /**
     * 计算文档得分并排序
     */
    private List<Post> calculateAndSortScores(List<Post> documents, Set<String> keywords) {
        // 计算每个文档的匹配得分
        Map<Post, Double> docScores = new HashMap<>();

        for (Post doc : documents) {
            double score = 0.0;

            // 标题匹配权重更高（1.5倍）
            for (String keyword : keywords) {
                if (doc.getTitle().contains(keyword)) {
                    score += 1.5;
                }
                if (doc.getContent().contains(keyword)) {
                    score += 1.0;
                }
            }

            // 结合文档本身的热度（浏览量、点赞数等）
            score += doc.getForwardCount() * 0.003;
            score += doc.getLikeCount() * 0.002;

            docScores.put(doc, score);
        }

        // 按得分降序排序
        return docScores.entrySet().stream()
                .sorted(Map.Entry.<Post, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 个性化排序（基于用户学习偏好）
     */
//    private List<Post> personalizeSort(List<Post> documents, Long userId) {
//        if (userId == null) {
//            return documents; // 未登录用户不做个性化处理
//        }
//
//        // 查询用户偏好的学科
//        User user = userMapper.selectById(userId);
//        if (user == null || user.getPreferSubjects() == null) {
//            return documents;
//        }
//
//        Set<String> preferSubjects = new HashSet<>(Arrays.asList(user.getPreferSubjects().split(",")));
//
//        // 对偏好学科的文档增加权重
//        return documents.stream()
//                .sorted((doc1, doc2) -> {
//                    boolean doc1InPrefer = preferSubjects.contains(doc1.getSubject());
//                    boolean doc2InPrefer = preferSubjects.contains(doc2.getSubject());
//
//                    if (doc1InPrefer && !doc2InPrefer) {
//                        return -1; // 优先展示偏好学科
//                    } else if (!doc1InPrefer && doc2InPrefer) {
//                        return 1;
//                    } else {
//                        return 0; // 其他保持原有排序
//                    }
//                })
//                .collect(Collectors.toList());
//    }

    /**
     * 记录搜索日志（带事务）
     */
//    @Transactional(rollbackFor = Exception.class)
//    private void logSearch(String query, Long userId, List<Post> results) {
//        SearchLog log = new SearchLog();
//        log.setUserId(userId);
//        log.setQueryContent(query);
//        log.setResultCount(results.size());
//        log.setSearchTime(new Date());
//        log.setIpAddress(getClientIp()); // 需要实现获取客户端IP的方法
//
//        // 记录搜索到的文档ID（取前5个）
//        String resultIds = results.stream()
//                .limit(5)
//                .map(doc -> doc.getId().toString())
//                .collect(Collectors.joining(","));
//        log.setResultIds(resultIds);
//
//        searchLogMapper.insert(log);
//    }

    /**
     * 转换为DTO
     */
    private List<PostVO> convertToDTO(List<Post> documents) {
        return documents.stream()
                .map(doc -> {
                    PostVO dto = new PostVO();
                    // 使用Bean转换工具复制属性
                    BeanUtils.copyProperties(doc, dto);
                    // 处理特殊字段
                    dto.setContent(generateSummary(doc.getContent()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 生成内容摘要
     */
    private String generateSummary(String content) {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    /**
     * 权限校验
     */
    private void checkPermission(String userId) {
        // 示例：禁止未登录用户搜索某些敏感内容
        if (userId == null) {
            // 实际业务中可根据query内容判断是否需要权限
        }
    }

    /**
     * 获取客户端IP（需要根据实际Web环境实现）
     */
    private String getClientIp() {
        // Web环境下可通过RequestContextHolder获取
        return "127.0.0.1"; // 示例值
    }
}
