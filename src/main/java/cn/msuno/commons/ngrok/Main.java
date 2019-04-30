package cn.msuno.commons.ngrok;

import cn.msuno.commons.ngrok.tunnel.Tunnel;

public class Main {
    public static void main(String[] args){
        final String serverAddress = "yzliusha.cn";
        final int serverPort = 8083;
        Tunnel tunnel = new Tunnel.TunnelBuild()
                .setPort(8080).setProto("http")
                .setSubDomain("ngrok").build();
        new NgrokClient(serverAddress,serverPort).addTunnel(tunnel).start();
    }
}
