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

import com.ischoolbar.programmer.entity.Account;
import com.ischoolbar.programmer.entity.BookOrder;
import com.ischoolbar.programmer.entity.RoomType;
import com.ischoolbar.programmer.entity.admin.User;
import com.ischoolbar.programmer.page.admin.Page;
import com.ischoolbar.programmer.service.AccountService;
import com.ischoolbar.programmer.service.BookOrderService;
import com.ischoolbar.programmer.service.RoomTypeService;
import com.ischoolbar.programmer.service.admin.LogService;

/**
 * 
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
	 * §Ò
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
	 * 
	 * @param bookOrder
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> add(BookOrder bookOrder){
		Map<String, String> ret = new HashMap<String, String>();
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "§Õ!");
			return ret;
		}
		if(bookOrder.getAccountId() == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrder.getRoomTypeId() == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getName())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getMobile())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getIdCard())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getArriveDate())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getLeaveDate())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}

		// 
		Account account = accountService.find(bookOrder.getAccountId());
		RoomType roomType = roomTypeService.find(bookOrder.getRoomTypeId());
		if(account != null && roomType != null){
			Double originalPrice = roomType.getPrice() != null ? roomType.getPrice() : 0.0;
			Double discount = 1.0; // 
			if(account.getLevel() == 1){
				discount = 0.9; // 
			}else if(account.getLevel() == 2){
				discount = 0.8; // 
			}
			Double actualPrice = originalPrice * discount;
			bookOrder.setOriginalPrice(originalPrice);
			bookOrder.setDiscount(discount);
			bookOrder.setActualPrice(actualPrice);
		}

		bookOrder.setCreateTime(new Date());
		if(bookOrderService.add(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		roomType = roomTypeService.find(bookOrder.getRoomTypeId());
		//¡Â
		if(roomType != null){
			roomType.setBookNum(roomType.getBookNum() + 1);
			roomType.setAvilableNum(roomType.getAvilableNum() - 1);
			roomTypeService.updateNum(roomType);
			//0¡Â
			if(roomType.getAvilableNum() == 0){
				roomType.setStatus(0);
				roomTypeService.edit(roomType);
			}
		}
		ret.put("type", "success");
		ret.put("msg", "!");
		return ret;
	}
	
	/**
	 * 
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> edit(BookOrder bookOrder){
		Map<String, String> ret = new HashMap<String, String>();
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "§Õ!");
			return ret;
		}
		if(bookOrder.getAccountId() == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrder.getRoomTypeId() == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getName())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getMobile())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getIdCard())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getArriveDate())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(StringUtils.isEmpty(bookOrder.getLeaveDate())){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		BookOrder existBookOrder = bookOrderService.find(bookOrder.getId());
		if(existBookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "§Ò!");
			return ret;
		}
		// //
		if(existBookOrder.getStatus() == 2 || existBookOrder.getStatus() == 3){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		//§Ø£
		if(existBookOrder.getRoomTypeId().longValue() != bookOrder.getRoomTypeId().longValue()){
			//£
			//
			RoomType oldRoomType = roomTypeService.find(existBookOrder.getRoomTypeId());
			oldRoomType.setAvilableNum(oldRoomType.getAvilableNum() + 1);
			oldRoomType.setBookNum(oldRoomType.getBookNum() - 1);
			roomTypeService.updateNum(oldRoomType);
			if(oldRoomType.getStatus() == 0){
				//
				if(oldRoomType.getAvilableNum() > 0){
					//¨®
					oldRoomType.setStatus(1);
					roomTypeService.edit(oldRoomType);
				}
			}
			//
			RoomType newRoomType = roomTypeService.find(bookOrder.getRoomTypeId());
			newRoomType.setAvilableNum(newRoomType.getAvilableNum() - 1);
			newRoomType.setBookNum(newRoomType.getBookNum() + 1);
			roomTypeService.updateNum(newRoomType);
			if(newRoomType.getAvilableNum() <= 0){
				//§á¡Â
				newRoomType.setStatus(0);//¨®
				roomTypeService.edit(newRoomType);
			}
		}
		ret.put("type", "success");
		ret.put("msg", "!");
		return ret;
	}

	/**
	 *  -> 
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
			ret.put("msg", "!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrder.getStatus() != 0){
			ret.put("type", "error");
			ret.put("msg", "§Õ!");
			return ret;
		}
		bookOrder.setStatus(1);
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		User admin = (User)request.getSession().getAttribute("admin");
		if(admin != null){
			logService.add("{"+admin.getUsername()+"}{"+id+"}!");
		}
		ret.put("type", "success");
		ret.put("msg", "!");
		return ret;
	}

	/**
	 * / -> 
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
			ret.put("msg", "!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrder.getStatus() != 0 && bookOrder.getStatus() != 1){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		bookOrder.setStatus(4);
		if(bookOrderService.edit(bookOrder) <= 0){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		RoomType roomType = roomTypeService.find(bookOrder.getRoomTypeId());
		if(roomType != null){
			//
			if(roomType.getBookNum() != null && roomType.getBookNum() > 0){
				roomType.setBookNum(roomType.getBookNum() - 1);
			}
			if(roomType.getAvilableNum() != null){
				roomType.setAvilableNum(roomType.getAvilableNum() + 1);
			}
			roomTypeService.updateNum(roomType);
			//
			if(roomType.getStatus() == 0 && roomType.getAvilableNum() != null && roomType.getAvilableNum() > 0){
				roomType.setStatus(1);
				roomTypeService.edit(roomType);
			}
		}
		User admin = (User)request.getSession().getAttribute("admin");
		if(admin != null){
			logService.add("{"+admin.getUsername()+"}{"+id+"}!");
		}
		ret.put("type", "success");
		ret.put("msg", "!");
		return ret;
	}

	/**
	 * 
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
			ret.put("msg", "!");
			return ret;
		}
		BookOrder bookOrder = bookOrderService.find(id);
		if(bookOrder == null){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrder.getStatus() != 4){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		if(bookOrderService.delete(id) <= 0){
			ret.put("type", "error");
			ret.put("msg", "!");
			return ret;
		}
		User admin = (User)request.getSession().getAttribute("admin");
		if(admin != null){
			logService.add("{"+admin.getUsername()+"}{"+id+"}!");
		}
		ret.put("type", "success");
		ret.put("msg", "!");
		return ret;
	}
	
	/**
	 * 
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
