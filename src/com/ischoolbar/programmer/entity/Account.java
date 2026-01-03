package com.ischoolbar.programmer.entity;

import org.springframework.stereotype.Component;

/**
 * �ͻ�ʵ����
 * @author ymj
 *
 */
@Component
public class Account {
	private Long id;//�ͻ�id
	private String name;//�ͻ���¼��
	private String password;//�ͻ���¼����
	private String realName;//��ʵ����
	private String idCard;//����֤����
	private String mobile;//�ֻ���
	private String address;//��ϵ��ַ
	private int status;//״̬��0�����ã�-1������
	private int level;//会员等级：1=普通会员(九折)，2=高级会员(八折)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}



}
