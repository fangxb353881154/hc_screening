package com.thinkgem.jeesite.modules.crm.web;

import com.google.common.collect.Maps;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import com.thinkgem.jeesite.common.utils.AppDesUtils;
import com.thinkgem.jeesite.common.utils.ResultUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.crm.service.CrmInfoService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by asus on 2016/11/7.
 */
@Controller
@RequestMapping(value = "${adminPath}/api/")
public class InterfaceApiController {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SystemService systemService;
    @Autowired
    private CrmInfoService crmInfoService;


    @ResponseBody
    @RequestMapping("/appLogin")
    public Map<String,Object> appLogin(@RequestParam(required = true) String msg){
        Map<String, Object> result = Maps.newHashMap();
        if (StringUtils.isNotEmpty(msg)) {
            msg = msg.replaceAll(" ", "+");
            String[] splits = msg.split("=5=");
            String loginName = splits[0];
            String appToken = splits[1];
            if (StringUtils.isNotEmpty(loginName) && StringUtils.isNotEmpty(appToken)) {
                AppDesUtils appDesUtils = new AppDesUtils();
                try{
                    logger.info("loginName: " + loginName + "        appToken: " + appToken);
                    loginName = appDesUtils.decryptString(loginName);
                    appToken = appDesUtils.decryptString(appToken);
                }catch (Exception e){
                    return ResultUtils.getFailure("亲，别玩我，谢谢！");
                }
                User user = systemService.getUserByLoginName(loginName);
                if (user != null) {
                    if (crmInfoService.isAppToken(user.getLoginName(), appToken)) {
                        user.setAppToken(appToken);
                        systemService.updateAppTokenById(user);
                        return ResultUtils.getSuccess("您的账号剩余额度："+user.getSurplusTotal()+"， 进入后台开启扫(zhuang)描(bi)模式，期间请勿重新(退出)应用！");
                    }else{
                        return ResultUtils.getFailure("您的UM号可能登录太久，劳驾重新登录！");
                    }
                }else{
                    return ResultUtils.getFailure("亲，您的UM号未开通该功能！");
                }
            }else{
                return ResultUtils.getFailure("亲，别玩我，谢谢！");
            }
        }else{
            return ResultUtils.getFailure();
        }
    }

    public static void main(String[] args) {
        String s = "wUekyQYCnoYczplYbpDRsJebkG7kL8OeUpN4ljZQt K3ux6RFk Y8g==";
        s = s.replaceAll(" ", "+");
        AppDesUtils appDesUtils = new AppDesUtils();
        String appToken = appDesUtils.decryptString(s);
        System.out.println(appToken);
    }
}
