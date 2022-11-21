package entity;

import Manager.RoomManager;
import Manager.ThreadPool;
import thread.GameThread;

import java.net.Socket;

public class GameRoom {
    public String player1;
    public String player2;
    public Socket p1;
    public Socket p2;
    GameThread thread;
    RoomManager roomManager;


    public GameRoom(String p, Socket sp, RoomManager roomManager) {
        player1 = p;
        p1 = sp;
        player2 = null;
        p2 = null;
        this.roomManager = roomManager;
    }

    public void join(String p, Socket sp) {
        player2 = p;
        p2 = sp;
        startGame();
    }

    public void startGame() {
        //可以加exception 当前玩家过多
        thread = ThreadPool.getFreeThread();
        thread.setPlayer(p1, p2,player1,player2);
        thread.gameRoom = this;
        thread.roomManager = this.roomManager;
        thread.start();
    }

    public void exit() {
        player1 = null;
        p1 = null;
        player2 = null;
        p2 = null;
        roomManager.deleteRoom(this);
    }
}
