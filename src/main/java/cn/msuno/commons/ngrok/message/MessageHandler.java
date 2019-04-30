package cn.msuno.commons.ngrok.message;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.msuno.commons.ngrok.SocketFactory;
import cn.msuno.commons.ngrok.pool.DataSwapRunnable;
import cn.msuno.commons.ngrok.pool.HealthCheckRunnable;
import cn.msuno.commons.ngrok.pool.MessageListenerRunnable;
import cn.msuno.commons.ngrok.pool.ThreadPoolUtils;
import cn.msuno.commons.ngrok.tunnel.Tunnel;
import cn.msuno.commons.util.CommonsUtils;

public class MessageHandler {
    private Logger log = LoggerFactory.getLogger(getClass());
    private SSLSocket socket;
    private SocketFactory socketFactory;
    private String clientId;
    private List<Tunnel> tunnels;
    private Map<String, Tunnel> tunnelMap = new HashMap<>();
    
    public MessageHandler(SSLSocket socket, SocketFactory socketFactory, List<Tunnel> tunnels) {
        this.socket = socket;
        this.socketFactory = socketFactory;
        this.tunnels = tunnels;
    }
    
    private MessageHandler newSocketAndCopy() {
        SSLSocket newSocket = socketFactory.build();
        MessageHandler messageHandler = new MessageHandler(newSocket, socketFactory, null);
        messageHandler.setClientId(this.clientId);
        messageHandler.setTunnelMap(this.tunnelMap);
        return messageHandler;
    }
    
    public boolean onMessage(JSONObject json) throws Exception {
        String type = json.getString("Type");
        JSONObject payload = json.getJSONObject("Payload");
        switch (type) {
            case "AuthResp": {
                String clientId = payload.getString("ClientId");
                this.clientId = clientId;
                String error = payload.getString("Error");
                if (CommonsUtils.isBlank(error)) {
                    log.debug("auth succeed...");
                    sendReqTunnel();
                    ThreadPoolUtils.submit(new HealthCheckRunnable(this));
                } else {
                    ThreadPoolUtils.shutdown();
                    log.error("auth failed error: {}", error);
                }
                break;
            }
            case "NewTunnel": {
                String error = payload.getString("Error");
                String reqId = payload.getString("ReqId");
                String url = payload.getString("Url");
                if (CommonsUtils.isBlank(error)) {
                    tunnelMap.put(url, tunnelMap.get(reqId));
                    log.info("register url: {}", url);
                } else {
                    ThreadPoolUtils.shutdown();
                    log.error("NewTunnel failed error: {}", error);
                }
                break;
            }
            case "ReqProxy":
                //注册代理需要新的线程和连接
                MessageHandler messageHandler = newSocketAndCopy();
                messageHandler.sendRegProxy();
                ThreadPoolUtils.submit(new MessageListenerRunnable(messageHandler));
                break;
            case "StartProxy": {
                String url = payload.getString("Url");
                Tunnel tunnel = tunnelMap.get(url);
            
                Socket locals = new Socket("127.0.0.1", Integer.valueOf(tunnel.getPort()));
                ThreadPoolUtils.submit(new DataSwapRunnable(this.socket.getInputStream(), locals.getOutputStream()));
                ThreadPoolUtils.submit(new DataSwapRunnable(locals.getInputStream(), this.socket.getOutputStream()));
            
                return true;
            }
        }
        return false;
    }
    
    public void sendAuth(){
        JSONObject request = new JSONObject();
        request.put("Type", "Auth");
        JSONObject payload = new JSONObject();
        payload.put("Version", "2");
        payload.put("MmVersion", "1.7");
        payload.put("User", "");
        payload.put("Password", "");
        payload.put("OS", "darwin");
        payload.put("Arch", "amd64");
        payload.put("ClientId", "");
        request.put("Payload", payload);
        sendMessage(request.toJSONString());
    }
    
    public void sendReqTunnel() {
        for (Tunnel tunnel : this.tunnels) {
            JSONObject reuqest = new JSONObject();
            reuqest.put("Type", "ReqTunnel");
            
            JSONObject payload = new JSONObject();
            String reqId = UUID.randomUUID().toString()
                    .toLowerCase().replace("-", "")
                    .substring(0, 16);
            tunnelMap.put(reqId, tunnel);
            payload.put("ReqId", reqId);
            payload.put("Protocol", tunnel.getProto());
            if (tunnel.getProto().equals("tcp")) {
                payload.put("RemotePort", tunnel.getRemotePort());
            } else {
                payload.put("Subdomain", tunnel.getSubDomain());
                payload.put("HttpAuth", tunnel.getHttpAuth());
                payload.put("Hostname", tunnel.getHostname());
            }
            reuqest.put("Payload", payload);
            sendMessage(reuqest.toJSONString());
        }
    }
    
    public void sendPing(){
        sendMessage("{\"Type\":\"Ping\",\"Payload\":{}}");
    }
    
    public void sendPong(){
        sendMessage("{\"Type\":\"Pong\",\"Payload\":{}}");
    }
    
    public void sendRegProxy(){
        sendMessage("{\"Type\":\"RegProxy\",\"Payload\":{\"ClientId\":\"" + clientId + "\"}}");
    }
    
    public void sendMessage(String str){
        log.debug("Writing message: {}", str);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(this.socket.getOutputStream());
            byte[] len = ByteBuffer.allocate(8).putLong(str.length()).array();
            CommonsUtils.reverse(len);
            ByteBuffer wrap = ByteBuffer.allocate(str.length() + 8);
            byte[] array = wrap.put(len).put(str.getBytes()).array();
        
            bos.write(array);
            bos.flush();
        } catch (IOException e) {
            log.error("occurred some exception", e);
        }
    }
    
    public SSLSocket getSocket() {
        return socket;
    }
    
    public void setSocket(SSLSocket socket) {
        this.socket = socket;
    }
    
    public SocketFactory getSocketFactory() {
        return socketFactory;
    }
    
    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public List<Tunnel> getTunnels() {
        return tunnels;
    }
    
    public void setTunnels(List<Tunnel> tunnels) {
        this.tunnels = tunnels;
    }
    
    public Map<String, Tunnel> getTunnelMap() {
        return tunnelMap;
    }
    
    public void setTunnelMap(Map<String, Tunnel> tunnelMap) {
        this.tunnelMap = tunnelMap;
    }
}
