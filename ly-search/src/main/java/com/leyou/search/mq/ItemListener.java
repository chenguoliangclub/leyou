package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "ly.create.index.queue",durable = "true"),
                    exchange = @Exchange(
                            value = "ly.item.exchange",
                            ignoreDeclarationExceptions = "true",
                            type = ExchangeTypes.TOPIC
                    ),
                    key = {"item.insert","item.update"}
            )
    )
    public void listenCreate(Long id) throws Exception{
        if (id == null){
            return;
        }
        //创建或更新索引
        this.searchService.createIndex(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.delete.index.queue",durable = "true"),
            exchange = @Exchange(value = "ly.item.exchange",durable = "true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void listenDelete(Long id){
        if (id == null){
            return;
        }
        //删除索引
        this.searchService.deleteIndex(id);
    }
}
