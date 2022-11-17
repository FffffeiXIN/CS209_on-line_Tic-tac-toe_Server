package Manager;

import entity.GameRoom;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomManager {
    private static ArrayList<GameRoom> rooms;
//    int MIN = 5;

    public RoomManager() {
        rooms = new ArrayList<>();
//        for (int i = 0; i < MIN; i++) {
//            rooms.add(new GameRoom());
//        }
        //for test
//        for (int i = 0; i < 3; i++) {
//            rooms.get(i).join("aaa+"+i,new Socket());
//        }
    }
    public GameRoom createRoom(String p, Socket sp){
        GameRoom newRoom = new GameRoom(p,sp, this);
        rooms.add(newRoom);
        return newRoom;
    }
    public GameRoom getRoom(String opponent){
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).player1.equals(opponent)){
                return rooms.get(i);
            }
        }
        return null;
    }
    public void deleteRoom(GameRoom gameRoom){
        rooms.remove(gameRoom);
    }

    public String getInfo(){
        String info = "";
        for (int i = 0; i < rooms.size(); i++) {
            GameRoom room = rooms.get(i);
            if(room.player1!=null && room.player2!=null){
                info += "full";
            }else{
                info+="available ";
                if(room.player1!=null) info+= room.player1+" ";
                if(room.player2!=null) info+= room.player2+" ";
            }
            info+="\r\n";
        }
        return info;
    }
}
