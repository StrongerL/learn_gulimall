package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author stronger
 * @email 123456@gmail.com
 * @date 2020-12-11 20:24:14
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}