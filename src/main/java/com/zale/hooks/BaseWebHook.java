package com.zale.hooks;

import com.blade.ioc.annotation.Bean;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.zale.init.TaleConst;
import com.zale.model.entity.Users;
import com.zale.utils.ZaleUtils;
import lombok.extern.slf4j.Slf4j;
/**
* @Author qyw
* @Description 拦截器
* @Date Created in 10:08 2018/2/5
**/        
@Bean
@Slf4j
public class BaseWebHook implements WebHook {

    /**
    * @Author qyw
    * @Description TODO
    * @Date Created in 22:18 2018/6/5
    * @Param [signature]
    * @Return boolean
    **/        
    @Override
    public boolean before(Signature signature) {
        Request  request  = signature.request();
        Response response = signature.response();

        String uri = request.uri();
        String ip  = request.address();

        // 禁止该ip访问
        if (TaleConst.BLOCK_IPS.contains(ip)) {
            response.text("You have been banned, brother");
            return false;
        }

        log.info("UserAgent: {}", request.userAgent());
        log.info("用户访问地址: {}, 来路地址: {}", uri, ip);

        if (uri.startsWith(TaleConst.STATIC_URI)) {
            return true;
        }
        //未安装跳转到安装界面
        if (!TaleConst.INSTALLED && !uri.startsWith(TaleConst.INSTALL_URI)) {
            response.redirect(TaleConst.INSTALL_URI);
            return false;
        }

        if (TaleConst.INSTALLED) {
            return isRedirect(request, response);
        }
        return true;
    }

    private boolean isRedirect(Request request, Response response) {
        Users  user = ZaleUtils.getLoginUser();
        String uri  = request.uri();
        if (null == user) {
            Integer uid = ZaleUtils.getCookieUid(request);
            if (null != uid) {
                user = new Users().find(uid);
                request.session().attribute(TaleConst.LOGIN_SESSION_KEY, user);
            }
        }
        if (uri.startsWith(TaleConst.ADMIN_URI) && !uri.startsWith(TaleConst.LOGIN_URI)) {
            if (null == user) {
                response.redirect(TaleConst.LOGIN_URI);
                return false;
            }
            request.attribute(TaleConst.PLUGINS_MENU_NAME, TaleConst.PLUGIN_MENUS);
        }
        return true;
    }

}
