package com.example.elderlycare.service;

import com.example.elderlycare.dto.response.HomeDataResponse;

/**
 * 首页数据服务接口
 */
public interface HomeDataService {

    /**
     * 获取首页数据
     */
    HomeDataResponse getHomeData(Integer userId);
}