package ch.ntb.mcdp.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Redirect {
	
	public static void redirect(){
		try {
			System.setOut(new PrintStream(new FileOutputStream("JavaOut.txt")));
			System.setErr(new PrintStream(new FileOutputStream("JavaErr.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
