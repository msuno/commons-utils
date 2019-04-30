package cn.msuno.commons.ngrok.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.msuno.commons.ngrok.message.MessageHandler;

public class HealthCheckRunnable implements Runnable {
    Logger log = LoggerFactory.getLogger(HealthCheckRunnable.class);
    
    private final MessageHandler messageHandler;
    
    public HealthCheckRunnable(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
    @Override
    public void run() {
        try {
            while (true) {
                messageHandler.sendPing();
                Thread.sleep(30000);
            }
        }catch (Exception e){
            log.error("Occurred some execption: {}",e.getMessage());
        }
    }
}
