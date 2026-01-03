package com.ischoolbar.programmer.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.ischoolbar.programmer.entity.Activity;

/**
 * 活动dao
 * @author ymj
 *
 */
@Repository
public interface ActivityDao {
	public int add(Activity activity);
	public int edit(Activity activity);
	public int delete(Long id);
	public List<Activity> findList(Map<String, Object> queryMap);
	public Integer getTotal(Map<String, Object> queryMap);
	public Activity find(Long id);
	public List<Activity> findAll();
}
