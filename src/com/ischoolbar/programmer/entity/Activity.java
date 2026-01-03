package com.ischoolbar.programmer.entity;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * 活动实体类
 * @author ymj
 *
 */
@Component
public class Activity {
	private Long id;//活动ID
	private String title;//活动标题
	private String content;//活动内容
	private Date startTime;//活动开始时间
	private Date endTime;//活动结束时间
	private int status;//状态：0=下架，1=上架
	private Date createTime;//创建时间
	private String photo;//活动图片

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
