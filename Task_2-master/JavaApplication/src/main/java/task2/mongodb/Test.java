package task2.mongodb;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Test {
	
	private class printOutput extends Thread {
		InputStream is = null;
 
		printOutput(InputStream is, String type) {
			this.is = is;
		}
 
		public void run() {
			String s = null;
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				while ((s = br.readLine()) != null) {
					System.out.println(s);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public printOutput getStreamWrapper(InputStream is, String type) {
			return new printOutput(is, type);
	}
	
	public static void main(String[] args) {
		 
		Runtime rt = Runtime.getRuntime();
		Test rte = new Test();
		printOutput errorReported, outputMessage;
		
		try {
			Process proc = rt.exec("/usr/local/bin/python3 /Users/andreadidonato/Desktop/GitHub/Task_2/mongodb_2/scraping/scrap.py");
			errorReported = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
			outputMessage = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
			errorReported.start();
			outputMessage.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
