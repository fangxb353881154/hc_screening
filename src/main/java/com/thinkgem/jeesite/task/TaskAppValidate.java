package com.thinkgem.jeesite.task;

import com.thinkgem.jeesite.modules.crm.service.CrmInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by asus on 2016/10/24.
 */
@Service
@Lazy(false)
public class TaskAppValidate {

    @Autowired
    private CrmInfoService crmInfoService;

   // @Scheduled(cron = "*/30 * * * * ?")
    public void validateAndSave(){
        String umId = "LIPEIZHU352";
        String token = "E1FA98EF1973418684C0681D9B1A2E49";
       // crmInfoService.validateAndSave(umId, token, null);
        System.out.println("+++++++++++++++  定时器        "+new Date()+"  ++++++++++++++++++++++");
    }
}
