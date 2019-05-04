package com.dashuai.learning.nacos.config.api;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * 使用命令在nacos上创建一个配置项目，将配置存放进去
 * curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=example&group=DEFAULT_GROUP&content=useLocalCache=true"
 * 当然也可直接在管理平台上手动创建一个配置存放项目。
 */
@Controller
@RequestMapping("config")
public class ConfigController {

    @NacosValue(value = "${useLocalCache:false}", autoRefreshed = true)
    boolean useLocalCache;

    @NacosValue(value = "${property.name}", autoRefreshed = true)
    String propertyName;

    @RequestMapping(value = "/get", method = GET)
    @ResponseBody
    public String get() {
        return useLocalCache + "  " + propertyName;
    }
}