package com.leyou.sms.mq;

import com.leyou.common.utills.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utills.SmsUtills;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

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
        if (StringUtils.isBlank(phone)){
            return ;
        }
        smsUtills.sendSms(phone,prop.getSignName(),prop.getVerifyCodeTemplate(), JsonUtils.serialize(msg));
    }
}
