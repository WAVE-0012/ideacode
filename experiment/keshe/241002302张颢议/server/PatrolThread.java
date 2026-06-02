import java.util.ArrayList;
import java.util.List;

public class PatrolThread extends Thread {
    private Server server;
    private boolean running = true;

    public PatrolThread(Server server) {
        this.server = server;
    }

    public void run() {
        while (running && server.isRunning()) {
            List<ClientHandler> toRemove = new ArrayList<>();
            for (ClientHandler ch : server.getClients()) {
                if (!ch.isConnected()) {
                    toRemove.add(ch);
                }
            }
            for (ClientHandler ch : toRemove) {
                server.removeClient(ch,false);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopThread() {
        running = false;
        interrupt();
    }
}