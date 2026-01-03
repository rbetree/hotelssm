package com.ischoolbar.programmer.controller.home;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ischoolbar.programmer.entity.Account;
import com.ischoolbar.programmer.entity.BookOrder;
import com.ischoolbar.programmer.entity.RoomType;
import com.ischoolbar.programmer.service.AccountService;
import com.ischoolbar.programmer.service.BookOrderService;
import com.ischoolbar.programmer.service.RoomTypeService;

/**
 * 前台用户控制器
 * @author ymj
 *
 */
@RequestMapping("/home/account")
@Controller
public class HomeAccountController {
	
	@Autowired
	private RoomTypeService roomTypeService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private BookOrderService bookOrderService;
	
	/**
	 * 前台用户中心首页
	 * @param model
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView list(ModelAndView model,HttpServletRequest request,
			@RequestParam(name="status",required=false) Integer status,
			@RequestParam(name="roomTypeId",required=false) Long roomTypeId,
			@RequestParam(name="arriveDateStart",defaultValue="") String arriveDateStart,
			@RequestParam(name="leaveDateEnd",defaultValue="") String leaveDateEnd
			){
		Account account = (Account)request.getSession().getAttribute("account");
		Map<String,Object> queryMap = new HashMap<String, Object>();
		queryMap.put("accountId", account.getId());
		if(status != null && status.intValue() == -1) status = null;
		if(roomTypeId != null && roomTypeId.longValue() == -1) roomTypeId = null;
		if(!StringUtils.isEmpty(arriveDateStart)) queryMap.put("arriveDateStart", arriveDateStart);
		if(!StringUtils.isEmpty(leaveDateEnd)) queryMap.put("leaveDateEnd", leaveDateEnd);
		queryMap.put("status", status);
		queryMap.put("roomTypeId", roomTypeId);
		queryMap.put("offset", 0);
		queryMap.put("pageSize", 999);
		model.addObject("bookOrderList", bookOrderService.findList(queryMap));
		model.addObject("roomTypeList", roomTypeService.findAll());
		model.addObject("status", status == null ? -1 : status);
		model.addObject("roomTypeId", roomTypeId == null ? -1 : roomTypeId);
		model.addObject("arriveDateStart", arriveDateStart);
		model.addObject("leaveDateEnd", leaveDateEnd);
		model.setViewName("home/account/index");
		return model;
	}
	
	/**
	 * 预定房间页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/book_order",method=RequestMethod.GET)
	public ModelAndView bookOrder(ModelAndView model,Long roomTypeId
			){
		model.addObject("roomType", roomTypeService.find(roomTypeId));
		model.setViewName("home/account/book_order");
		return model;
	}
	
	
	/**
	 * 预定信息提交
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/book_order",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,String> bookOrderAct(BookOrder bookOrder,HttpServletRequest request){
		Map<String, String> ret = new HashMap<String, String>();
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "请填写正确的预定订单信息!");
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
		if(bookOrder.getArriveDate().compareTo(bookOrder.getLeaveDate()) >= 0){
			ret.put("type", "error");
			ret.put("msg", "离店时间必须大于入住时间!");
			return ret;
		}
		// 游客可预订：未登录时自动注册并登录（用户名=手机号，密码=身份证后6位）
		Account account = (Account)request.getSession().getAttribute("account");
		if(account == null){
			Account existAccount = accountService.findByName(bookOrder.getMobile());
			if(existAccount != null){
				if(existAccount.getStatus() == -1){
					ret.put("type", "error");
					ret.put("msg", "该用户已被禁用，请联系管理员!");
					return ret;
				}
				if(!StringUtils.isEmpty(existAccount.getIdCard()) && !existAccount.getIdCard().equals(bookOrder.getIdCard())){
					ret.put("type", "error");
					ret.put("msg", "该手机号已注册，请使用账号登录后下单!");
					return ret;
				}
				account = existAccount;
			}else{
				Account newAccount = new Account();
				newAccount.setName(bookOrder.getMobile());
				newAccount.setPassword(buildQuickPassword(bookOrder.getIdCard()));
				newAccount.setRealName(bookOrder.getName());
				newAccount.setIdCard(bookOrder.getIdCard());
				newAccount.setMobile(bookOrder.getMobile());
				newAccount.setAddress("");
				newAccount.setStatus(0);
				if(accountService.add(newAccount) <= 0){
					ret.put("type", "error");
					ret.put("msg", "快速注册失败，请稍后重试!");
					return ret;
				}
				// 兼容：部分环境未回填自增ID时二次查询
				if(newAccount.getId() == null){
					newAccount = accountService.findByName(bookOrder.getMobile());
					if(newAccount == null){
						ret.put("type", "error");
						ret.put("msg", "快速注册失败，请稍后重试!");
						return ret;
					}
				}
				account = newAccount;
			}
			request.getSession().setAttribute("account", account);
		}
		if(account == null){
			ret.put("type", "error");
			ret.put("msg", "客户不能为空!");
			return ret;
		}
		bookOrder.setAccountId(account.getId());
		RoomType roomType = roomTypeService.find(bookOrder.getRoomTypeId());
		if(roomType == null){
			ret.put("type", "error");
			ret.put("msg", "房型不能为空!");
			return ret;
		}
		if(roomType.getStatus() == 0 || roomType.getAvilableNum() == null || roomType.getAvilableNum() <= 0){
			ret.put("type", "error");
			ret.put("msg", "该房型已满房，请选择其他房型!");
			return ret;
		}
		bookOrder.setCreateTime(new Date());
		bookOrder.setStatus(0);
		if(bookOrderService.add(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "添加失败，请联系管理员!");
			return ret;
		}
		//预定成功后去修改该房型的预定数
		roomType.setBookNum(roomType.getBookNum() + 1);
		roomType.setAvilableNum(roomType.getAvilableNum() - 1);
		roomTypeService.updateNum(roomType);
		//如果可用的房间数为0，则设置该房型状态已满
		if(roomType.getAvilableNum() == 0){
			roomType.setStatus(0);
			roomTypeService.edit(roomType);
		}
		ret.put("type", "success");
		ret.put("msg", "预定成功!");
		return ret;
	}

	private String buildQuickPassword(String idCard){
		if(StringUtils.isEmpty(idCard)) return "123456";
		String trimmed = idCard.trim();
		if(trimmed.length() <= 6) return trimmed;
		return trimmed.substring(trimmed.length() - 6);
	}
	
		/**
		 * 取消预定订单（仅允许取消“待确认/已确认”的订单）
		 * @param id
		 * @return
		 */
	@RequestMapping(value="/cancel_book_order",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,String> cancelBookOrder(Long id,HttpServletRequest request){
		Map<String,String> ret = new HashMap<String, String>();
		if(id == null){
			ret.put("type", "error");
			ret.put("msg", "请选择要取消的订单!");
			return ret;
		}
		Account account = (Account)request.getSession().getAttribute("account");
		if(account == null){
			ret.put("type", "error");
			ret.put("msg", "登录会话超时或还未登录，请重新登录!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "订单不存在!");
			return ret;
		}
		if(bookOrder.getAccountId() == null || bookOrder.getAccountId().longValue() != account.getId().longValue()){
			ret.put("type", "error");
			ret.put("msg", "无权操作该订单!");
			return ret;
		}
			if(bookOrder.getStatus() != 0 && bookOrder.getStatus() != 1){
				ret.put("type", "error");
				ret.put("msg", "当前订单状态不允许取消!");
				return ret;
			}
			bookOrder.setStatus(4);//4：已取消
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
		ret.put("type", "success");
		ret.put("msg", "取消成功!");
		return ret;
	}
	
	/**
	 * 修改预定订单页面
	 * @param model
	 * @param id
	 * @return
	 */
		@RequestMapping(value="/edit_book_order",method=RequestMethod.GET)
		public ModelAndView editBookOrder(ModelAndView model,Long id,HttpServletRequest request){
		if(id == null){
			model.setViewName("redirect:index");
			return model;
		}
		Account account = (Account)request.getSession().getAttribute("account");
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null || account == null || bookOrder.getAccountId() == null || bookOrder.getAccountId().longValue() != account.getId().longValue()){
			model.setViewName("redirect:index");
			return model;
		}
			if(bookOrder.getStatus() != 0 && bookOrder.getStatus() != 1){
				//非“待确认/已确认”订单不允许修改
				model.setViewName("redirect:index");
				return model;
			}
		model.addObject("bookOrder", bookOrder);
		model.addObject("roomTypeList", roomTypeService.findAll());
		model.setViewName("home/account/edit_book_order");
		return model;
	}
	
	/**
		 * 修改预定订单提交（仅允许修改“待确认/已确认”的订单）
		 * @param bookOrder
		 * @return
		 */
	@RequestMapping(value="/edit_book_order",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,String> editBookOrderAct(BookOrder bookOrder,HttpServletRequest request){
		Map<String,String> ret = new HashMap<String, String>();
		if(bookOrder == null || bookOrder.getId() == null){
			ret.put("type", "error");
			ret.put("msg", "请选择正确的订单!");
			return ret;
		}
		Account account = (Account)request.getSession().getAttribute("account");
		if(account == null){
			ret.put("type", "error");
			ret.put("msg", "登录会话超时或还未登录，请重新登录!");
			return ret;
		}
		BookOrder existBookOrder = bookOrderService.find(bookOrder.getId());
		if(existBookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "订单不存在!");
			return ret;
		}
		if(existBookOrder.getAccountId() == null || existBookOrder.getAccountId().longValue() != account.getId().longValue()){
			ret.put("type", "error");
			ret.put("msg", "无权操作该订单!");
			return ret;
		}
			if(existBookOrder.getStatus() != 0 && existBookOrder.getStatus() != 1){
				ret.put("type", "error");
				ret.put("msg", "当前订单状态不允许修改!");
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
		if(bookOrder.getArriveDate().compareTo(bookOrder.getLeaveDate()) >= 0){
			ret.put("type", "error");
			ret.put("msg", "离店时间必须大于入住时间!");
			return ret;
		}
		
			//只允许修改本人的订单，强制回填 accountId/status
			bookOrder.setAccountId(account.getId());
			// 已确认订单被修改后，为避免信息变更遗漏，统一回到“待确认”状态
			bookOrder.setStatus(existBookOrder.getStatus() == 1 ? 0 : existBookOrder.getStatus());
		
		//房型变更时，先校验新房型是否可预订
		boolean roomTypeChanged = existBookOrder.getRoomTypeId() != null
				&& existBookOrder.getRoomTypeId().longValue() != bookOrder.getRoomTypeId().longValue();
		RoomType newRoomType = null;
		if(roomTypeChanged){
			newRoomType = roomTypeService.find(bookOrder.getRoomTypeId());
			if(newRoomType == null){
				ret.put("type", "error");
				ret.put("msg", "房型不能为空!");
				return ret;
			}
			if(newRoomType.getStatus() == 0 || newRoomType.getAvilableNum() == null || newRoomType.getAvilableNum() <= 0){
				ret.put("type", "error");
				ret.put("msg", "该房型已满房，请选择其他房型!");
				return ret;
			}
		}
		
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "修改失败，请联系管理员!");
			return ret;
		}
		
		//判断房型是否发生变化：同步恢复旧房型数量，扣减新房型数量
		if(roomTypeChanged){
			RoomType oldRoomType = roomTypeService.find(existBookOrder.getRoomTypeId());
			if(oldRoomType != null){
				oldRoomType.setAvilableNum(oldRoomType.getAvilableNum() + 1);
				oldRoomType.setBookNum(oldRoomType.getBookNum() - 1);
				roomTypeService.updateNum(oldRoomType);
				if(oldRoomType.getStatus() == 0 && oldRoomType.getAvilableNum() > 0){
					oldRoomType.setStatus(1);
					roomTypeService.edit(oldRoomType);
				}
			}
			//使用上方已校验过的新房型对象
			newRoomType.setAvilableNum(newRoomType.getAvilableNum() - 1);
			newRoomType.setBookNum(newRoomType.getBookNum() + 1);
			roomTypeService.updateNum(newRoomType);
			if(newRoomType.getAvilableNum() <= 0){
				newRoomType.setStatus(0);
				roomTypeService.edit(newRoomType);
			}
		}
		ret.put("type", "success");
		ret.put("msg", "修改成功!");
		return ret;
	}
	
