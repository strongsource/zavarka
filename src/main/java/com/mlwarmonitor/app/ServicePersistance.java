/* File name: <@TODO>		Author: 
 * 
 * Description: <@TODO>
 > Attention: To add new autostart service through the command line type
 * in shell the following command:
 * "sc create NEW_AUTOSTART_SERVICE binPath= PROGRAM.EXE start= auto"
 * 
 * Terminology: <@TODO>
 > 		Autostart service is a service which starts automatically on
 * 		system start or reboot. Such services have 
 * 		START_TYPE == 2 (AUTO_START).
 */
package com.mlwarmonitor.app;
import java.io.*;
import java.util.*;
public class ServicePersistance {
		// contains names of autostart services
		private ArrayList<String> autostartServices;
		// Sleeptime beetween scannings
		private static final int SLEEPTIME = 3000;
		
		/**		
		 * Default constructor of the class
		 */
		public ServicePersistance(){}
		
		/**
		 * Starts a new thread which monitors wheather a new
		 * autostart service has been added to system. The file
		 * thich is started by a new autostart service will be
		 * submitted to VirusTotal.
		 * @return the thread, which monitors autostart services.
		 */
		public Thread scan(){
			Thread t = new Thread("Service Persistance"){
				public void run(){
					ArrayList<String> allServices = getAllServiceNames();
					autostartServices = getAutostartServicesOfList(allServices);
					System.out.println("The list with autostart services has been initialized.");
					int amountOfServices = allServices.size();
					while(true){
						try{
							Thread.sleep(SLEEPTIME);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
						ArrayList<String> newAllServices = getAllServiceNames();
						// Check if there is a new autostart service.
						if(amountOfServices < newAllServices.size()){
							for(String s : newAllServices){
								if(!allServices.contains(s) && isAutostartService(s)){
									System.out.println("Caution! A new autostart service \"" + s + "\" has been registered.");
									isServiceMalicious(s);
									autostartServices.add(s);
								}
							}
							allServices = newAllServices;
							amountOfServices = allServices.size();
						}
					}
				}
			};
			t.start();
			return t;
		}

		/**
		 * The parameter autostart service will be allocated on
		 * the system and the file which is started by this process.
		 * This file will be submitted on VirusTotal.
		 * @param serviceName - name of an autostart service.
		 */
		private void isServiceMalicious(String serviceName){
			String[] command = {"cmd", "/C", "sc qc " + serviceName};
	        ProcessBuilder probuilder = new ProcessBuilder(command);
	        
			Process process = null;
			try{
				process = probuilder.start();
		
		        InputStream is = process.getInputStream();		// Read output
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(isr);
		        String line= br.readLine();
		        // find the file, which is started by the service
		        while ((line = br.readLine()) != null && !line.trim().startsWith("BINARY_PATH"))
		        	continue;
		        String filePath = ScannerMain.getFilePath(line).get(0);
		        System.out.println("\"" + serviceName + "\" is supposed to execute \"" + filePath + "\" on every system start.");
				VirusTotal.detectionRateOnVT(filePath);
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		/**
		 * Searches for the parameter service in windows and
		 * gives information wheather this service is an autostart
		 * service.
		 * @param service - name of the service.
		 * @return true, if the parameter an autostart service.
		 */
		private boolean isAutostartService(String service){
			String[] command = {"cmd", "/C", "sc qc " + service + 
								" | find /i \"AUTO_START\""};
	        ProcessBuilder probuilder = new ProcessBuilder(command);
	        
			Process process = null;
			try{
				process = probuilder.start();
		
		        InputStream is = process.getInputStream();		//Read output
		        InputStreamReader isr = new InputStreamReader(is);
		        BufferedReader br = new BufferedReader(isr);
		        String line= br.readLine();

				if((line != null))
					return true;
			}catch(IOException ex){
				ex.printStackTrace();
			}
			//waitForExitValue(process);
			return false;
		}
		
		/**
		 * @param listOfServices - List that contains service names.
		 * @return A new ArrayList containing only autostart services.
		 */
		private ArrayList<String> getAutostartServicesOfList(ArrayList<String> listOfServices){
			ArrayList<String> autostartServices = new ArrayList<String>();
			for(String s : listOfServices)
				if(isAutostartService(s))
					autostartServices.add(s);
			return autostartServices;
		}
		
		/**
		 * Fills the parameter list with names of autostart services.
		 * The following shell command is executed: 
		 > cmd /C sc queryex type= all state= all | find /i "SERVICE_NAME"
		 > "cmd" starts the command line process with "\C" parameter, which
		 * means that the command line process terminates after the execution.
		 > "sc queryex type= all state= all" lists all registered services on
		 * the system, which are piped to the command "find /i "SERVICE_NAME""
		 *  and as consequence only service names will be printed out. 
		 */
		private ArrayList<String> getAllServiceNames(){
			ArrayList<String> listOfServices = new ArrayList<String>();
			String[] command = {"cmd","/C","sc queryex type= all state= all" +
								" | find /i \"SERVICE_NAME\""};
			// Process to be executed in shell
			ProcessBuilder probuilder = new ProcessBuilder(command);

			//Set up your work directory
			//probuilder.directory(new File("c:\\"));

			Process process = null;
			try{
				process = probuilder.start();

				//Read output
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);	
				BufferedReader br = new BufferedReader(isr);
				String line;
				// Remove the preceding string "SERVICE_NAME: "
				while ((line = br.readLine()) != null){
					line = line.substring("SERVICE_NAME: ".length());
					listOfServices.add(line);
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
			return listOfServices;
		}
		
		/**
		 * Returns the exit value of the process, which may indicate
		 * whether the process has crashed. If the process crashes,
		 * the exit value is not 0.
		 * @param p - reference to the process.
		 */
//		private void waitForExitValue(Process p){
//			try {
//	            int exitValue = p.waitFor();
//	        } catch (InterruptedException e) {
//	            e.printStackTrace();
//	        }
//		}
}
