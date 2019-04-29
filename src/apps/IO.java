package apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IO {

	Scanner scanner;
	IO(){
		try {
		  this.scanner = new Scanner(new File("src/apps/test.jpy"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Scanner getScanner() {
		return scanner;
	}	
}
