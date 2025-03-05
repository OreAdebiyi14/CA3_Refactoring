/*
 * 
 * This class is for accessing, creating and modifying records in a file
 * 
 * */

 //theres multiple instances of randomfile that might open files incorrectly
 //need to use a singleton pattern

 import java.io.IOException;
 import java.io.RandomAccessFile;
 
 public class RandomFile {
	 private static RandomFile instance;
	 private RandomAccessFile file;
 
	 private RandomFile() {}
 
	 public static RandomFile getInstance() {
		 if (instance == null) {
			 instance = new RandomFile();
		 }
		 return instance;
	 }
 
	 public void openFile(String fileName) throws IOException {
		 file = new RandomAccessFile(fileName, "rw");
	 }
 
	 public void closeFile() throws IOException {
		 if (file != null) file.close();
	 }
 }
 