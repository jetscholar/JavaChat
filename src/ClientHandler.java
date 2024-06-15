import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {
    
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + "has entered the chat.");



        } catch (IOException e) {
            // TODO: handle exception
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    // @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                // important concept, runs on separate thread so it does not block
                messageFromClient = bufferedReader.readLine();
                broadcastEverything(messageFromClient);
            } catch (IOException e) {
                // TODO: handle exception
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();

                }
            } catch (Exception e) {
                // TODO: handle exception
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
}
