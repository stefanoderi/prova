package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class FileObj {
	
	private String name;
	private boolean writeBuffer; 
	private int readBuffer;
	private String lastChange;
	private ArrayList <String> test;

	public FileObj(String name) {
		this.name = name;
		this.test = new ArrayList<String>();
		this.lastChange=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now());
		this.writeBuffer = false;		
		this.readBuffer = 0;
	}
	
	// informazioni file e anche modfiche 
	
	public synchronized void setName(String name) throws InterruptedException {
		while(readBuffer>0 || writeBuffer == true) {
			wait();
		}
		this.name=name;
		this.updateDate();
	}
	
	public String getName() {
		return name;
	}
	
	public void updateDate() {
		this.lastChange=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now());
	}
	
	public String getLastChange() {
		return lastChange;
	}
	
	public void setTest(ArrayList<String> test) {
		this.test = test;
	}
	
	public String getTest() {
		String testContent = "";
		for(int i = 0; i < test.size(); i++) {
			testContent = testContent + test.get(i) + " ";
		}
		return testContent;
	}
	
	public String backspace() {
		if (test.isEmpty()) {
			return "Nessun contenuto trovato";
		} else {
			test.remove(test.size() - 1);
		}
		return "Ultima riga Eliminata";
	}

	public void addTest(String newLine){
		test.add(newLine);
	}
	
	public String toString() {
		return "Nome: " + name + " Ultimo aggiornamento: "+ lastChange +" Utenti in lettura: "+ readBuffer ;
	}

		public synchronized void addReadBuffer() {
			this.readBuffer= this.readBuffer+1;
			notifyAll();
		}
		
		public synchronized void removeReadBuffer() {
			this.readBuffer= this.readBuffer-1;		
			notifyAll();
		}

		public void setReadBuffer(int readBuffer) {
		    this.readBuffer = readBuffer;
	    }
		
		public int getReadBuffer() {
			return readBuffer;
		}
		
		
		// writing sessions commands
		
		public boolean getWriteBuffer() {
			return writeBuffer;
		}
		
		public synchronized void openWriteSession() throws InterruptedException {
			while(writeBuffer==false && readBuffer>0) {
				wait();
			}
			this.writeBuffer=true;
			notifyAll();
		}
		
		public synchronized void closeWriteSession() {
			this.updateDate();
			this.writeBuffer=false;
			notifyAll();
		}
}
