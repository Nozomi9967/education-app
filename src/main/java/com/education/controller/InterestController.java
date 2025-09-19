package com.education.controller;

import com.education.pojo.dto.InsertInterestDTO;
import com.education.pojo.dto.InsertPostDTO;
import com.education.result.Result;
import com.education.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 兴趣圈管理
 */
@RestController
@RequestMapping("/interest")
@Slf4j
public class InterestController {

    @Autowired
    private InterestService interestService;

    /**
     * 新增兴趣圈接口
     * @param insertInterestDTO
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增兴趣圈接口")
    public Result add(@Valid @RequestBody InsertInterestDTO insertInterestDTO) {
        return interestService.insert(insertInterestDTO);
    }

    /**
     * 删除兴趣圈
     * @param interestId
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除兴趣圈接口")
    public Result remove(@RequestParam Integer interestId) {
        return interestService.remove(interestId);
    }

    /**
     * 根据兴趣圈名查询接口
     * @param name
     * @return
     */
    @GetMapping("/queryByName")
    @Operation(summary = "根据兴趣圈名查询接口",description =
            "不加参数返回所有，按照参与人数降序返回，返回的数据排序规则为：1.关注了的优先排 2.越早关注越靠前 3.最后再按照参与人数排"
    )
    public Result queryByName(@RequestParam(required = false) String name) {
        return interestService.queryByName(name);
    }

    /**
     * 获取自己所关注的兴趣圈接口
     * @return
     */
    @GetMapping("/my-interest")
    @Operation(summary = "获取自己所关注的兴趣圈接口")
    public Result myInterest() {
        return interestService.myInterest();
    }

    /**
     * 关注兴趣圈接口
     * @param interestId
     * @return
     */
    @PostMapping("/follow-interest/{interestId}")
    @Operation(summary = "关注兴趣圈接口")
    public Result followInterest(@PathVariable Integer interestId) {
        return interestService.followInterest(interestId);
    }
}
