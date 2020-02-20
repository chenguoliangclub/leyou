package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utills.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //redis保存手机验证码前缀
    static final String VERITIFY_CODE_PREFIX ="sms:phone:";

    public Boolean checkUserData(String data,Integer type){
        User u = new User();
        switch (type){
            case 1:
                u.setUsername(data);
                break;
            case 2:
                u.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        return userMapper.selectCount(u) == 0;
    }

    public void sendCode(String phone){
        String code = NumberUtils.generateCode(6);
        String key = VERITIFY_CODE_PREFIX + phone;
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {
        //获取redis中保存的验证码
        String redisCode = redisTemplate.opsForValue().get(VERITIFY_CODE_PREFIX + user.getPhone());
        //校验验证码
        if (!redisCode.equals(code)){
            throw new LyException(ExceptionEnum.INVALID_VERITIFY_CODE);
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setCreated(new Date());
        //加密密码
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //保存到数据库
        userMapper.insert(user);
    }

    public User queryUserByUsernameAndPassword(String username, String password) {
        User u = new User();
        u.setUsername(username);
        //查询数据库
        User user = userMapper.selectOne(u);
        if (user == null) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //校验密码
        if (!user.getPassword().equals(CodecUtils.md5Hex(password,user.getSalt()))) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return user;
    }
}
