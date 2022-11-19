package thread;

import Manager.RoomManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RequestThread extends Thread {
    RoomManager roomManager;

    public RequestThread(RoomManager roomManager) {
        this.roomManager = roomManager;

    }

    @Override
    public void run() {
        try {
            // 要接收的报文
            byte[] bytes = new byte[1024];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            // 创建socket并指定端口
            DatagramSocket socket = new DatagramSocket(8080);

            // 接收socket客户端发送的数据。如果未收到会一致阻塞
            while (true) {
                socket.receive(packet);
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();
                String receiveMsg = new String(packet.getData(), 0, packet.getLength());

                System.out.println(receiveMsg);
                //处理信息
//                String name = receiveMsg.split(" ")[0];
//                String passwd = receiveMsg.split(" ")[1];
                String name = receiveMsg;
                //连接数据库
                Connection c = null;
                Statement stmt = null;
                int win=0;
                int lose=0;
                int draw=0;
                try {
                    Class.forName("org.postgresql.Driver");
                    c = DriverManager.getConnection("jdbc:postgresql://10.16.4.246:5432/cs209_a2",
                            "postgres", "123456");
                    System.out.println("Opened database successfully");

                    stmt = c.createStatement();
                    String sql = "SELECT * FROM users where name=\'" + name + "\';";
                    System.out.println(sql);
                    ResultSet rs = stmt.executeQuery(sql);
                    boolean exit = false;
                    while (rs.next()) {
                        exit = true;
                        win = rs.getInt("win");
                        lose = rs.getInt("lose");
                        draw = rs.getInt("draw");
                        System.out.println("win = " + win);
                        System.out.println("lose = " + lose);
                        System.out.println("draw = " + draw);
                        System.out.println();
                    }
                    if (!exit) {
                        win = 0;
                        lose = 0;
                        draw = 0;
                        sql = "INSERT INTO users (name,win,lose,draw) VALUES (\'" + name + "\', 0,0,0);";
                        System.out.println(sql);
                        stmt.executeUpdate(sql);
                    }
                    rs.close();
                    stmt.close();
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }

                String response = win+" "+lose+" "+draw+"\r\n"+roomManager.getInfo();

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
