package Manager;

import thread.GameThread;

import java.util.ArrayList;

public class ThreadPool extends Thread{
    private static ArrayList<GameThread> pool = new ArrayList<>();
    int MIN = 15;
    public ThreadPool() {
        for (int i = 0; i < MIN; i++) {
            pool.add(new GameThread());
        }
    }
    public static GameThread getFreeThread(){
        for (int i = 0; i < pool.size(); i++) {
            if(!pool.get(i).getThreadState()) return pool.get(i);
        }
        GameThread addThread = new GameThread();
        pool.add(addThread);
        return addThread;
    }
}
