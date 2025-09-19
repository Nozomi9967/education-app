package com.education.service.impl;

import com.education.service.SegmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于IKAnalyzer的分词服务实现（实际项目推荐方案）
 * 支持：精准分词/智能分词、停用词过滤
 * 注意：由于IKAnalyzer版本API变化，移除了动态词典加载功能
 */
@Service
@Slf4j
public class SegmentServiceImpl implements SegmentService {

    // 停用词集合（内存缓存，避免频繁IO）
    private final Set<String> stopWords = new HashSet<>();

    // 停用词文件路径（从配置文件注入）
    @Value("${segment.stopwords.path:classpath:stopwords.txt}")
    private Resource stopWordsResource;

    /**
     * 初始化方法：加载停用词
     * 注解@PostConstruct确保在服务初始化时执行
     */
    @PostConstruct
    public void init() {
        try {
            // 加载停用词
            loadStopWords();
            log.info("分词服务初始化完成");
        } catch (IOException e) {
            log.error("分词服务初始化失败：加载停用词出错", e);
            throw new RuntimeException("分词服务初始化失败：加载停用词出错", e);
        }
    }

    /**
     * 加载停用词到内存集合
     */
    private void loadStopWords() throws IOException {
        if (!stopWordsResource.exists()) {
            log.warn("停用词文件不存在：{}，将使用默认停用词", stopWordsResource.getDescription());
            // 添加一些默认的停用词
            addDefaultStopWords();
            return;
        }

        try (InputStream is = stopWordsResource.getInputStream();
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) { // 忽略空行和注释行（#开头）
                    stopWords.add(line);
                }
            }
            log.info("成功加载停用词 {} 个", stopWords.size());
        }
    }

    /**
     * 添加默认停用词
     */
    private void addDefaultStopWords() {
        String[] defaultStopWords = {
                "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一",
                "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有",
                "看", "好", "自己", "这", "那", "它", "他", "她", "但是", "因为", "所以",
                "如果", "虽然", "然后", "可以", "应该", "或者", "而且", "但", "与", "及"
        };

        for (String word : defaultStopWords) {
            stopWords.add(word);
        }
        log.info("添加默认停用词 {} 个", defaultStopWords.length);
    }

    /**
     * 基础分词方法（使用IKAnalyzer的智能分词模式）
     * @param text 待分词文本
     * @return 分词结果列表
     */
    @Override
    public List<String> segment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<>();
        // 使用智能分词模式（true=智能分词，false=精准分词）
        try (StringReader reader = new StringReader(text)) {
            IKSegmenter segmenter = new IKSegmenter(reader, true);
            Lexeme lexeme;
            // 循环获取分词结果
            while ((lexeme = segmenter.next()) != null) {
                String word = lexeme.getLexemeText();
                if (word != null && !word.trim().isEmpty()) {
                    result.add(word);
                }
            }
        } catch (IOException e) {
            log.error("分词失败，文本：{}", text, e);
            return new ArrayList<>(0);
        }
        return result;
    }

    /**
     * 分词并过滤停用词
     * @param text 待分词文本
     * @return 过滤后的分词结果
     */
    @Override
    public List<String> segmentWithStopWordFilter(String text) {
        List<String> segmentedWords = segment(text);
        if (segmentedWords.isEmpty()) {
            return new ArrayList<>(0);
        }

        // 过滤停用词和长度为1的无意义词（如"的"、"了"，但保留有意义的单字词如"数"、"学"）
        List<String> result = new ArrayList<>(segmentedWords.size());
        for (String word : segmentedWords) {
            // 过滤条件：不是停用词，且长度大于1（或特殊单字词例外）
            if (!stopWords.contains(word) && shouldKeepWord(word)) {
                result.add(word);
            }
        }
        return result;
    }

    /**
     * 判断是否应该保留该词
     * @param word 待判断的词
     * @return true表示保留，false表示过滤
     */
    private boolean shouldKeepWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        // 过滤纯数字、纯标点符号
        if (word.matches("^[0-9]+$") || word.matches("^[\\p{Punct}]+$")) {
            return false;
        }

        // 保留长度大于1的词，或者是有意义的单字词（可根据业务需要调整）
        if (word.length() > 1) {
            return true;
        }

        // 定义一些有意义的单字词（教育领域相关）
        Set<String> meaningfulSingleWords = Set.of("数", "学", "语", "英", "理", "化", "生", "史", "地", "政");
        return meaningfulSingleWords.contains(word);
    }

    /**
     * 精准分词方法
     * @param text 待分词文本
     * @return 分词结果列表
     */
    public List<String> segmentSmart(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<>();
        // 使用精准分词模式（false=精准分词）
        try (StringReader reader = new StringReader(text)) {
            IKSegmenter segmenter = new IKSegmenter(reader, false);
            Lexeme lexeme;
            while ((lexeme = segmenter.next()) != null) {
                String word = lexeme.getLexemeText();
                if (word != null && !word.trim().isEmpty()) {
                    result.add(word);
                }
            }
        } catch (IOException e) {
            log.error("精准分词失败，文本：{}", text, e);
            return new ArrayList<>(0);
        }
        return result;
    }
}