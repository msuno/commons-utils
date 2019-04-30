package cn.msuno.commons.ngrok.pool;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.msuno.commons.ngrok.message.MessageHandler;
import cn.msuno.commons.util.CommonsUtils;

public class MessageListenerRunnable implements Runnable {
    Logger log = LoggerFactory.getLogger(getClass());
    private final MessageHandler messageHandler;
    private final SSLSocket socket;
    public MessageListenerRunnable(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.socket = messageHandler.getSocket();
    }
    @Override
    public void run() {
        try {
            log.debug("Waiting to read message");
            byte[] hLen = new byte[8];
            byte[] strByte;
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            while (true) {
                while (bis.available() >= 8) {
                
                }
                int i = bis.read(hLen);
                if (i == -1) {
                    return;
                }
            
                CommonsUtils.reverse(hLen);
                int strLen = ((Long) ByteBuffer.wrap(hLen).getLong()).intValue();
                log.debug("Reading message with length: {}", strLen);
                strByte = new byte[strLen];
                int readCount = 0;
                while (readCount < strLen) {
                    int read = bis.read(strByte, readCount, strLen - readCount);
                    if (read == -1) {
                        return;
                    }
                    readCount += read;
                }
                JSONObject json = JSON.parseObject(strByte, JSONObject.class);
                log.debug("Read message: {}", json.toJSONString());
                if (messageHandler.onMessage(json)) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Occurred some exception", e);
        }
    }
}