	/**
	 * 修改个人信息提交
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/update_info",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,String> updateInfoAct(Account account,HttpServletRequest request){
		Map<String,String> retMap = new HashMap<String, String>();
		if(account == null){
			retMap.put("type", "error");
			retMap.put("msg", "请填写正确的用户信息！");
			return retMap;
		}
		if(StringUtils.isEmpty(account.getName())){
			retMap.put("type", "error");
			retMap.put("msg", "用户名不能为空！");
			return retMap;
		}
		Account loginedAccount = (Account)request.getSession().getAttribute("account");
		if(isExist(account.getName(),loginedAccount.getId())){
			retMap.put("type", "error");
			retMap.put("msg", "该用户名已经存在！");
			return retMap;
		}
		loginedAccount.setAddress(account.getAddress());
		loginedAccount.setIdCard(account.getIdCard());
		loginedAccount.setMobile(account.getMobile());
		loginedAccount.setName(account.getName());
		loginedAccount.setRealName(account.getRealName());
		if(accountService.edit(loginedAccount) <= 0){
			retMap.put("type", "error");
			retMap.put("msg", "修改失败，请联系管理员！");
			return retMap;
		}
		request.getSession().setAttribute("account", loginedAccount);
		retMap.put("type", "success");
		retMap.put("msg", "修改成功！");
		return retMap;
	}
	
	/**
	 * 修改密码提交
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/update_pwd",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,String> updatePwdAct(String oldPassword,String newPassword,HttpServletRequest request){
		Map<String,String> retMap = new HashMap<String, String>();
		if(StringUtils.isEmpty(oldPassword)){
			retMap.put("type", "error");
			retMap.put("msg", "请填写原来的密码！");
			return retMap;
		}
		if(StringUtils.isEmpty(newPassword)){
			retMap.put("type", "error");
			retMap.put("msg", "请填写新密码！");
			return retMap;
		}
		Account loginedAccount = (Account)request.getSession().getAttribute("account");
		if(!oldPassword.equals(loginedAccount.getPassword())){
			retMap.put("type", "error");
			retMap.put("msg", "原密码错误！");
			return retMap;
		}
		loginedAccount.setPassword(newPassword);
		if(accountService.edit(loginedAccount) <= 0){
			retMap.put("type", "error");
			retMap.put("msg", "修改失败，请联系管理员！");
			return retMap;
		}
		retMap.put("type", "success");
		retMap.put("msg", "修改密码成功！");
		return retMap;
	}
	
	/**
	 * 判断用户是否存在
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean isExist(String name,Long id){
		Account account = accountService.findByName(name);
		if(account == null)return false;
		if(account != null && account.getId().longValue() == id)return false;
		return true;
	}
}
