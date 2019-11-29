package JADevelopmentTeam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Connector {
    private static Connector instance = null;
    private ServerSocket serverSocket = null;

    private Connector(){
        try{
            serverSocket = new ServerSocket(4444);
            initializeServer();
            System.out.println("port assigned");
        }catch (IOException ex){
            System.out.println("Could not listen on port 4444"); System.exit(-1);
        }
    }
    private void initializeServer(){
        Socket socket;
        ExecutorService pool = Executors.newFixedThreadPool(200);
        while (true){
            System.out.println("Oczekuję na klienta");
            try {
                socket = serverSocket.accept();
                pool.execute(new Player(socket));
                System.out.println("Connected!");
            }
            catch (IOException e) {
                System.out.println("Accept failed: 4444"); System.exit(-1);
            }
        }
    }
    static Connector getInstance(){
        if(instance==null){
            instance = new Connector();
        }
        return instance;
    }

}
