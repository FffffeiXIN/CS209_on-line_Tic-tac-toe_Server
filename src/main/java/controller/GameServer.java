package controller;

import Manager.RoomManager;
import entity.GameRoom;
import thread.RequestThread;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
//    public static void connect_enter(ServerSocket s,RoomManager roomManager) throws IOException {
//        Socket clientSocket = s.accept();
//        //收到消息
//        InputStream is = clientSocket.getInputStream();
//        byte[] buf = new byte[1024];
//        int readLen = 0;
//        String prepare_request="";
//        while ((readLen = is.read(buf)) != -1) {
//            prepare_request = new String(buf, 0, readLen);
//        }
//        //解析报文 Create GameRoom/player myname
//        String[] request_info = prepare_request.split(" ");
//        if (request_info[0].equals("Create")){
//            roomManager.createRoom(request_info[1],clientSocket);
//        }
//        else {
//            String opponent = request_info[0];
//            GameRoom room = roomManager.getRoom(opponent);
//            room.join(request_info[1],clientSocket);
//        }


        //创建房间or进入房间
        //创建房间：new 房间
        //进入房间：join进去，判断是否准备，都准备了开始
        //报文格式： 1（第一次消息：创建or进入 2（下棋）
//    }


    public static void main(String[] args) {

        RoomManager roomManager = new RoomManager();
        Thread requestThread = new RequestThread(roomManager);
        requestThread.start();

        try {
            // 创建服务端socket
            ServerSocket serverSocket = new ServerSocket(8081);
//
            // 创建客户端socket
//            Socket socket = new Socket();

            //循环监听等待客户端的连接
            while(true){
                // 监听客户端
                //客户原子操作：请求连接+创建房间or进入房间
                Socket clientSocket = serverSocket.accept();
                //收到消息
                InputStream is = clientSocket.getInputStream();
                byte[] buf = new byte[1024];
                int readLen = 0;
                String prepare_request="";
                readLen = is.read(buf);
                prepare_request = new String(buf, 0, readLen);
                //解析报文 Create GameRoom/player myname
                String[] request_info = prepare_request.split(" ");
                if (request_info[0].equals("Create")){
                    roomManager.createRoom(request_info[1],clientSocket);
                }
                else {
                    String opponent = request_info[0];
                    GameRoom room = roomManager.getRoom(opponent);
                    room.join(request_info[1],clientSocket);
                }
//                connect_enter(serverSocket,roomManager);
//                socket = serverSocket.accept();
//
//                ServerThread thread = new ServerThread(socket);
//                thread.start();
//
//                InetAddress address=socket.getInetAddress();
//                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
    }
}
