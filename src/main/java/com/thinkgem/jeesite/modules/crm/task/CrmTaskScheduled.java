package com.thinkgem.jeesite.modules.crm.task;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.modules.crm.TaskDataUtils;
import com.thinkgem.jeesite.modules.crm.entity.CrmInfo;
import com.thinkgem.jeesite.modules.crm.service.CrmInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2016/11/21.
 */
@Service
@Lazy(false)
public class CrmTaskScheduled {

    @Autowired
    private CrmInfoService crmInfoService;
    @Autowired
    private SystemService systemService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 扫描队列
     */
    @Scheduled(cron = "0/6 0 * * * ?")
    public void validateAndSave() {
        Map<String, List<CrmInfo>> map = TaskDataUtils.getCache();
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String userId = (String) entry.getKey();
            List<CrmInfo> crmInfoList = (List<CrmInfo>) entry.getValue();

            User user = UserUtils.get(userId);
            if (user != null  && crmInfoList != null && crmInfoList.size() > 0) {
                if (user.getSurplusTotal() < 1) {
                    logger.info("----------------------- 额度不足，清除队列。  loginName:" + user.getLoginName() + "    Name:" + user.getName());
                    TaskDataUtils.removeCacheByUserId(user.getId());
                }else{
                    new ValidateApp(crmInfoList.get(0), user).start();
                    TaskDataUtils.setCaChe(user, (List<CrmInfo>) crmInfoList.remove(0));
                }
            }
        }
    }

    public class ValidateApp extends Thread {
        private CrmInfo crmInfo;
        private User user;

        public ValidateApp(CrmInfo crmInfo, User user) {
            this.crmInfo = crmInfo;
            this.user = user;
        }

        public void run() {
            crmInfoService.validateAndSave(crmInfo, user, true);
            user.setSurplusTotal(user.getSurplusTotal() - 1);
            //20条更新一次数据库（额度）
            if (user.getSurplusTotal() % 20 == 0) {
                systemService.updateSurplusTotalById(user);
            }
        }
    }
}
