/**
 * 缓存简单实现
 *
 * 愿意消耗一些内存空间来提升速度
 * 预料到某些键会被多次查询
 * 缓存中存放的数据总量不会超出内存容量
 * 要更快的响应，缓存不需要网络 io（集中式缓存需要额外网络 io）
 * @author luna
 * 2021/8/18
 */
package com.luna.common.cache;
