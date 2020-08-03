package com.dashuai.learnin.elasticsearchadvance.api;

import com.dashuai.learnin.elasticsearchadvance.model.FilmEntity;
import com.dashuai.learnin.elasticsearchadvance.service.impl.FilmService;
import com.dashuai.learning.utils.result.ApiResult;
import com.google.common.base.Strings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Film api
 * <p/>
 * Created in 2018.12.19
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@Api(value = "SpringBoot集成ElasticSearch查询测试接口", tags = "FilmApi")
public class FilmApi {

    @Autowired
    private FilmService filmService;

    /**
     * Search api result.
     *
     * @param name the name
     * @return the api result
     */
    @GetMapping("/search/{name}")
    @ApiOperation(value = "查询特定name", notes = "普通查询", response = ApiResult.class)
    public ApiResult search(@PathVariable String name) {
        if (!Strings.isNullOrEmpty(name)) {
            return ApiResult.prepare().success(filmService.search(name, ""));
        }
        return null;
    }

    /**
     * Search highlight api result.
     *
     * @param name the name
     * @return the api result
     */
    @GetMapping("/search/highlight/{name}")
    @ApiOperation(value = "查询特定name", notes = "高亮显示", response = ApiResult.class)
    public ApiResult searchHighlight(@PathVariable String name) {
        if (!Strings.isNullOrEmpty(name)) {
            return ApiResult.prepare().success(filmService.searchHinghlight(name, ""));
        }
        return null;
    }

    /**
     * Save api result.
     *
     * @param filmEntity the film entity
     * @return the api result
     */
    @PostMapping("save")
    @ApiOperation(value = "添加Film", response = ApiResult.class)
    @ApiImplicitParam(name = "filmEntity", value = "电影实体", required = true, dataType = "FilmEntity")
    public ApiResult save(@RequestBody FilmEntity filmEntity) {
        filmService.save(filmEntity);
        return ApiResult.prepare().success("处理成功");
    }

    /**
     * Gets analyze result.
     *
     * @param content     the content
     * @param analyzeType the analyze type
     * @return the analyze result
     */
    @GetMapping("/getAnalyzeResult")
    @ApiOperation(value = "测试分词结果", notes = "提供接口用于查看分词结果", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "分词内容", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "analyzeType", value = "分词类型", dataType = "String", paramType = "query")
    })
    public ApiResult getAnalyzeResult(@RequestParam(name = "content") String content, @RequestParam(name = "analyzeType", required = false) String analyzeType) {
        return ApiResult.prepare().success(filmService.getAnalyzeResult(content, analyzeType));
    }
}