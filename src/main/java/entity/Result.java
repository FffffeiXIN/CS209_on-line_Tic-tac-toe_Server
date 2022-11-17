package entity;

import java.io.Serializable;

public class Result implements Serializable {
    int state_code; //200：操作成功 301：对手掉线
    int[][] chessboard;
    boolean win;
    String p1;
    String p2;

    public Result(int[][] chessboard, boolean win, int code, String p1, String p2){
        this.chessboard = chessboard;
        this.win = win;
        state_code = code;
        this.p1 = p1;
        this.p2 = p2;
    }

}
