/* File name: <@TODO>		Author:
 * 
 * Description: <@TODO>
 * Terminology: <@TODO>
 * 
 */
package com.mlwarmonitor.app;
import java.io.*;
import com.kanishka.virustotal.dto.*;
import com.kanishka.virustotal.exception.*;
import com.kanishka.virustotalv2.*;
public class VirusTotal{
	// API key identifies the user of the VirusTotal Community. It is a sensitive data!
	private static final String virusTotalAPIkey = "896c74ff806d3f4cc55c68d742bfd6ed2e787b986e73d52230fe676cee820106";
	// Sleeptime beetween scannings
	private static final int CHECK_INTERVAL = 4000;
	
	/**
	 * Prints out the amount of antimalware programs
	 * that have classified the parameter file as a
	 * malicious one.
	 * @param filePath path to the file on system
	 */
	public static void detectionRateOnVT(final String filePath){
		// Extract the file name from the file path
		String filePathArray [] = filePath.split("\\\\");
		final String fileName = filePathArray[filePathArray.length - 1];
		// Trigger new thread which will submit the file on the VirusTotal
		Thread t = new Thread("VirusTotal - " + fileName){
			public void run(){
				try {
					VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(virusTotalAPIkey);
					VirustotalPublicV2 virusTotalobj = new VirustotalPublicV2Impl();
					ScanInfo scanInformation = virusTotalobj.scanFile(new File(filePath));

					String md5 = scanInformation.getMd5();
					FileScanReport report = virusTotalobj.getScanReport(md5);
					/* Wait till the report is ready. The report is ready when the file can be found by MD5 */
					while(report.getMd5() == null)
					{
						try{
							Thread.sleep(CHECK_INTERVAL);
						}catch(InterruptedException e){
							e.printStackTrace();
						}
						report = virusTotalobj.getScanReport(md5);
					}
					// Print out the classification of the malware
					int positives = report.getPositives();
					if(positives > 0)
						System.out.println("\"" + fileName + "\" has been classified as a MALICIOUS file by " + positives + " VirusTotal antimalware programs. Find the report on VirusTotal by MD5: " + md5);
					else
						System.out.println("\"" + fileName + "\" has been classified as a NOT MALICIOUS file by VirusTotal." );
				}catch(APIKeyNotFoundException ex){
					System.err.println("API key not found: " + ex.getMessage());
				}catch(UnsupportedEncodingException ex){
					System.err.println("Unsupported encoding format: " + ex.getMessage());
				}catch(UnauthorizedAccessException ex){
					System.err.println("Unauthorized Accesse: " + ex.getMessage());
				}catch(Exception ex){
					System.err.println("Problems during check on VirusTotal: " + ex.getMessage());
				}
			}};
			t.start();
	}
}
