/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.crm.entity.CrmInfo;

import java.util.List;

/**
 * 客户信息DAO接口
 * @author fangxb
 * @version 2016-10-23
 */
@MyBatisDao
public interface CrmInfoDao extends CrudDao<CrmInfo> {

    /**
     * 根据用户ID获取需要进行筛选的数据
     * @param crmInfo
     * @return
     */
    List<CrmInfo> findListByScreening(CrmInfo crmInfo);
}