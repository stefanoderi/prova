package org.example;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class ClientHandler implements Runnable{
	
	private Socket s;
	private Directory directory;
	private boolean readSession;
    private boolean writeSession;
    private FileObj file;
    private File fileJSON;
    //private String request;

	private String notifyMessage;


    public ClientHandler(Socket s, Directory directory, File fileJSON) {
        this.s = s;
        this.directory = directory;
        this.fileJSON = fileJSON;
    } 

	@Override
	public void run() {		
		try {		
			Scanner fromClient = new Scanner(s.getInputStream());
			PrintWriter toClient = new PrintWriter(s.getOutputStream(), true);
			readSession=false;
            writeSession=false;
			file = null;

			while(true) {

				String[] requestCommands;
				String request = fromClient.nextLine();
				requestCommands = request.split(" ",3);

				
				if (requestCommands[0].equals("list")) {
					notifyMessage = directory.getList();
					toClient.println(notifyMessage);
				}

				else if (requestCommands[0].equals("create")) {

					notifyMessage = directory.createFile(requestCommands[1]);
					toClient.println(notifyMessage);
				}
				
				else if (requestCommands[0].equals("read")) {

					file = directory.getFileForReading(requestCommands[1]);
					if (file == null) {
						toClient.println("il file è inesistente");
					} else {
						file.addReadBuffer();
						readSession = true;
						String testo = file.getTest();
						if ( testo == null){
							toClient.println("il file non contiene testo");
						} else {
							toClient.println(testo);
							while (true) {
								request = fromClient.nextLine();
								if (request.equals(":close")) {
									readSession = false;
									file.removeReadBuffer();
									break;
								}
							}
							toClient.println("Sessione lettura conclusa");
						}

					}
				}

				
				else if (requestCommands[0].equals("edit")) {
					file = directory.getFileForEditing(requestCommands[1]);
					if (file == null) {
						toClient.println("file inesistente");
					} else {
						file.openWriteSession();
						writeSession = true;
						String testo = file.getTest();
						if (testo == null) {
							testo = "";
						}

						toClient.println("Testo:" + testo);

						while (true) {
							String command = fromClient.nextLine();
							if (command.equalsIgnoreCase(":backspace")) {
								toClient.println(file.backspace());
							}
							else if (command.equalsIgnoreCase(":close")) {
								file.closeWriteSession();
								writeSession = false;
								break;
							} else if(command.equalsIgnoreCase("")) {
							}
							else {
								file.addTest(command);
								toClient.println("Modifica accettata  -- Comandi disponibili :backspace :close");
							}
							toClient.flush();
						}
						toClient.println("Sessione scrittura conclusa");
					}
				}
				
				else if (requestCommands[0].equals("rename")) {
					String name = requestCommands[1];
					String newName = requestCommands[2];

					file = directory.getFileForEditing(name);
					file.openWriteSession();
					writeSession = true;
					file.setName(newName);
					file.closeWriteSession();
					writeSession = false;

					toClient.println("Nome del file cambiato");
				}
				
				else if (requestCommands[0].equals("delete")) {

					directory.deleteFile(requestCommands[1]);
					toClient.println("File Eliminato");

				}
				else if (requestCommands[0].equals("quit")){
					fromClient.close();
					break;
					}
				else {
					toClient.println("Comando non riconosciuto");
				}
			}
			
			s.close();
			System.out.println("Client chiuso");
			
			} catch (InterruptedException e) {
	        	return;
	        } catch (IOException e) {
	            System.err.println("Error during I/O operation:");
	            e.printStackTrace();
	        } catch (NoSuchElementException e) {
	        	try {
					s.close();
				} catch (IOException unexpectedShutdown) {
					unexpectedShutdown.printStackTrace();
				}

	        	if(readSession==true) {
	        		readSession=false;
					file.removeReadBuffer();
	        	}
				if(writeSession==true) {
					writeSession=false;
					file.closeWriteSession();
				}

	        	//salvataggio dati sul file 
	        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
				try {
					FileWriter fileJ = new FileWriter(fileJSON);
					gson.toJson(directory,fileJ);
					fileJ.flush();
	            	fileJ.close();
				} catch (IOException e1) {

					e.printStackTrace();
				}
	        					
	        	System.err.println("Chiusura inaspettata del client");
	        }
	        
	    }
}
		

