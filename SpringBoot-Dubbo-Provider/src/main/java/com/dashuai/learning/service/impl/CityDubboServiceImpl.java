package com.dashuai.learning.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashuai.learning.domain.City;
import com.dashuai.learning.service.CityDubboService;


/**
 * City dubbo service
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class CityDubboServiceImpl implements CityDubboService {
    @Override
    public City findCityByName(String cityName) {
        return new City(1L, 2L, "广州", "催人成长的地方");
    }

    @Override
    public String mmbMethod(String name) {
        StringBuilder sb = new StringBuilder();
        return sb.append(name).append("尼玛卖批的！").toString();
    }
}
