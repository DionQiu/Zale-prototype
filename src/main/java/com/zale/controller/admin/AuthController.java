package com.zale.controller.admin;

import com.blade.kit.DateKit;
import com.blade.kit.EncryptKit;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.JSON;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.ui.RestResponse;
import com.zale.controller.BaseController;
import com.zale.exception.TipException;
import com.zale.init.TaleConst;
import com.zale.model.dto.LogActions;
import com.zale.model.entity.Logs;
import com.zale.model.entity.Users;
import com.zale.model.param.LoginParam;
import com.zale.utils.TaleUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录，退出
 * Created by qyw on 2017/2/21.
 */
@Slf4j
@Path("admin")
public class AuthController extends BaseController {

    @Route(value = "login", method = HttpMethod.GET)
    public String login(Response response) {
        if (null != this.user()) {
            response.redirect("/admin/index");
            return null;
        }
        return "admin/login";
    }

    @Route(value = "login", method = HttpMethod.POST)
    @JSON
    public RestResponse doLogin(LoginParam loginParam, Request request,
                                Session session, Response response) {

        Integer error_count = cache.get("login_error_count");
        try {
            error_count = null == error_count ? 0 : error_count;
            if (null != error_count && error_count >= TaleConst.LOGIN_MAX_ERROE_COUNT) {
                return RestResponse.fail("您输入错误已超过"+TaleConst.LOGIN_MAX_ERROE_COUNT+"次,"+"请"+(TaleConst.EXCEED_LOGIN_MAX_ERROE_COUNT_WAIT_TIME/60)+"分钟后尝试");
            }

/*            long count = new Users().where("username", loginParam.getUsername()).count();
            if (count < 1) {
                return RestResponse.fail("不存在该用户");
            }*/
            String pwd = EncryptKit.md5(loginParam.getUsername(), loginParam.getPassword());

            Users user = new Users().where("username", loginParam.getUsername()).and("password", pwd).find();
            if (null == user) {
                error_count += 1;
                cache.set("login_error_count", error_count, TaleConst.EXCEED_LOGIN_MAX_ERROE_COUNT_WAIT_TIME);
                return RestResponse.fail("用户名或密码错误");
            }
            session.attribute(TaleConst.LOGIN_SESSION_KEY, user);
            if (StringKit.isNotBlank(loginParam.getRemeberMe())) {
                TaleUtils.setCookie(response, user.getUid());
            }

            Users temp = new Users();
            temp.setLogged(DateKit.nowUnix());
            temp.update(user.getUid());
            log.info("登录成功：{}", loginParam.getUsername());
            cache.set("login_error_count", 0);

            new Logs(LogActions.LOGIN, loginParam.getUsername(), request.address(), user.getUid()).save();
        } catch (Exception e) {
            error_count += 1;
            cache.set("login_error_count", error_count, TaleConst.EXCEED_LOGIN_MAX_ERROE_COUNT_WAIT_TIME);
            String msg = "登录失败";
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
