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

import com.ischoolbar.programmer.entity.BookOrder;
import com.ischoolbar.programmer.entity.RoomType;
import com.ischoolbar.programmer.entity.admin.User;
import com.ischoolbar.programmer.page.admin.Page;
import com.ischoolbar.programmer.service.AccountService;
import com.ischoolbar.programmer.service.BookOrderService;
import com.ischoolbar.programmer.service.RoomTypeService;
import com.ischoolbar.programmer.service.admin.LogService;

/**
 * 预定订单管理后台控制器
 * @author ymj
 *
 */
@RequestMapping("/admin/book_order")
@Controller
public class BookOrderController {
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private RoomTypeService roomTypeService;
	@Autowired
	private BookOrderService bookOrderService;
	@Autowired
	private LogService logService;
	
	
	/**
	 * 预定订单管理列表页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public ModelAndView list(ModelAndView model){
		model.addObject("roomTypeList", roomTypeService.findAll());
		model.addObject("accountList", accountService.findAll());
		model.setViewName("book_order/list");
		return model;
	}
	
	/**
	 * 预定订单信息添加操作
	 * @param bookOrder
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(BookOrder bookOrder){
		Map<String, String> ret = new HashMap<String, String>();
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的预定订单信息!");
			return ret;
		}
		if(bookOrder.getAccountId() == null){
			ret.put("type", "error");
			ret.put("msg", "客户不能为空!");
			return ret;
		}
		if(bookOrder.getRoomTypeId() == null){
			ret.put("type", "error");
			ret.put("msg", "房型不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getName())){
			ret.put("type", "error");
			ret.put("msg", "预定订单联系人名称不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getMobile())){
			ret.put("type", "error");
			ret.put("msg", "预定订单联系人手机号不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getIdCard())){
			ret.put("type", "error");
			ret.put("msg", "联系人身份证号不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getArriveDate())){
			ret.put("type", "error");
			ret.put("msg", "到达时间不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getLeaveDate())){
			ret.put("type", "error");
			ret.put("msg", "离店时间不能为空!");
			return ret;
		}
		bookOrder.setCreateTime(new Date());
		if(bookOrderService.add(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "添加失败，请联系管理员!");
			return ret;
		}
		RoomType roomType = roomTypeService.find(bookOrder.getRoomTypeId());
		//预定成功后去修改该房型的预定数
		if(roomType != null){
			roomType.setBookNum(roomType.getBookNum() + 1);
			roomType.setAvilableNum(roomType.getAvilableNum() - 1);
			roomTypeService.updateNum(roomType);
			//如果可用的房间数为0，则设置该房型状态已满
			if(roomType.getAvilableNum() == 0){
				roomType.setStatus(0);
				roomTypeService.edit(roomType);
			}
		}
		ret.put("type", "success");
		ret.put("msg", "添加成功!");
		return ret;
	}
	
	/**
	 * 预定订单信息编辑操作
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(BookOrder bookOrder){
		Map<String, String> ret = new HashMap<String, String>();
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的预定订单信息!");
			return ret;
		}
		if(bookOrder.getAccountId() == null){
			ret.put("type", "error");
			ret.put("msg", "客户不能为空!");
			return ret;
		}
		if(bookOrder.getRoomTypeId() == null){
			ret.put("type", "error");
			ret.put("msg", "房型不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getName())){
			ret.put("type", "error");
			ret.put("msg", "预定订单联系人名称不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getMobile())){
			ret.put("type", "error");
			ret.put("msg", "预定订单联系人手机号不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getIdCard())){
			ret.put("type", "error");
			ret.put("msg", "联系人身份证号不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getArriveDate())){
			ret.put("type", "error");
			ret.put("msg", "到达时间不能为空!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getLeaveDate())){
			ret.put("type", "error");
			ret.put("msg", "离店时间不能为空!");
			return ret;
		}
		BookOrder existBookOrder = bookOrderService.find(bookOrder.getId());
		if(existBookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "请选择正确的数据进行编辑!");
			return ret;
		}
		// 已入住/已完成订单不允许修改，避免与入住/退房联动产生不一致
		if(existBookOrder.getStatus() == 2 || existBookOrder.getStatus() == 3){
			ret.put("type", "error");
			ret.put("msg", "已入住或已完成的订单不允许修改!");
			return ret;
		}
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "编辑失败，请联系管理员!");
			return ret;
		}
		//判断房型是否发生变化
		if(existBookOrder.getRoomTypeId().longValue() != bookOrder.getRoomTypeId().longValue()){
			//房型发生了变化
			//首先恢复原来房型的预定数及可用数
			RoomType oldRoomType = roomTypeService.find(existBookOrder.getRoomTypeId());
			oldRoomType.setAvilableNum(oldRoomType.getAvilableNum() + 1);
			oldRoomType.setBookNum(oldRoomType.getBookNum() - 1);
			roomTypeService.updateNum(oldRoomType);
			if(oldRoomType.getStatus() == 0){
				//旧的房间原来是满房，现在不满房的话，恢复状态
				if(oldRoomType.getAvilableNum() > 0){
					//设置成状态可用
					oldRoomType.setStatus(1);
					roomTypeService.edit(oldRoomType);
				}
			}
			//修改新的房型的可用数和预定数
			RoomType newRoomType = roomTypeService.find(bookOrder.getRoomTypeId());
			newRoomType.setAvilableNum(newRoomType.getAvilableNum() - 1);
			newRoomType.setBookNum(newRoomType.getBookNum() + 1);
			roomTypeService.updateNum(newRoomType);
			if(newRoomType.getAvilableNum() <= 0){
				//没有可用房间数
				newRoomType.setStatus(0);//设置成满房
				roomTypeService.edit(newRoomType);
			}
		}
		ret.put("type", "success");
		ret.put("msg", "修改成功!");
		return ret;
	}

	/**
	 * 确认订单（待确认 -> 已确认）
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/confirm",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> confirm(Long id, javax.servlet.http.HttpServletRequest request){
		Map<String, String> ret = new HashMap<String, String>();
		if(id == null){
			ret.put("type", "error");
			ret.put("msg", "请选择要确认的订单!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "订单不存在!");
			return ret;
		}
		if(bookOrder.getStatus() != 0){
			ret.put("type", "error");
			ret.put("msg", "只有待确认订单才能确认!");
			return ret;
		}
		bookOrder.setStatus(1);
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "确认失败，请联系管理员!");
			return ret;
		}
		User admin = (User)request.getSession().getAttribute("admin");
		if(admin != null){
			logService.add("用户名为{"+admin.getUsername()+"}的员工确认了预定订单{"+id+"}!");
		}
		ret.put("type", "success");
		ret.put("msg", "确认成功!");
		return ret;
	}

	/**
	 * 取消订单（待确认/已确认 -> 已取消）
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/cancel",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> cancel(Long id, javax.servlet.http.HttpServletRequest request){
		Map<String, String> ret = new HashMap<String, String>();
		if(id == null){
			ret.put("type", "error");
			ret.put("msg", "请选择要取消的订单!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "订单不存在!");
			return ret;
		}
		if(bookOrder.getStatus() != 0 && bookOrder.getStatus() != 1){
			ret.put("type", "error");
			ret.put("msg", "当前订单状态不允许取消!");
			return ret;
		}
		bookOrder.setStatus(4);
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "取消失败，请联系管理员!");
			return ret;
		}
		RoomType roomType = roomTypeService.find(bookOrder.getRoomTypeId());
		if(roomType != null){
			//恢复房型的预定数与可用数
			if(roomType.getBookNum() != null && roomType.getBookNum() > 0){
				roomType.setBookNum(roomType.getBookNum() - 1);
			}
			if(roomType.getAvilableNum() != null){
				roomType.setAvilableNum(roomType.getAvilableNum() + 1);
			}
			roomTypeService.updateNum(roomType);
			//原来是满房，现在恢复可用
			if(roomType.getStatus() == 0 && roomType.getAvilableNum() != null && roomType.getAvilableNum() > 0){
				roomType.setStatus(1);
				roomTypeService.edit(roomType);
			}
		}
		User admin = (User)request.getSession().getAttribute("admin");
		if(admin != null){
			logService.add("用户名为{"+admin.getUsername()+"}的员工取消了预定订单{"+id+"}!");
		}
		ret.put("type", "success");
		ret.put("msg", "取消成功!");
		return ret;
	}

	/**
	 * 删除订单（仅允许删除已取消订单，避免数量联动重复回滚）
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> delete(Long id, javax.servlet.http.HttpServletRequest request){
		Map<String, String> ret = new HashMap<String, String>();
		if(id == null){
			ret.put("type", "error");
			ret.put("msg", "请选择要删除的订单!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "订单不存在!");
			return ret;
		}
		if(bookOrder.getStatus() != 4){
			ret.put("type", "error");
			ret.put("msg", "仅允许删除已取消订单，请先取消订单再删除!");
			return ret;
		}
		if(bookOrderService.delete(id) <= 0){
			ret.put("type", "error");
			ret.put("msg", "删除失败，请联系管理员!");
			return ret;
		}
		User admin = (User)request.getSession().getAttribute("admin");
		if(admin != null){
			logService.add("用户名为{"+admin.getUsername()+"}的员工删除了预定订单{"+id+"}!");
		}
		ret.put("type", "success");
		ret.put("msg", "删除成功!");
		return ret;
	}
	
	/**
	 * 分页查询预定订单信息
	 * @param name
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> list(
			@RequestParam(name="name",defaultValue="") String name,
			@RequestParam(name="accountId",defaultValue="") Long accountId,
			@RequestParam(name="roomTypeId",defaultValue="") Long roomTypeId,
			@RequestParam(name="idCard",defaultValue="") String idCard,
			@RequestParam(name="mobile",defaultValue="") String mobile,
			@RequestParam(name="status",required=false) Integer status,
			Page page
			){
		Map<String,Object> ret = new HashMap<String, Object>();
		Map<String,Object> queryMap = new HashMap<String, Object>();
		queryMap.put("name", name);
		queryMap.put("status", status);
		queryMap.put("accountId", accountId);
		queryMap.put("roomTypeId", roomTypeId);
		queryMap.put("idCard", idCard);
		queryMap.put("mobile", mobile);
		queryMap.put("offset", page.getOffset());
		queryMap.put("pageSize", page.getRows());
		ret.put("rows", bookOrderService.findList(queryMap));
		ret.put("total", bookOrderService.getTotal(queryMap));
		return ret;
	}
	
	
}
