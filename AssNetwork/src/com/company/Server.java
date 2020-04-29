package com.company;

import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {



    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
//        new File("www").mkdir();
        try {
            //Server is listening on port 4444
            serverSocket = new ServerSocket(4444);
            System.out.println("Server is listening...");

            //((((((( For the Assignment1 you should read and write your messages according to following rules:
            //Messages are supposed to be finished by '\n' in the project
            //msg = in.readLine();
            //out.write((String.valueOf(obj)+"\n").getBytes("UTF-8"));      )))))))

            while (true) {
                //Server waits for a new connection
                Socket socket = serverSocket.accept();
                // Java creates new socket object for each connection.

                System.out.println("Client Connected...");


                // A new thread is created per client
                Thread client = new Thread(new ClientThread(socket));
                // It starts running the thread by calling run() method
                client.start();

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null)
                serverSocket.close();
        }
    }

}
