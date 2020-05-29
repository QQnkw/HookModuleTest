package com.mode.weibo;

import java.io.*;
import java.net.Socket;

public class TcpClient {
    public static final String adb_path = "C:\\Program Files (x86)\\Nox\\bin\\nox_adb";//adb所在路径
    //    private static final String HOST = "localhost";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public TcpClient() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("login exception" + ex.getMessage());
        }
    }

    private void openUrl(String msg) {
        if (socket.isConnected()) {
            if (!socket.isOutputShutdown()) {
                out.println(msg);
                try {
                    String s = in.readLine();
                    System.out.println("服务端返回数据:" + s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage() {
        try {
            //把虚拟机的8090端口绑定到PC本机的8080端口，这样当PC向8080发送数据时实际上是发到虚拟机的8090端口
            //Runtime.getRuntime().exec(G3ExpPCclient.adb_path + " –s emulator-5554 forward tcp:8080 tcp:8090");这个方法不好用
            Runtime.getRuntime().exec(adb_path + " forward tcp:8080 tcp:8090");//这个好用
            System.out.println("已经将虚拟机端口8090绑定到PC端口8080 " + adb_path);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String msg = "输入完整路径http://www.baidu.com ";
        if (msg.equals("exit")) {
            System.out.println("退出");
            System.exit(-1);
        } else {
            openUrl(msg);
        }
    }
}
