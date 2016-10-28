/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.HttpRequest;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.crm.common.Contents;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.crm.entity.CrmInfo;
import com.thinkgem.jeesite.modules.crm.dao.CrmInfoDao;

/**
 * 客户信息Service
 *
 * @author fangxb
 * @version 2016-10-23
 */
@Service
@Transactional(readOnly = true)
public class CrmInfoService extends CrudService<CrmInfoDao, CrmInfo> {

    @Autowired
    private CrmInfoDao crmInfoDao;


    public CrmInfo get(String id) {
        return super.get(id);
    }

    public List<CrmInfo> findList(CrmInfo crmInfo) {
        return super.findList(crmInfo);
    }

    public Page<CrmInfo> findPage(Page<CrmInfo> page, CrmInfo crmInfo) {
        return super.findPage(page, crmInfo);
    }

    @Transactional(readOnly = false)
    public void save(CrmInfo crmInfo) {
        super.save(crmInfo);
    }

    @Transactional(readOnly = false)
    public void delete(CrmInfo crmInfo) {
        super.delete(crmInfo);
    }

    /**
     * 根据用户ID获取需要进行筛选的数据
     *
     * @param crmInfo
     * @return
     */
    public List<CrmInfo> findListByScreening(CrmInfo crmInfo) {
        return crmInfoDao.findListByScreening(crmInfo);
    }


    @Transactional(readOnly = false)
    public void validateAndSave(String umId, String token, List<CrmInfo> infoList) {
        String brCode = getBrCode(umId, token);
        if (StringUtils.isNotEmpty(brCode)) {
            Map<String, Object> requestDataMap = Maps.newHashMap();
            requestDataMap.put("umUserName", umId);
            requestDataMap.put("appFlag", Contents.appFlag);
            requestDataMap.put("currentStatus", Contents.currentStatus);
            requestDataMap.put("currentFollow", Contents.currentFollow);
            requestDataMap.put("refCode", umId);
            requestDataMap.put("brCode", brCode);
            requestDataMap.put("salesChannel", Contents.salesChannel);
            requestDataMap.put("subChannel", Contents.subChannel);
            requestDataMap.put("productType", Contents.productType);
            requestDataMap.put("protectFlag", Contents.protectFlag);
            String url = Global.getConfig("peace.assures.url");
            url += "?" + getHttpGetParam(umId, token) + "&requestType=" + Contents.REQUEST_TYPE_VALIDATE_AND_SAVE;
            for (CrmInfo info : infoList) {
                requestDataMap.put("id", info.getIdCard());
                requestDataMap.put("custName", info.getCustname());
                requestDataMap.put("mobile", info.getMobile());
                requestDataMap.put("remark", "");
                //筛选
                String s = null;
                try {
                    s = HttpRequest.sendPost(url, "requestDataMap=" + URLEncoder.encode(JSONUtils.toJSONString(requestDataMap), "UTF-8"));
                    System.out.println("筛选结果：" + s);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("数据提交失败！");
                }
                Map resultMap = (Map) JSONUtils.parse(s);
                String applNo = (String) resultMap.get("applNo");
                String resultMsg = (String) resultMap.get("resultMsg");
                resultMsg = StringUtils.isNotEmpty(resultMsg) ? resultMsg : "无额度客户";
                info.setResults(resultMsg);
                super.save(info);
                if (resultMap != null && StringUtils.isNotEmpty(applNo) && (StringUtils.indexOf(resultMsg, "重复提交") == -1)) {
                    //取消订单
                    cancelInfo(umId, token, applNo);
                }
            }
        } else {
            throw new RuntimeException("用户登录信息错误！");
        }
    }

    /**
     * 数据扫描
     * @param info      客户信息
     * @param isCancel  是否自动取消
     */
    @Transactional(readOnly = false)
    public void validateAndSave(CrmInfo info, boolean isCancel) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        User user = UserUtils.getUser();
        String umId = user.getLoginName();
        String token = user.getAppToken();
        String brCode = user.getBrCode();

