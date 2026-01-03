package com.ischoolbar.programmer.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ischoolbar.programmer.entity.Activity;

/**
 * 活动service
 * @author ymj
 *
 */
@Service
public interface ActivityService {
	public int add(Activity activity);
	public int edit(Activity activity);
	public int delete(Long id);
	public List<Activity> findList(Map<String, Object> queryMap);
	public Integer getTotal(Map<String, Object> queryMap);
	public Activity find(Long id);
	public List<Activity> findAll();
}
