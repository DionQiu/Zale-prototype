package com.zale;

import com.blade.Blade;
import com.blade.security.web.csrf.CsrfMiddleware;
import com.blade.validator.ValidatorMiddleware;
import com.zale.init.TaleLoader;

/**
* @Author qyw
* @Description 启动类
* @Date Created in 10:09 2018/2/4
**/        
public class Application {

    public static void main(String[] args) throws Exception {
        Blade blade = Blade.me();
        TaleLoader.init(blade);
        blade.use(new ValidatorMiddleware(), new CsrfMiddleware()).start(Application.class, args);
    }

}