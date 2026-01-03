package com.ischoolbar.programmer.controller.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ischoolbar.programmer.entity.Activity;
import com.ischoolbar.programmer.page.admin.Page;
import com.ischoolbar.programmer.service.ActivityService;

/**
 * 活动管理后台控制器
 * @author ymj
 *
 */
@RequestMapping("/admin/activity")
@Controller
public class ActivityController {

	@Autowired
	private ActivityService activityService;

	/**
	 * 活动管理列表页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelAndView model){
		model.setViewName("activity/list");
		return model;
	}

	/**
	 * 活动信息添加操作
	 * @param activity
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(Activity activity){
		Map<String, String> ret = new HashMap<String, String>();
		if(activity == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的活动信息!");
			return ret;
		}
		if(StringUtils.isEmpty(activity.getTitle())){
			ret.put("type", "error");
			ret.put("msg", "活动标题不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(activity.getContent())){
			ret.put("type", "error");
			ret.put("msg", "活动内容不能为空!");
			return ret;
		}
		activity.setCreateTime(new Date());
		if(activityService.add(activity) <= 0){
			ret.put("type", "error");
			ret.put("msg", "添加失败，请联系管理员!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "添加成功!");
		return ret;
	}

	/**
	 * 活动信息编辑操作
	 * @param activity
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(Activity activity){
		Map<String, String> ret = new HashMap<String, String>();
		if(activity == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的活动信息!");
			return ret;
		}
		if(StringUtils.isEmpty(activity.getTitle())){
			ret.put("type", "error");
			ret.put("msg", "活动标题不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(activity.getContent())){
			ret.put("type", "error");
			ret.put("msg", "活动内容不能为空!");
			return ret;
		}
		if(activityService.edit(activity) <= 0){
			ret.put("type", "error");
			ret.put("msg", "编辑失败，请联系管理员!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "修改成功!");
		return ret;
	}

	/**
	 * 活动信息删除操作
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(Long id){
		Map<String, String> ret = new HashMap<String, String>();
		if(id == null){
			ret.put("type", "error");
			ret.put("msg", "请选择要删除的活动!");
			return ret;
		}
		if(activityService.delete(id) <= 0){
			ret.put("type", "error");
			ret.put("msg", "删除失败，请联系管理员!");
			return ret;
		}
		ret.put("type", "success");
		ret.put("msg", "删除成功!");
		return ret;
	}

	/**
	 * 分页查询活动信息
	 * @param title
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(
			@RequestParam(name="title",required=false,defaultValue="") String title,
			@RequestParam(name="status",required=false) Integer status,
			@RequestParam(name="page",required=false,defaultValue="1") Integer page,
			@RequestParam(name="rows",required=false,defaultValue="10") Integer rows
			){
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("title", title);
		if(status != null && status != -1){
			queryMap.put("status", status);
		}
		queryMap.put("offset", (page-1)*rows);
		queryMap.put("pageSize", rows);
		ret.put("rows", activityService.findList(queryMap));
		ret.put("total", activityService.getTotal(queryMap));
		return ret;
	}
}
