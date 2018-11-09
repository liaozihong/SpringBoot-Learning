package com.dashuai.learning.service;


import com.dashuai.learning.domain.City;

/**
 * The interface City dubbo service.
 * <p/>
 * Created in 2018.11.09
 * <p/>
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
