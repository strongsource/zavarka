/* File name: <@TODO>		Author:
 * 
 * Description: <@TODO>
 * This program can be only runned on Windows OS(XP and later). 
 * 
 * Terminology: <@TODO>
 * 
 * @TODOs
 * - Queue for VirusTotal?
 * - Mutants?
 * - Learn malicious IP, Domains, Hosts and block them
 */
package com.mlwarmonitor.app;
import java.util.*;
import java.util.regex.*;
public class ScannerMain{
	
	/**
	 * Constructor
	 * @param threadName
	 * @param remoteService
	 */
	public ScannerMain(){
	}
	
	/**
	 * @param s - a string which contains one or more file pathes
	 * in form like "C:\ProgramData\Mozilla\m.exe" or "D:\file.dll"
	 * or similiar. File pathes like this "\\file.txt" would not 
	 * be recognized. The file path has to be absolute.
	 * @return an ArrayList which contains one or more file pathes founded
	 * in the parameter.
	 */
	public static ArrayList<String> getFilePath(String s){
		ArrayList<String> paths = new ArrayList<String>();
		Pattern pathPattern = Pattern.compile("[a-zA-Z]:?(\\\\[a-zA-Z0-9 &!§$%()=`ґІі{}\\[\\]._-]+)+\\\\?");
		Matcher matcher = pathPattern.matcher(s);
		while(matcher.find())
		    paths.add(matcher.group(0));
		return paths.size() == 0 ? null : paths;
	}

	/********************************************
	 ***************** MAIN *********************
	 ********************************************/
	public static void main(String[] args){
		try {
			//new ServicePersistance().scan();
			//new RegistryPersistence().scan();
	        //new CriticalFolderWatcher(CriticalFolderWatcher.getTempFolderPath(), true).scan(); // this thread
			SimpleScanner.scan("D:\\VIRUS\\cybertracker\\cybertracker.malwarehunterteam.com","D:\\VIRUS\\signature2.txt");
			//SimpleScanner.scan("D:\\vir\\","D:\\VIRUS\\signature2.txt");
		} catch (Exception e) {
			System.out.println("Something bad happened: " + e.getMessage());
		}
	}
}


