package zadanie;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void sendMessage(){
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        if (hasADate(msgFromGroupChat)){
                            String end = parseDate(msgFromGroupChat);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime from = LocalDateTime.parse(end, formatter);
                            LocalDateTime to = LocalDateTime.now();
                            Duration duration = Duration.between(to, from);
                            if (duration.isNegative())
                                throw new invalidDateException("Earlier date than it is now");
                            else{
                                //System.out.println(duration.getSeconds());
                                Thread.sleep(duration.getSeconds()*1000);
                            }
                        }else
                            throw new invalidDateException("Input doesn't have a date");
                        System.out.println(msgFromGroupChat);
                    }catch (IOException | InterruptedException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    } catch (invalidDateException e) {
                        System.err.print(e);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
    public String parseDate(String msg){
        String[] arr = msg.split(" ");
        String end = "";
        for (int i = arr.length-2;i< arr.length;i++){
            end = end.concat(arr[i]);
            if (i!=arr.length-1){
                end = end.concat(" ");
            }
        }
        return end;
    }
    public boolean hasADate(String msg){
        String[] arr = msg.split(" ");
        boolean flag = false;
        if (arr.length<2)
            return flag;
        for (String str: arr) {
            if (str.matches(".*\\d+.*")) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}

