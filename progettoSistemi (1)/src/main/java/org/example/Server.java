package org.example;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {

	public static void main (String args []) {
		ArrayList<Socket> clientConnessi = new ArrayList<>();

		if (args.length < 2) {
			System.err.println("Inserisci java Server.java <path> <port>");
			return;
		}
		String pathFile = args[0];

		int port = Integer.parseInt(args[1]);

		try {
			ServerSocket serverSocket = new ServerSocket(port);

			Gson gson= new Gson();
			File fileJSON = createFileJSON(pathFile);
			JsonReader reader = new JsonReader(new FileReader(fileJSON));
			Directory directory = gson.fromJson(reader, Directory.class);

			Scanner userInput = new Scanner(System.in);


			Thread serverHandlerThread= new Thread(new ServerHandler(serverSocket, directory, userInput, fileJSON));
			serverHandlerThread.start();
			System.out.println("Server Attivo");

			while(true) {
				Socket s = serverSocket.accept();
				clientConnessi.add(s);
				System.out.println("Connesso");
				Thread clientHandlerThread = new Thread( new ClientHandler(s, directory, fileJSON));
				clientHandlerThread.start();
			}

		} catch (IOException e) {
			try {
				for(Socket t: clientConnessi) {
					t.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	private static File createFileJSON(String pathFile) {
		File fileJSON;
		Scanner inputUser = new Scanner(System.in);
		String fileName = null;
		boolean invalidName = true;

		while (invalidName == true){
			System.out.println("Inserisci il nome del file json");
			fileName = inputUser.next();
			if (isInvalidFileName(fileName) == false){
				break;
			}
			System.out.println("Caratteri non accettati, ritenta l'inserimento");
		}

		if(pathFile.equals(".")) 
			fileJSON = new File(fileName+".txt");
		else 
			fileJSON = new File(pathFile,fileName+".txt");

		if(!fileJSON.exists()) {
			
			System.out.println(fileName + " è stato creato, perchè inesistente");
			try {
				fileJSON.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				FileWriter fileWriter = new FileWriter(fileJSON,true);
				BufferedWriter buffWriter = new BufferedWriter(fileWriter);
				buffWriter.write("{\r\n"
						+ "  \"directory\": []\r\n"
						+ "}");

				buffWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else {
			System.out.println(fileName +" è stato trovato");
			return fileJSON;
		}

		return fileJSON;
	}

	public static boolean isInvalidFileName(String fileName) {
		Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^]");
		Matcher matcher = pattern.matcher(fileName);
		return matcher.find();
	}
}