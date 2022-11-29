package org.example;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {

    public static void main (String args[]) {

        if (args.length < 2) {
            System.err.println("Inserisci java Client.java <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {

            Socket s = new Socket(host, port);
            System.out.println("Connesso");

            Scanner fromServer = new Scanner(s.getInputStream());
            PrintWriter toServer = new PrintWriter(s.getOutputStream(), true);
            Scanner userInput = new Scanner(System.in);


            while (true) {
                String request = userInput.nextLine();
                toServer.println(request);
                if (request.equals("quit")) {
                    break;
                }
                String response = fromServer.nextLine();
                System.out.println(response);

            }
            s.close();
            userInput.close();
            System.out.println("Chiuso");

        } catch (IOException e) {
            System.err.println("Error during an I/O operation:");
        } catch (NoSuchElementException e) {
        	System.err.println("Chiusura inaspettata del Server");
        }

    }
}
