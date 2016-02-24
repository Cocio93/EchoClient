package echoclient;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoClient implements Runnable {

    private static String username;
    private static String hostname = "jonathanh.cloudapp.net";
    private static int port = 9090;
    private Socket socket;
    private static PrintWriter networkOut;
    private static Scanner networkIn;
    private static BufferedReader userIn;

    public EchoClient(String host, int port) {
        this.hostname = host;
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            hostname = args[0];
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        }
        EchoClient client = new EchoClient(hostname, port);
        client.run();
    }

    @Override
    public void run() { 
        try {
            socket = new Socket(hostname, port);
            userIn = new BufferedReader(new InputStreamReader(System.in));
            networkIn = new Scanner(socket.getInputStream());
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected succesfully to " + hostname + ":" + port);
            System.out.print("Please type you name, and finish with 'ENTER': ");
            username = userIn.readLine();
            sendName();
        } catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        do {
            String line;
            try {
                if ((line = userIn.readLine()) != null) {
                    sendMessage(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (networkIn.hasNext()) {
                getMessage();
            }
        } while (true);
    }

    private void sendName() {
        System.out.println("Hello " + username);
        networkOut.println("USER#" + username);
    }

    private void sendMessage(String message) {
        if (message.equalsIgnoreCase("exit")) {
            disconnect();
        }
        String send = "SEND#*#" + message;
        networkOut.println(send);
    }

    private void getMessage() {
        String[] cmd = networkIn.nextLine().split("#");
        if (cmd[0].equals("MESSAGE")) {
            String msg = cmd[1] + " - " + cmd[2];
            System.out.println(msg);
        }
        if (cmd[0].equals("USERS")) {
            String users = cmd[1];
            System.out.println("Users online: " + users);
        }
    }

    private void disconnect() {
        networkOut.println("LOGOUT#");
    }
}
