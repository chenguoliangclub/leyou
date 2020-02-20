package com.leyou.sms.mq;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.leyou.common.utills.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utills.SmsUtills;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListenner {

    @Autowired
    private SmsProperties prop;

    @Autowired
    private SmsUtills smsUtills;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "ly.sms.exchange",type = ExchangeTypes.TOPIC),
            key = "sms.verify.code"
    ))
    public void listenSmsSend(Map<String,String> msg){
        if(CollectionUtils.isEmpty(msg)){
            return ;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isEmpty(phone)){
            return ;
        }
        SendSmsResponse sendSmsResponse = smsUtills.sendSms(phone, prop.getSignName(), prop.getVerifyCodeTemplate(), JsonUtils.serialize(msg));
        log.info("短信发送成功，手机号为{},验证码为{}",phone,msg.get("code"));
    }
}
