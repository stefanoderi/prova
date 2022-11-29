package org.example;

import java.util.ArrayList;


public class Directory {
	
	private ArrayList<FileObj> directory;
	
	public Directory() {
		this.directory = new ArrayList<FileObj>();
	}
	
	public int getSize() {
			if (directory.isEmpty()) {
				return 0;
			}
			int size = directory.size();
			return size;
	}
	
	public int getReadSession() {
		int readers = 0;
		for (FileObj file : directory) {
			readers += file.getReadBuffer();
		}
		return readers;
	}
	
	public int getWriteSession() {
		int writers = 0;
		for (FileObj file : directory) {
			if (file.getWriteBuffer() == true)
			writers++;
		}
		return writers;
	}

	public void clearSessions(){
		for (FileObj file : directory) {
			if (file.getWriteBuffer() == true || file.getReadBuffer() != 0){
				file.closeWriteSession();
				file.setReadBuffer(0);
			}
		}
	}
	
	public String getList() {
		String tmp="";
		for(FileObj f : directory) {
			tmp= tmp + f.toString() + " -- ";
		}
		if(!tmp.equals(""))
			return tmp;
		else
			return "La directory è vuota";
	}
	
	public String createFile(String name) {
		boolean check = false;
		for (FileObj file : directory) {
			if (file.getName().equals(name)) {
				check = true;
			} 
		}
		if (check == false) {
			FileObj file = new FileObj(name);
			directory.add(file);
			return "File creato";
		} else
		return "File esistente";
	}
	
	public synchronized void deleteFile(String name) throws InterruptedException {
		FileObj fileProv = null;
		for( FileObj file : directory) {
			if(file.getName().equals(name)) {
				while(file.getWriteBuffer() == true || file.getReadBuffer() > 0) {
					wait();
				}
				fileProv = file;
			}
		}
		directory.remove(fileProv);
		notifyAll();
	}
	
	public synchronized FileObj getFileForReading(String name) throws InterruptedException {
		FileObj fileReading = null;
		for(FileObj file : directory) {
			if(file.getName().equals(name)) {
				while(file.getWriteBuffer()==true) {
					wait();
				}
				fileReading = file;
				notifyAll();
			}			
		}			
		return fileReading;		
	}
	
	public synchronized FileObj getFileForEditing(String name) throws InterruptedException {
		FileObj fileWriting = null;
		for(FileObj file : directory) {
			if(file.getName().equals(name)) {
				while(file.getWriteBuffer()==true || file.getReadBuffer()>0) {
					wait();
				}
				fileWriting = file;
				notifyAll();
				}
			}		
		return fileWriting;
		}
}
