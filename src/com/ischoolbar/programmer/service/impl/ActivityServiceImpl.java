package com.ischoolbar.programmer.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ischoolbar.programmer.dao.ActivityDao;
import com.ischoolbar.programmer.entity.Activity;
import com.ischoolbar.programmer.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private ActivityDao activityDao;

	@Override
	public int add(Activity activity) {
		return activityDao.add(activity);
	}

	@Override
	public int edit(Activity activity) {
		return activityDao.edit(activity);
	}

	@Override
	public int delete(Long id) {
		return activityDao.delete(id);
	}

	@Override
	public List<Activity> findList(Map<String, Object> queryMap) {
		return activityDao.findList(queryMap);
	}

	@Override
	public Integer getTotal(Map<String, Object> queryMap) {
		return activityDao.getTotal(queryMap);
	}

	@Override
	public Activity find(Long id) {
		return activityDao.find(id);
	}

	@Override
	public List<Activity> findAll() {
		return activityDao.findAll();
	}
}
