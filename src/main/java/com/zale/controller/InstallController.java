package com.zale.controller;


import com.blade.Environment;
import com.blade.ioc.annotation.Inject;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.ui.RestResponse;
import com.zale.controller.admin.AttachController;
import com.zale.exception.TipException;
import com.zale.init.TaleConst;
import com.zale.model.entity.Users;
import com.zale.service.OptionsService;
import com.zale.service.SiteService;
import com.zale.utils.ZaleUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Path("install")
public class InstallController extends BaseController {

    @Inject
    private SiteService siteService;

    @Inject
    private OptionsService optionsService;

    /**
     * 安装页
     *
     * @return
     */
    @Route(value = "/", method = HttpMethod.GET)
    public String index(Request request) {
        boolean existInstall = Files.exists(Paths.get(AttachController.CLASSPATH + "install.lock"));
        int allow_reinstall = TaleConst.OPTIONS.getInt("allow_install", 0);

        if (allow_reinstall == 1) {
            request.attribute("is_install", false);
        } else {
            request.attribute("is_install", existInstall);
        }
        return "install";
    }

    @Route(value = "/", method = HttpMethod.POST)
    @JSON
    public RestResponse doInstall(@Param String site_title, @Param String site_url,
                                  @Param String admin_user, @Param String admin_email,
                                  @Param String admin_pwd) {
        if (Files.exists(Paths.get(AttachController.CLASSPATH + "install.lock"))
                && TaleConst.OPTIONS.getInt("allow_install", 0) != 1) {
            return RestResponse.fail("请勿重复安装");
        }
        //TODO　fix 由于数据库设置了用户名唯一约束,如果是重新安装再次使用相同的用户名，因为之前的数据还存在报错org.sqlite.SQLiteException: [SQLITE_CONSTRAINT_UNIQUE]  A UNIQUE constraint failed (UNIQUE constraint failed: t_users.username)
        try {
            if (StringKit.isBlank(site_title) ||
                    StringKit.isBlank(site_url) ||
                    StringKit.isBlank(admin_user) ||
                    StringKit.isBlank(admin_pwd)) {
                return RestResponse.fail("请确认网站信息输入完整");
            }

            if (admin_pwd.length() < 6 || admin_pwd.length() > 14) {
                return RestResponse.fail("请输入6-14位密码");
            }

            if (StringKit.isNotBlank(admin_email) && !ZaleUtils.isEmail(admin_email)) {
                return RestResponse.fail("邮箱格式不正确");
            }

            Users temp = new Users();
            temp.setUsername(admin_user);
            temp.setPassword(admin_pwd);
            temp.setEmail(admin_email);

            siteService.initSite(temp);

            if (site_url.endsWith("/")) {
                site_url = site_url.substring(0, site_url.length() - 1);
            }
            if (!site_url.startsWith("http")) {
                site_url = "http://".concat(site_url);
            }
            optionsService.saveOption("site_title", site_title);
            optionsService.saveOption("site_url", site_url);

            TaleConst.OPTIONS = Environment.of(optionsService.getOptions());
        } catch (Exception e) {
            String msg = "安装失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                log.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

}
