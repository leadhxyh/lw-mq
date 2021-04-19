package com.lw.mq.rest.boot;

import com.lw.mq.biz.inf.TimerService;
import com.lw.mq.biz.common.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MqBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    private static final Logger log = LoggerFactory.getLogger(MqBootstrapListener.class);

    private static boolean isInit = false;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!isInit) {
            try {
                startTimer();
            } catch (Exception e) {
                log.error("mq初始化异常", e);
                throw e;
            }
        }
    }

    private void startTimer() {
        Map<String, TimerService> startedServices = SpringUtil.getBeans(TimerService.class);
        if (startedServices != null) {
            startedServices.entrySet().forEach(t1 -> {
                try {
                    t1.getValue().start();
                    log.info(t1.getKey() + "启动完成!");
                } catch (Exception e) {
                    log.error(t1.getKey() + "启动异常!", e);
                }
            });
        }
    }
}
