/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.entity;

import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

import javax.validation.constraints.NotNull;

/**
 * 客户信息Entity
 * @author fangxb
 * @version 2016-10-23
 */
public class CrmInfo extends DataEntity<CrmInfo> {
	
	private static final long serialVersionUID = 1L;
	private String custname;		// 客户名称
	private String mobile;		// 手机号
	private String idCard;		// 身份证号
	private String results;		// 查询结果
	private String remark;		// 备注
	
	public CrmInfo() {
		super();
	}

	public CrmInfo(String id){
		super(id);
	}

	@Length(min=1, max=20, message="客户名称长度必须介于 1 和 20 之间")
	@NotNull(message="客户名称不能为空")
	@ExcelField(title="客户名称", align=1, sort=1)
	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}
	
	@Length(min=1, max=11, message="手机号长度必须介于 1 和 11 之间")
	@NotNull(message="手机号不能为空")
	@ExcelField(title="手机号", align=1, sort=10)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Length(min=1, max=100, message="身份证号长度必须介于 1 和 100 之间")
	@NotNull(message="身份证号不能为空")
	@ExcelField(title="身份证号", align=1, sort=20)
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	@Length(min=0, max=2000, message="查询结果长度必须介于 0 和 2000 之间")
	@ExcelField(title="查询结果", align=1, sort=30,type = 1)
	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	@ExcelField(title="备注", align=1, sort=25)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}