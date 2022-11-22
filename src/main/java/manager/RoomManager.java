package manager;

import entity.GameRoom;
import java.net.Socket;
import java.util.ArrayList;

public class RoomManager {
    private static ArrayList<GameRoom> rooms;

    public RoomManager() {
        rooms = new ArrayList<>();
    }

    public GameRoom createRoom(String p, Socket sp) {
        GameRoom newRoom = new GameRoom(p, sp, this);
        rooms.add(newRoom);
        return newRoom;
    }

    public GameRoom getRoom(String opponent) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).player1.equals(opponent)) {
                return rooms.get(i);
            }
        }
        return null;
    }

    public void deleteRoom(GameRoom gameRoom) {
        rooms.remove(gameRoom);
    }

    public String getInfo() {
        String info = "";
        for (int i = 0; i < rooms.size(); i++) {
            GameRoom room = rooms.get(i);
            if (room.player1 != null && room.player2 != null) {
                info += "full";
            } else {
                info += "available ";
                if (room.player1 != null) info += room.player1 + " ";
                if (room.player2 != null) info += room.player2 + " ";
            }
            info += "\r\n";
        }
        return info;
    }
}
