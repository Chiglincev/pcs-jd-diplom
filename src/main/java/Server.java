import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;


    public Server(int port){
        this.port = port;
    }

    public void start(BooleanSearchEngine engine) throws IOException {
        Gson gson = new Gson();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Starting server at " + port + "...");

            while (true) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {
                    String word = in.readLine().toLowerCase();
                    out.println(gson.toJson(engine.search(word)));
                }
            }
        }
    }
}
