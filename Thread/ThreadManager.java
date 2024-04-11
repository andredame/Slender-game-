package Thread;
import Elements.*;
import java.util.HashMap;
import java.util.Map;

public class ThreadManager {

    private Map<Integer,Thread> threads;

    public Map<Integer, Thread> getThreads() {
        return threads;
    }

    public void setThreads(Map<Integer, Thread> threads) {
        this.threads = threads;
    }

    public ThreadManager() {
        this.threads = new HashMap<Integer,Thread>() ;
    }

    public void addThread(Thread thread) {
        if (thread instanceof CharacterThread) {
            CharacterThread characterThread = (CharacterThread) thread;
            threads.put(characterThread.getIdentificador(), thread);
        } else if (thread instanceof ThrowableThread) {
            ThrowableThread throwableThread = (ThrowableThread) thread;
            threads.put(throwableThread.getIdentificador(), thread);
        }
    }

    public void checkLife() {
        for (Thread thread : threads.values()) {
            if (thread instanceof CharacterThread) {
                CharacterThread characterThread = (CharacterThread) thread;
                GameCharacter character = characterThread.getCharacter();
                if (!character.isAlive()) {
                    threads.remove(character.getId());
                }
            }
        }
    }

    public void initiateThreads() {
        for (Thread thread : threads.values()) {
            thread.start();
        }
    }

    public void startAllThreads() {
        for (Thread thread : threads.values()) {
            thread.start();
        }
    }

    public void stopAllThreads() {
        for (Thread thread : threads.values()) {
            thread.interrupt();
        }
    }

    public boolean isCharacterAtPosition(int x, int y) {
        for (Thread thread : threads.values()) {
            if (thread instanceof CharacterThread) {
                CharacterThread characterThread = (CharacterThread) thread;
                GameCharacter character = characterThread.getCharacter();
                if (character.getX() == x && character.getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }
    



}   