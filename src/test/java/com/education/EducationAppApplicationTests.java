package com.education;

import com.education.pojo.vo.PostVO;
import com.education.service.SynonymSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EducationAppApplicationTests {

    @Autowired
    SynonymSearchService synonymSearchService;

    @Test
    void contextLoads() {
        List<PostVO> list = synonymSearchService.search("美食", 2);
        list.forEach(System.out::println);
    }

}
