/* File name: <@TODO>		Author: Maksym Levchenko
 * 
 * Description: <@TODO>
 >  Attention: To add new autorun program through the command line type
 * in shell the following command:
 * "REG ADD HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\Run\ /v NEW_REGISTRY_KEY"
 *
 *
 * Terminology: <@TODO>
 > 
 */
package com.mlwarmonitor.app;
import java.io.*;
import java.util.*;
public class RegistryPersistence{
	// Critical keys in Windows Registry.
	private ArrayList<String> registryValues [];
	// 
	private String keysString [];
	// Sleeptime beetween scannings
	private static final int SLEEPTIME = 3000;
	
	/**
	 * Default constructor of this class.
	 */
	@SuppressWarnings("unchecked")
	public RegistryPersistence(){
		registryValues = (ArrayList<String>[])new ArrayList [7];	// Array of ArrayLists.
		keysString = new String [7];
		keysString[0] = "HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
		keysString[1] = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
		keysString[2] = "HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\RunOnce";
		keysString[3] = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\RunOnce";
		keysString[4] = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Windows";
		keysString[5] = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon";
		keysString[6] = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Svchost";
		// @TODO another registry keys ---> AppInit
	}
	
	/**
	 * @TODO
	 */
	public Thread scan(){
		Thread t = new Thread("Registry Persistance"){
			public void run(){
				if(registryValues.length != keysString.length)
					throw new RuntimeException("registryValues.length != keysString.length");
				for(int i = 0; i < registryValues.length;i++)
					registryValues[i] = getInitializedKeyList(keysString[i]);
				System.out.println("The list with registry autoruns programs has been initialized.");
				while(true){
					try{
						Thread.sleep(SLEEPTIME);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					// Iterate over all registry keys list
					for(int i = 0; i < registryValues.length;i++){
						String regKey = keysString[i];
						ArrayList<String> values = getInitializedKeyList(regKey);
						for(String s : values)
							if(!registryValues[i].contains(s)){
								registryValues[i].add(s);
								System.out.println("Caution! A new autorun entry \"" + s.trim() + "\" has been added to the " + regKey + ".");
								for(String str : ScannerMain.getFilePath(s.trim()))
									VirusTotal.detectionRateOnVT(str);
							}
					}
				}
			}
		};
		t.start();
		return t;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private ArrayList<String> getInitializedKeyList(String key){
		ArrayList<String> listOfValues = new ArrayList<String>();
		String[] command = {"cmd","/C","reg query " + key};
		// Process to be executed in shell
		ProcessBuilder probuilder = new ProcessBuilder(command);

		Process process = null;
		try{
			process = probuilder.start();

			//Read output
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null)
				listOfValues.add(line);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		listOfValues.remove(key);	// Removes registry path from the output
		return listOfValues;
	}	
}






