package server;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class test {
     
    public static void main(String[] args) {
    	ProcessBuilder processBuilder = new ProcessBuilder();
    	try {
			Runtime.getRuntime().exec("cmd /c start \"\" hello.bat");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
}