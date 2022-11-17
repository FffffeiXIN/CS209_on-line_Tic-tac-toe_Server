package thread;

import Manager.RoomManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestThread extends Thread{
    RoomManager roomManager;

    public RequestThread(RoomManager roomManager){
        this.roomManager = roomManager;

    }
    @Override
    public void run(){
        try {
            // 要接收的报文
            byte[] bytes = new byte[1024];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            // 创建socket并指定端口
            DatagramSocket socket = new DatagramSocket(8080);

            // 接收socket客户端发送的数据。如果未收到会一致阻塞
            while (true){
                socket.receive(packet);
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();
                String receiveMsg = new String(packet.getData(),0,packet.getLength());

                System.out.println(receiveMsg);
                String response = roomManager.getInfo();

                // 创建packet包对象，封装要发送的包数据和服务器地址和端口号
                DatagramPacket send_packet = new DatagramPacket(response.getBytes(),
                        response.getBytes().length, addr, port);

                // 发送消息到服务器
                socket.send(send_packet);

            }
            // 关闭socket
//            socket.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
