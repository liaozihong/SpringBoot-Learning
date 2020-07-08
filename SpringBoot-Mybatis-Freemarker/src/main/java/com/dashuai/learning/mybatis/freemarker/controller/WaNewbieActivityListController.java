package com.dashuai.learning.mybatis.freemarker.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dashuai.learning.mybatis.freemarker.entity.WaNewbieActivityList;
import com.dashuai.learning.mybatis.freemarker.service.WaNewbieActivityListService;
import com.dashuai.learning.mybatis.freemarker.utils.JSONParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 活动列表 前端控制器
 * </p>
 *
 * @author liaozihong
 * @since 2020-07-08
 */
@RestController
@RequestMapping("/waNewbieActivityList")
public class WaNewbieActivityListController {
    @Autowired
    WaNewbieActivityListService waNewbieActivityListService;

    @RequestMapping("/getList")
    public List<WaNewbieActivityList> getList(){
        List<WaNewbieActivityList> waNewbieActivityListList = waNewbieActivityListService.getBaseMapper().selectList(Wrappers.<WaNewbieActivityList>lambdaQuery());
        return waNewbieActivityListList;
    }
}
