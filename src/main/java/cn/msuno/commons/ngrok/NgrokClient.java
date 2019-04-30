package cn.msuno.commons.ngrok;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.msuno.commons.exception.NgrokClientExecption;
import cn.msuno.commons.ngrok.message.MessageHandler;
import cn.msuno.commons.ngrok.pool.MessageListenerRunnable;
import cn.msuno.commons.ngrok.pool.ThreadPoolUtils;
import cn.msuno.commons.ngrok.tunnel.Tunnel;

public class NgrokClient {
    private static Logger log = LoggerFactory.getLogger(NgrokClient.class);
    
    private SSLSocket socket;
    private final SocketFactory socketFactory;
    private List<Tunnel> tunnels = new ArrayList<>();
    
    public NgrokClient(String serverAddress, int serverPort) {
        this.socketFactory = new SocketFactory(serverAddress, serverPort);
    }
    
    public void start() {
        try {
            log.info("start");
            this.socket = this.socketFactory.build();
        } catch (NgrokClientExecption e) {
            log.error("Ngrok Start failed: ", e);
            return;
        }
        
        MessageHandler messageHandler = new MessageHandler(socket, socketFactory, this.tunnels);
        ThreadPoolUtils.submit(new MessageListenerRunnable(messageHandler));
        messageHandler.sendAuth();
        
    }
    
    public List<Tunnel> getTunnels() {
        return this.tunnels;
    }
    
    public NgrokClient addTunnel(Tunnel tunnel) {
        this.tunnels.add(tunnel);
        return this;
    }
}
