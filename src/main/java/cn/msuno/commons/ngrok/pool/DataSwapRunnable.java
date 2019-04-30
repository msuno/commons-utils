package cn.msuno.commons.ngrok.pool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSwapRunnable implements Runnable {
    Logger log = LoggerFactory.getLogger(DataSwapRunnable.class);
    
    private DataInputStream in;
    private DataOutputStream out;
    
    public DataSwapRunnable(InputStream in, OutputStream out) {
        this.in = new DataInputStream(in);
        this.out = new DataOutputStream(out);
    }
    
    @Override
    public void run() {
        try {
            int readBytes;
            byte buf[] = new byte[1024];
            while (true) {
                readBytes = in.read(buf, 0, 1024);
                if (readBytes == -1)
                    break;
                if (readBytes > 0) {
                    out.write(buf, 0, readBytes);
                    out.flush();
                }
            }
            out.close();
            in.close();
        } catch (SocketException e) {
            //链接关闭,等待下一次访问时重新打开
            return;
        } catch (Exception e) {
            log.error("Occurred some exception", e);
        }
    
    }
}
