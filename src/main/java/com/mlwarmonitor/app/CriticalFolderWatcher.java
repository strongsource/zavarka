/* File name: <@TODO>		Author: Maksym Levchenko
 * 
 * Description: <@TODO>
 * Attention: This code is mostly taken from 
 * https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
 * and changed due to requirements of the author.
 >
 * Terminology: <@TODO>
 * 
 * @TODO another folders
 > 
 */
package com.mlwarmonitor.app;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class CriticalFolderWatcher{
	
	private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
 
    /**
     * Constructor.
     * Creates a WatchService and registers the given directory.
     */
    CriticalFolderWatcher(String pathString, boolean recursive) throws IOException{
    	Path path = Paths.get(pathString); // change type from String to Path
    	this.watcher = FileSystems.getDefault().newWatchService();
    	this.keys = new HashMap<WatchKey,Path>();
    	this.recursive = recursive;
    	if (recursive)
    		registerAll(path);
    	else
    		register(path);
		System.out.format("Folder scanning begun \"%s\" \n", path);
    }
    
    /**
	 * @return a path to the Temp folder on current system.
	 */
	public static String getTempFolderPath(){
		//create a temp file
		File temp = null;
		try {
			temp = File.createTempFile("temp2delete", ".tmp");
		} catch (IOException e){
			System.err.println("File could not be created in the Temp folder: " + e.getMessage());
		}
		//Get tempropary file path
		String absolutePath = temp.getAbsolutePath();
		String tempFilePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
		temp.delete();
		return tempFilePath;
	}
    
    /**
     * @param event - @TODO
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> castEvent(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
 
    /**
     * Register the given directory with the WatchService
     * @param dir - path to the folder needed to be registered.
     */
    private void register(Path dir) throws IOException {
    	// There is also another event constants ENTRY_MODIFY, ENTRY_DELETE
        WatchKey key = dir.register(watcher, ENTRY_CREATE);
        keys.put(key, dir);
    }
 
    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
 
    /**
     * Process all events for keys queued to the watcher
     */
    @SuppressWarnings("rawtypes")
	void scan() {
        while(true){
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!");
                continue;
            }
            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW)
                    continue;
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = castEvent(event);
                Path name = ev.context();
                Path path = dir.resolve(name);
                // print out event
                if(!path.toFile().isDirectory()){
                	System.out.format("Processing an event \"%s : %s\" \n", event.kind().name(), path);
                	System.out.format("Submitting \"%s\" on VirusTotal... \n", path);
                	VirusTotal.detectionRateOnVT(path.toString());
                }
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(path, NOFOLLOW_LINKS)) {
                            registerAll(path);
                        }
                    } catch (IOException x) {
                        // ignore to keep it readbale
                    }
                }
            }
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid){
            	keys.remove(key);
            	// all directories are inaccessible
            	if (keys.isEmpty())
            		break;
            }
        }
    }
}