        Map<String, Object> requestDataMap = Maps.newHashMap();
        requestDataMap.put("umUserName", umId);
        requestDataMap.put("appFlag", Contents.appFlag);
        requestDataMap.put("currentStatus", Contents.currentStatus);
        requestDataMap.put("currentFollow", Contents.currentFollow);
        requestDataMap.put("refCode", umId);
        requestDataMap.put("brCode", brCode);
        requestDataMap.put("salesChannel", Contents.salesChannel);
        requestDataMap.put("subChannel", Contents.subChannel);
        requestDataMap.put("productType", Contents.productType);
        requestDataMap.put("protectFlag", Contents.protectFlag);
        String url = Global.getConfig("peace.assures.url");
        url += "?" + getHttpGetParam(umId, token) + "&requestType=" + Contents.REQUEST_TYPE_VALIDATE_AND_SAVE;
        requestDataMap.put("id", info.getIdCard());
        requestDataMap.put("custName", info.getCustname());
        requestDataMap.put("mobile", info.getMobile());
        requestDataMap.put("remark", "");
        //筛选
        String s = null;
        /*try {
            s = HttpRequest.sendPost(url, "requestDataMap=" + URLEncoder.encode(JSONUtils.toJSONString(requestDataMap), "UTF-8"));
            logger.info("筛选结果：" + s);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("数据提交失败！");
        }
        Map resultMap = (Map) JSONUtils.parse(s);
        String applNo = (String) resultMap.get("applNo");
        String resultMsg = (String) resultMap.get("resultMsg");
        resultMsg = StringUtils.isNotEmpty(resultMsg) ? resultMsg : "无额度客户";
        info.setResults(resultMsg);
        super.save(info);
        if (resultMap != null
                && StringUtils.isNotEmpty(applNo)
                && (StringUtils.indexOf(resultMsg, "重复提交") == -1)
                && isCancel) {
            //取消订单
            cancelInfo(applNo);
        }*/

        info.setResults(info.getIdCard());
        super.save(info);

    }

    /**
     * 接口获取brcode
     *
     * @param umId
     * @param token
     * @return
     */
    public String getBrCode(String umId, String token) {
        Map<String, String> requestDataMap = Maps.newHashMap();
        requestDataMap.put("appFlag", Contents.appFlag);
        requestDataMap.put("umUserName", umId);
        String urlParam = "requestType=" + Contents.REQUEST_TYPE_GET_INFO + "&" + getHttpGetParam(umId, token) + "&requestDataMap=" + JSONUtils.toJSONString(requestDataMap);
        String s = HttpRequest.sendGet(Global.getConfig("peace.assures.url"), urlParam);
        logger.info("获取登录用户信息：" + s);

        Map<String, String> result = (Map<String, String>) JSONUtils.parse(s);
        String brCode = result.get("brCode");
        if (StringUtils.isNotEmpty(brCode)) {
            //保存token至缓存数据
            User user = UserUtils.getUser();
            user.setAppToken(token);
            user.setBrCode(brCode);
            UserUtils.setUser(user);
        }
        return brCode;
    }


    /**
     * 判断 APP token
     *
     * @return
     */
    public boolean isAppToken() {
        User user = UserUtils.getUser();
        if (StringUtils.isNotEmpty(user.getAppToken())) {
            return isAppToken(user.getLoginName(), user.getAppToken());
        }
        return false;
    }

    /**
     * 验证APP token
     *
     * @return
     */
    public boolean isAppToken(String umId, String token) {
        return true;
        //return StringUtils.isNotEmpty(getBrCode(umId, token));
    }


    /**
     * 验证APP token
     *
     * @return
     */
    public void cancelInfo(String appNo) {
        if (StringUtils.isNotEmpty(appNo)) {
            User user = UserUtils.getUser();
            //判断 token
            if (StringUtils.isNotEmpty(user.getAppToken()) && isAppToken()) {
                cancelInfo(user.getLoginName(), user.getAppToken(), appNo);
            }
        }
    }

    /**
     * 取消订单
     *
     * @param umId
     * @param token
     * @param applNo
     */
    public void cancelInfo(String umId, String token, String applNo) {
        Map<String, Object> requestDataMap = Maps.newHashMap();
        requestDataMap.put("appFlag", Contents.appFlag);
        requestDataMap.put("umUserName", umId);
        requestDataMap.put("applNo", applNo);
        requestDataMap.put("currentStatus", Contents.currentStatus);
        requestDataMap.put("currentFollow", Contents.currentFollow);

        String urlParam = "requestType=" + Contents.REQUEST_TYPE_CANCEL + "&" + getHttpGetParam(umId, token) + "&requestDataMap=" + JSONUtils.toJSONString(requestDataMap);
        System.out.println(urlParam);
        String s = HttpRequest.sendGet(Global.getConfig("peace.assures.url"), urlParam);
        logger.info("取消订单：" + s);
    }

    public String getHttpGetParam(String umId, String token) {
        String result = "umId=" + umId + "&token=" + token + "&isOtherChannel=" + Contents.isOtherChannel;
        return result;
    }

    /**
     * 无分页查询人员列表
     *
     * @param info
     * @return
     */
    public List<CrmInfo> findInfo(CrmInfo info) {
        // 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
        info.getSqlMap().put("dsf", dataScopeFilter(info.getCurrentUser(), "o", "a"));
        List<CrmInfo> list = crmInfoDao.findList(info);
        return list;
    }
}