package org.example;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServerHandler implements Runnable{
	
	private ServerSocket s;
    private Directory directory; 
    private Scanner scan;
    private File fileJSON;
    
    public ServerHandler(ServerSocket s,Directory directory,Scanner scan,File fileJSON) {
    	this.s=s;
        this.directory = directory;
        this.scan=scan;
        this.fileJSON = fileJSON;
    } 
    
    
	@Override
	public void run() {

			while(true) {

				String request = scan.nextLine();

				if(request.equals("quit")) {
					
					Gson gson = new GsonBuilder().setPrettyPrinting().create(); //
					try {
						directory.clearSessions();
						FileWriter fileJ = new FileWriter(fileJSON);
						gson.toJson(directory,fileJ);
						fileJ.flush();
						fileJ.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
						try {
							s.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					System.out.println("Server chiuso");
					scan.close();
            		break;
					
            	} else if(request.equals("info")) {
					try {
						System.out.println("File gestiti:" + directory.getSize());
						System.out.println("Sessioni lettura:" + directory.getReadSession());
						System.out.println("Sessioni Scrittura:" + directory.getWriteSession());
					} catch (NullPointerException e){
						System.out.println("Directory non inizializzata");
					}
            	} else {
                    System.out.println("Ripetere comando, accettati: quit, info");
                }
			}
			
		
	}

}