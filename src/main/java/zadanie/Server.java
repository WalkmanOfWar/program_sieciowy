package zadanie;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket; //czeka na nadchodzące połączenia i tworzy socketObject żeby się z nimi komunikować

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while (!serverSocket.isClosed()){

                Socket socket = serverSocket.accept(); //czeka w nieskonczoność aż jakiś klient się połączy
                System.out.println("New client has connected");
                ClientHandler clientHandler = new ClientHandler(socket); // każdy objekt tej klasy będzie odpowiedzialny za komunikację

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){

        }
    }
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
