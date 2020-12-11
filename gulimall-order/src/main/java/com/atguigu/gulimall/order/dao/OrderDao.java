package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author stronger
 * @email 123456@gmail.com
 * @date 2020-12-11 21:03:48
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
