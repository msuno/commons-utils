package cn.msuno.commons.ngrok;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.msuno.commons.exception.NgrokClientExecption;

public class SocketFactory {
    private Logger log = LoggerFactory.getLogger(SocketFactory.class);
    private final String serverAddress;
    private final int serverPort;
    
    public SocketFactory(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    
    public SSLSocket build() {
        try {
            SSLSocket socket = (SSLSocket) trustAllSocketFactory().createSocket(serverAddress, serverPort);
            socket.startHandshake();
            return socket;
        } catch (Exception e) {
            throw new NgrokClientExecption("Create connect failed,Please check ngrok server!", e);
        }
    }
    
    public SSLSocketFactory trustAllSocketFactory() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                    
                }
        };
        SSLContext sslCxt = SSLContext.getInstance("TLSv1.2");
        sslCxt.init(null, trustAllCerts, null);
        return sslCxt.getSocketFactory();
    }
    
}
