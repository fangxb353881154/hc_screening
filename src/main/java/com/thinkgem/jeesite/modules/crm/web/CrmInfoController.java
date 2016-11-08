/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.crm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.crm.entity.CrmInfo;
import com.thinkgem.jeesite.modules.crm.service.CrmInfoService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户信息Controller
 *
 * @author fangxb
 * @version 2016-10-23
 */
@Controller
@RequestMapping(value = "${adminPath}/crm/crmInfo")
public class CrmInfoController extends BaseController {

    @Autowired
    private CrmInfoService crmInfoService;
    @Autowired
    private SystemService systemService;


    @ModelAttribute
    public CrmInfo get(@RequestParam(required = false) String id) {
        CrmInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = crmInfoService.get(id);
        }
        if (entity == null) {
            entity = new CrmInfo();
        }
        return entity;
    }

    @RequiresPermissions("crm:crmInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(CrmInfo crmInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        if (!user.isAdmin()) {
            crmInfo.setCreateBy(user);
        }
        Page<CrmInfo> page = crmInfoService.findPage(new Page<CrmInfo>(request, response), crmInfo);
        model.addAttribute("page", page);
        return "modules/crm/crmInfoList";
    }

    @RequiresPermissions("crm:crmInfo:view")
    @RequestMapping(value = "form")
    public String form(CrmInfo crmInfo, Model model) {
        model.addAttribute("crmInfo", crmInfo);
        return "modules/crm/crmInfoForm";
    }

    @RequiresPermissions("crm:crmInfo:edit")
    @RequestMapping(value = "save")
    public String save(CrmInfo crmInfo, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, crmInfo)) {
            return form(crmInfo, model);
        }
        crmInfoService.save(crmInfo);
        addMessage(redirectAttributes, "保存客户信息成功");
        return "redirect:" + Global.getAdminPath() + "/crm/crmInfo/?repage";
    }

    @RequiresPermissions("crm:crmInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(CrmInfo crmInfo, RedirectAttributes redirectAttributes) {
        crmInfoService.delete(crmInfo);
        addMessage(redirectAttributes, "删除客户信息成功");
        return "redirect:" + Global.getAdminPath() + "/crm/crmInfo/?repage";
    }

    @RequiresPermissions("crm:crmInfo:edit")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public String exportFile(CrmInfo crmInfo, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            User user = UserUtils.getUser();
            if (!user.isAdmin()) {
                crmInfo.setCreateBy(user);
            }
            String fileName = "客户信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<CrmInfo> list = crmInfoService.findInfo(crmInfo);
            new ExportExcel("客户信息", CrmInfo.class).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * @return
     */
    @RequiresPermissions("crm:crmInfo:edit")
    @RequestMapping("validateAndSave")
    public String validateAndSave(RedirectAttributes redirectAttributes) {
        if (crmInfoService.isAppToken()) {
            CrmInfo info = new CrmInfo();
            User user = UserUtils.getUser();
            if (user.getSurplusTotal() <= 0) {
                addMessage(redirectAttributes, "开启扫描失败，您的账号额度为（" + user.getSurplusTotal() + "），请充值！");
                return "redirect:" + Global.getAdminPath() + "/crm/crmInfo/list?repage";
            }
            int limits = 10000;
            //默认一次性扫描1W条，额度不够以最大额度为主
            limits = user.getSurplusTotal() > 10000 ? limits : user.getSurplusTotal();
            info.setCreateBy(user);
            info.setLimit(limits);
            List<CrmInfo> infoList = crmInfoService.findListByScreening(info);
            try {
                if (infoList != null && infoList.size() > 0) {
                    int eachNumber = Integer.valueOf(Global.getConfig("thread.each.number"));
                    int startInt = 0;//起点
                    int listCount = infoList.size();
                    int queue = 1;
                    while (startInt < listCount) {
                        int endInt = (startInt + eachNumber) > listCount ? listCount : (startInt + eachNumber);
                        List<CrmInfo> infoQueueList = infoList.subList(startInt, endInt);

                        new ValidateApp(infoQueueList, queue).start();
                        queue++;
                        startInt += eachNumber;
                    }
                    //crmInfoService.validateAndSave(umId, token, infoList);
                    user.setSurplusTotal(user.getSurplusTotal() - listCount);
                    systemService.updateSurplusTotalById(user);
                    addMessage(redirectAttributes,"正在扫描.....<br/>本次扫描数据"+listCount+"条,预计需要10-20分钟. 期间<strong>请勿重复提交筛选</strong>、<strong>请勿退出APP</strong>." +
                            "<br/>违规操作损失的额度概不负责.");
                    return "redirect:" + Global.getAdminPath() + "/crm/crmInfo/list?repage";
                } else {
                    throw new RuntimeException("未获取到需要扫描的客户信息！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                addMessage(redirectAttributes, "扫描失败，失败原因：" + e.getMessage());
            }
            return "redirect:" + Global.getAdminPath() + "/crm/crmInfo/list?repage";
        } else {
            addMessage(redirectAttributes, "令牌过期，重新登录");
            return "redirect:" + Global.getAdminPath() + "/logout";
        }


    }

    /**
     * 下载导入客户数据模板
     *
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("crm:crmInfo:edit")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "客户数据导入模板.xlsx";
            List<CrmInfo> list = Lists.newArrayList();
            CrmInfo crmInfo = new CrmInfo();
            crmInfo.setCustname("张XX");
            crmInfo.setMobile("13800001234");
            crmInfo.setIdCard("350022199901012200");
            list.add(crmInfo);
            new ExportExcel("客户信息", CrmInfo.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/crm/crmInfo/list?repage";
    }

    @RequiresPermissions("crm:crmInfo:edit")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<CrmInfo> list = ei.getDataList(CrmInfo.class);
            Pattern p = Pattern.compile("^1[34578]\\d{9}$");
            for (CrmInfo info : list) {
                String name = info.getCustname();
                name = name.length() > 3 ? name.substring(0, 3) : name;
                info.setCustname(name.trim());

                String idCard = info.getIdCard().toUpperCase();
                idCard = idCard.length() > 18 ? idCard.substring(0, 18) : idCard;
                info.setIdCard(idCard.trim());

                String mobile = info.getMobile();
                mobile = mobile.indexOf("0") == 0 ? mobile.substring(1) : mobile;
                mobile = mobile.length() > 11 ? mobile.substring(0, 11) : mobile;
                info.setMobile(mobile.trim());

                if (p.matcher(info.getMobile()).matches()) {
                    Pattern sFp = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
                    if (sFp.matcher(info.getIdCard()).matches()) {
                        info.setCustname(info.getCustname().replaceAll(" ", ""));
                        crmInfoService.save(info);
                        successNum++;
                    }else{
                        failureMsg.append("<br/>客户 " + info.getCustname() + "(" + info.getIdCard() + ")" + " 身份证不正确; ");
                        failureNum++;
                    }
                } else {
                    failureMsg.append("<br/>客户 " + info.getCustname() + "(" + info.getMobile() + ")" + " 手机号码不正确; ");
                    failureNum++;
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条客户信息，导入信息如下：");
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条客户信息" + failureMsg);
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入客户信息失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/crm/crmInfo/list?repage";
    }




    public class ValidateApp extends Thread{
        private List<CrmInfo> infoList = Lists.newArrayList();
        private Integer queue;

        public ValidateApp(List<CrmInfo> infoList, Integer queue){
            this.infoList = infoList;
            this.queue = queue;
        }

        public void run(){
            int i = 1;
            for (CrmInfo info : infoList) {
                logger.debug("第（" + queue + "）条列队，第-" + i + "-条数据");
                crmInfoService.validateAndSave(info, true);
                i++;
            }
        }
    }
}