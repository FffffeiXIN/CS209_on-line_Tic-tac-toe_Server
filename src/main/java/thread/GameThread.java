package thread;

import Manager.RoomManager;
import entity.GameRoom;

import java.io.*;
import java.net.Socket;

public class GameThread extends Thread {
    private int PLAY_1 = 1;
    private int PLAY_2 = -1;
    private int EMPTY = 0;
    private boolean TURN = true;
    private int[][] chessBoard = new int[3][3];
    private boolean win = false;
    private boolean full = false;
    String p1;
    String p2;
    Socket splayer1;
    Socket splayer2;
    public GameRoom gameRoom;
    public RoomManager roomManager;
    boolean state = false;

    //先setPlayer再run
    public void setPlayer(Socket player1, Socket player2, String p1, String p2) {
        this.splayer1 = player1;
        this.splayer2 = player2;
        this.p1 = p1;
        this.p2 = p2;
        state = true;
    }


    @Override
    public void run() {
        //告诉双方游戏开始 前端初始化游戏界面
        try {
            String start_mess = "ok to start";
            byte[] send_bytes1 = start_mess.getBytes();
            OutputStream os1 = splayer1.getOutputStream();
            os1.write(send_bytes1);
            os1.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                System.out.println("1");
                InputStream is1 = splayer1.getInputStream();
                byte[] buf1 = new byte[1024];
                int readLen = 0;
                readLen = is1.read(buf1);
                String action_str = new String(buf1, 0, readLen);
                //判读是否退出
                if (action_str.equals("exit")){
                    //给玩家2发消息说对方走了
                    String exit_info ="oppExit";
                    byte[] send_exit = exit_info.getBytes();
                    OutputStream os2 = splayer2.getOutputStream();
                    os2.write(send_exit);
                    os2.flush();
                    break;
                }
                else {
                    //处理数据 拿到 x y
                    int x = Integer.parseInt(action_str.split(" ")[0]);
                    int y = Integer.parseInt(action_str.split(" ")[1]);
                    chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
                    print();
                    win = checkWin(x, y);
                    TURN = !TURN;
                    //返回报文格式：是否获胜 双方都发
                    String is_win = win ? "Yes " : "No ";
                    is_win += x + " " + y + " ";
                    //检查是否终局
                    full = checkFull();
                    if (full) is_win += "full";
                    byte[] send_win = is_win.getBytes();
                    OutputStream os1 = splayer1.getOutputStream();
                    os1.write(send_win);
                    os1.flush();
                    OutputStream os2 = splayer2.getOutputStream();
                    os2.write(send_win);
                    os2.flush();

                    if (win || full) break;//游戏结束

                    System.out.println("2");
                    InputStream is2 = splayer2.getInputStream();
                    byte[] buf2 = new byte[1024];
                    int readLen2 = 0;
                    readLen2 = is2.read(buf2);
                    String action_str2 = new String(buf2, 0, readLen2);
                    if (action_str2.equals("exit")){
                        //给玩家1发消息说对方走了
                        String exit_info ="oppExit";
                        byte[] send_exit = exit_info.getBytes();
                        os1.write(send_exit);
                        os1.flush();
                        break;
                    }
                    //处理数据 拿到 x y
                    int x2 = Integer.parseInt(action_str2.split(" ")[0]);
                    int y2 = Integer.parseInt(action_str2.split(" ")[1]);
                    chessBoard[x2][y2] = TURN ? PLAY_1 : PLAY_2;
                    print();
                    win = checkWin(x2, y2);
                    TURN = !TURN;
                    is_win = win ? "Yes " : "No ";
                    is_win += x2 + " " + y2 + " ";
                    if (full) is_win += "full";
                    send_win = is_win.getBytes();
                    os1 = splayer1.getOutputStream();
                    os1.write(send_win);
                    os1.flush();
                    os2 = splayer2.getOutputStream();
                    os2.write(send_win);
                    os2.flush();
                    if (win || full) break;//游戏结束
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
//        try {
//            if (splayer1 != null)
//                splayer1.close();
//            if (splayer2 != null)
//                splayer2.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        roomManager.deleteRoom(gameRoom);
    }

    public boolean getThreadState() {
        return state;
    }

    public boolean checkWin(int x, int y) {
        int piece = chessBoard[x][y];
        if (chessBoard[x][0] == chessBoard[x][1] && chessBoard[x][1] == chessBoard[x][2]) return true;
        if (chessBoard[0][y] == chessBoard[1][y] && chessBoard[1][y] == chessBoard[2][y]) return true;
        if (x == y) {
            if (chessBoard[0][0] == chessBoard[1][1] && chessBoard[2][2] == chessBoard[1][1]) return true;
        }
        if (x + y == 2) {
            if (chessBoard[0][2] == chessBoard[1][1] && chessBoard[2][0] == chessBoard[1][1]) return true;
        }
        return false;
    }

    public boolean checkFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (chessBoard[i][j] == EMPTY) return false;
            }
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(chessBoard[i][j] + " ");
            }
            System.out.println();
        }
    }
}
