package com.logger;

import java.util.Random;
import java.lang.Math;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Logger {

	private static long rand = Math.abs(new Random().nextLong());
	private static final String baseDir = new File("").getAbsolutePath();
	private static final String FILENAME = baseDir + "/logs/logfile."+String.valueOf(rand)+".txt";
	
	private static final Logger instance = new Logger();
	
	private Logger(){}
	
	public static Logger getInstance(){
		return instance;
	}

	public void write(String content) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME, true))) {
            bw.write(content);
			System.out.println();

			// no need to close it.
			//bw.close();

			//System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	
	public void printFileName(){
		System.out.println("FILENAME: "+FILENAME);
	}

}
