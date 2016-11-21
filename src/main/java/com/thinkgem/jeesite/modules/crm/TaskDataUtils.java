package com.thinkgem.jeesite.modules.crm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.modules.crm.entity.CrmInfo;
import com.thinkgem.jeesite.modules.sys.entity.User;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2016/11/21.
 */
public class TaskDataUtils {

    public static String CRM_INFO_CACHE_LIST = "CRM_INFO_CACHE_LIST";

    public static void setCaChe(User user, List<CrmInfo> infoList) {
        if (infoList != null && infoList.size() > 0) {
            Map<String, List<CrmInfo>> map = getCache();
            if (map == null) {
                map = Maps.newHashMap();
            }
            List<CrmInfo> crmInfos = map.get(user.getId());
            if (crmInfos != null) {
                for (CrmInfo info : crmInfos) {
                    if (!infoList.contains(info)) {
                        infoList.add(info);
                    }
                }
            }
            map.put(user.getId(), infoList);
            CacheUtils.put(CRM_INFO_CACHE_LIST, map);
        }
    }

    public static void removeCacheByUserId(String userId) {
        Map<String, List<CrmInfo>> map = getCache();
        map.remove(userId);
        CacheUtils.put(CRM_INFO_CACHE_LIST, map);
    }

    public static Map<String,List<CrmInfo>> getCache(){
        Map<String, List<CrmInfo>> map = (Map<String, List<CrmInfo>>) CacheUtils.get(CRM_INFO_CACHE_LIST);
        if (map == null) {
            map = Maps.newHashMap();
        }
        return map;
    }
}
