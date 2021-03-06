package com.leyou.cart.interceptor;


import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.UserInfo;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utills.CookieUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.PortableInterceptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private JwtProperties props;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<UserInfo>();

    public UserInterceptor(JwtProperties props){
        this.props = props;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, props.getCookieName());
        if (StringUtils.isBlank(token)) {
            // 未登录,返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        try{
            //解析token
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, props.getPublicKey());
            tl.set(userInfo);
        }catch (Exception e){
            log.error("[购物车服务]解析用户身份失败",e);
            return false;
        }
        //把用户信息放到线程
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }

    public static UserInfo getUserInfo(){
        return tl.get();
    }
}
