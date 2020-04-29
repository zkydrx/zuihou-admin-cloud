package com.github.zuihou.authority.config.mq;

import com.alibaba.fastjson.JSONObject;
import com.github.zuihou.authority.dto.auth.SystemApiScanSaveDTO;
import com.github.zuihou.authority.service.auth.SystemApiService;
import com.github.zuihou.context.BaseContextHandler;
import com.github.zuihou.mq.constant.QueueConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * 消息队列配置
 *
 * @author zuihou
 * @date 2019/12/17
 */
@Configuration
@AllArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "zuihou.rabbitmq", name = "enabled", havingValue = "true")
public class AuthorityMqAutoConfiguration
{
    private final SystemApiService systemApiService;

    @Bean
    public Queue apiResourceQueue()
    {
        Queue queue = new Queue(QueueConstants.QUEUE_SCAN_API_RESOURCE);
        log.info("Query {} [{}]", QueueConstants.QUEUE_SCAN_API_RESOURCE, queue);
        return queue;
    }

    @RabbitListener(queues = QueueConstants.QUEUE_SCAN_API_RESOURCE)
    public void scanApiResourceRabbitListener(@Payload String param)
    {
        SystemApiScanSaveDTO scan = JSONObject.parseObject(param, SystemApiScanSaveDTO.class);
        BaseContextHandler.setTenant(scan.getTenant());

        this.systemApiService.batchSave(scan);
    }

}
