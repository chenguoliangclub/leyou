package com.leyou.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utills.CookieUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class authFilter extends ZuulFilter {
    @Autowired
    private JwtProperties props;
    @Autowired
    private FilterProperties filterProperties;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.DEBUG_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext cxt = RequestContext.getCurrentContext();
        //获取请求对象
        HttpServletRequest request = cxt.getRequest();
        //获取请求路径
        String requestURI = request.getRequestURI();
        //判断请求路径是否在白名单
        return !inWhiteList(requestURI);
    }

    private boolean inWhiteList(String url) {
        for (String allowPath:(filterProperties.getAllowPaths())){
            if (url.startsWith(allowPath))
                return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext cxt = RequestContext.getCurrentContext();
        //获取请求对象
        HttpServletRequest request = cxt.getRequest();
        //获取cookie
        String token = CookieUtils.getCookieValue(request, props.getCookieName());
        //解析token
        try{
            JwtUtils.getInfoFromToken(token,props.getPublicKey());
        }catch (Exception e){
            //token不合法拦截
            cxt.setSendZuulResponse(false);
            cxt.setResponseStatusCode(403);
        }
        return null;
    }
}
