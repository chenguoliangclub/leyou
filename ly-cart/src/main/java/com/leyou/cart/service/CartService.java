package com.leyou.cart.service;

import com.leyou.auth.utils.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utills.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:user:";

    public void add(Cart cart){
        //获取用户
        UserInfo userInfo = UserInterceptor.getUserInfo();
        //获取key
        String key = KEY_PREFIX + userInfo.getId();
        String hashKey = cart.getSkuId().toString();
        //判断购物车是否已存在
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        if (operations.hasKey(hashKey)){
            //是，增加数量
            String json = operations.get(hashKey).toString();
            Cart cacheCart = JsonUtils.parse(json, Cart.class);
            cacheCart.setNum(cacheCart.getNum() + cart.getNum());
            operations.put(hashKey,JsonUtils.serialize(cacheCart));
        }else{
            //否，创建购物车
            operations.put(hashKey,JsonUtils.serialize(cart));
        }
    }

    public List<Cart> getCartList() {
        //获取用户
        UserInfo userInfo = UserInterceptor.getUserInfo();
        //获取key
        String key = KEY_PREFIX + userInfo.getId();
        //判断redis中是否存在
        if (redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //在redis中查询数据
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Object> values = operations.values();
        List<Cart> carts = values.stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
        return carts;
    }

    public void putCart(Long skuId, int num) {
        //获取用户
        UserInfo userInfo = UserInterceptor.getUserInfo();
        //获取key
        String key = KEY_PREFIX + userInfo.getId();
        String hashKey = skuId.toString();
        //判断购物车是否已存在
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        if (operations.hasKey(hashKey)){
            //是，增加数量
            String json = operations.get(hashKey).toString();
            Cart cacheCart = JsonUtils.parse(json, Cart.class);
            cacheCart.setNum(num);
            operations.put(hashKey,JsonUtils.serialize(cacheCart));
        }else{
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
    }

    public void deleteCart(Long skuId) {
        //获取用户
        UserInfo userInfo = UserInterceptor.getUserInfo();
        //获取key
        String key = KEY_PREFIX + userInfo.getId();
        String hashKey = skuId.toString();
        //删除购物车
        redisTemplate.opsForHash().delete(key,hashKey);
    }
}
