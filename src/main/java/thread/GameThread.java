package thread;

import entity.GameRoom;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import manager.RoomManager;

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
    boolean normal1 = true;
    boolean normal2 = true;

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
                normal1 = false;
                System.out.println("1");
                InputStream is1 = splayer1.getInputStream();
                byte[] buf1 = new byte[1024];
                int readLen = 0;
                readLen = is1.read(buf1);
                normal1 = true;
                String action_str = new String(buf1, 0, readLen);
                //判读是否退出
                if (action_str.equals("exit")) {
                    //给玩家2发消息说对方走了
                    normal1 = false;
                    String exit_info = "oppExit";
                    byte[] send_exit = exit_info.getBytes();
                    OutputStream os2 = splayer2.getOutputStream();
                    os2.write(send_exit);
                    os2.flush();
                    break;
                } else {
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
                    if (full) {
                        is_win += "full";
                    }
                    byte[] send_win = is_win.getBytes();
                    OutputStream os1 = splayer1.getOutputStream();
                    normal1 = false;
                    os1.write(send_win);
                    os1.flush();
                    normal1 = true;
                    normal2 = false;
                    OutputStream os2 = splayer2.getOutputStream();
                    os2.write(send_win);
                    os2.flush();
                    normal2 = true;

                    if (win || full) {
                        break; //游戏结束
                    }

                    normal2 = false;
                    System.out.println("2");
                    InputStream is2 = splayer2.getInputStream();
                    byte[] buf2 = new byte[1024];
                    int readLen2 = 0;
                    readLen2 = is2.read(buf2);
                    normal2 = true;
                    String action_str2 = new String(buf2, 0, readLen2);
                    if (action_str2.equals("exit")) {
                        //给玩家1发消息说对方走了
                        normal2 = false;
                        String exit_info = "oppExit";
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
                    if (full) {
                        is_win += "full";
                    }
                    send_win = is_win.getBytes();
                    normal1 = false;
                    os1 = splayer1.getOutputStream();
                    os1.write(send_win);
                    os1.flush();
                    normal1 = true;
                    normal2 = false;
                    os2 = splayer2.getOutputStream();
                    os2.write(send_win);
                    os2.flush();
                    normal2 = true;
                    if (win || full) {
                        break; //游戏结束
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                //给客户发消息
                if (!normal1) {
                    String exit_info = "oppExit";
                    byte[] send_exit = exit_info.getBytes();
                    OutputStream os = null;
                    try {
                        os = splayer2.getOutputStream();
                        os.write(send_exit);
                        os.flush();
                        break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (!normal2) {
                    String exit_info = "oppExit";
                    byte[] send_exit = exit_info.getBytes();
                    OutputStream os = null;
                    try {
                        os = splayer1.getOutputStream();
                        os.write(send_exit);
                        os.flush();
                        break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    break;
                }
            }
        }
        if (normal1 && normal2) {
            try {
                InputStream is1 = splayer1.getInputStream();
                InputStream is2 = splayer2.getInputStream();
                byte[] buf1 = new byte[1024];
                byte[] buf2 = new byte[1024];
                int readLen1 = 0;
                int readLen2 = 0;
                readLen1 = is1.read(buf1);
                readLen2 = is2.read(buf2);
                String close_str1 = new String(buf1, 0, readLen1);
                String close_str2 = new String(buf2, 0, readLen2);
                String[] info1 = close_str1.split(" ");
                String[] info2 = close_str2.split(" ");
                String name1 = info1[0];
                String win1 = info1[1];
                String lose1 = info1[2];
                String draw1 = info1[3];
                String name2 = info2[0];
                String win2 = info2[1];
                String lose2 = info2[2];
                String draw2 = info2[3];

                Connection c = null;
                Statement stmt = null;

                try {
                    Class.forName("org.postgresql.Driver");
                    c = DriverManager.getConnection("jdbc:postgresql://10.16.4.246:5432/cs209_a2",
                            "postgres", "123456");
                    System.out.println("Opened database successfully");

                    stmt = c.createStatement();
                    String sql1 = "UPDATE users set win = \'" + win1 + "\', lose = \'" + lose1 + "\', draw = \'" + draw1 + "\' where name=\'" + name1 + "\';";
                    String sql2 = "UPDATE users set win = \'" + win2 + "\', lose = \'" + lose2 + "\', draw = \'" + draw2 + "\' where name=\'" + name2 + "\';";
                    System.out.println(sql1);
                    System.out.println(sql2);
                    stmt.executeUpdate(sql1);
                    stmt.executeUpdate(sql2);
                    stmt.close();
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        roomManager.deleteRoom(gameRoom);
        System.out.println("此局游戏结束");
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
