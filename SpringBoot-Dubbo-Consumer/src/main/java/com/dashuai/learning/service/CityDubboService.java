package com.dashuai.learning.service;


import com.dashuai.learning.domain.City;

/**
 * 城市业务 Dubbo 服务层
 * <p>
 * 注意此服务类要与Provider的服务类包名一样，不然dubbo会认为这是不同服务的消费者和提供者
 *
 * @author Liaozihong
 */
public interface CityDubboService {

    /**
     * 根据城市名称，查询城市信息
     *
     * @param cityName the city name
     * @return the city
     */
    City findCityByName(String cityName);

    /**
     * Mmb method string.
     *
     * @param name the name
     * @return the string
     */
    String mmbMethod(String name);
}
