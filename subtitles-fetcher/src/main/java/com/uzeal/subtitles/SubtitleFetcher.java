package com.uzeal.subtitles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.xmlrpc.client.XmlRpcHttpTransportException;

import com.uzeal.subtitles.opensub.OpenSubtitleAPI;

public class SubtitleFetcher {
	private static final Set<String> subtitleExtensions = Stream.of(".srt", ".sub").collect(Collectors.toSet());
	private static final Set<String> videoExtenstions = Stream.of(".mkv",".mp4",".avi",".mov",".m4v").collect(Collectors.toSet());
	private static final Set<String> skipWords = Stream.of("anime").collect(Collectors.toSet());
	private static final String failedFile = "failed.txt";
	private static long timeBetween = 2000;
	private static long timeToAdd = 500;
	private static long maxWait = 15000;
	private static int maxDownloaded = 200;
	private static int downloaded = 0;
	private static Set<String> failed;
	
	public static void main(String[] args) {
		try {
			//Initializer.initDB();
			File currentDir = new File(".");
			System.out.println("Reading file from: "+currentDir.getAbsolutePath()+"main-dirs.properties");
			Properties sources = null;
			try(FileInputStream fis = new FileInputStream("main-dirs.properties")) {
				sources = new Properties();
				sources.load(fis);
			}
			
			failed = new HashSet<String>();
			readFailed();
			
			if(sources != null && !sources.isEmpty() && args.length > 1) {
				OpenSubtitleAPI api = new OpenSubtitleAPI();
				try {
					api.login(args[0], args[1]);
					sources.forEach((sourceName,path) -> processSource(api, (String)sourceName, (String)path));
				} catch(RuntimeException e) {
					throw new Exception(e);
				} finally {
					api.logOut();
				}
			} else {
				System.out.println("Empty args or empty main-dirs.properties");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			writeFailed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void processSource(OpenSubtitleAPI api, String sourceName, String path) {
		System.out.println("Processing Source: "+sourceName+" with path: "+path);
		try {
			Files.walk(new File(path).toPath())
				.map(filePath -> filePath.toFile())
				.filter(file -> !failed.contains(file.getAbsolutePath()))
				.filter(file -> !isSkipped(file))
				.filter(file -> !file.isDirectory() && isVideo(file))
				.filter(video -> !hasSubtitles(video))
				.forEach(video -> waitAndDownload(api, video));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	private static void waitAndDownload(OpenSubtitleAPI api, File video) {
		try {
			Thread.sleep(timeBetween);
			if(!api.downloadSub(video)) {
				System.out.println("Didnt download skipping next time");
				failed.add(video.getAbsolutePath());
			} else {
				downloaded++;
				if(downloaded >= maxDownloaded) {
					throw new RuntimeException("Max Downloaded");
				}
			}
		} catch(Exception e) {
			if(e instanceof XmlRpcHttpTransportException || e.getCause() instanceof XmlRpcHttpTransportException) {
				System.out.println("Error connecting: "+e.getMessage());
				int attempts = 5;
				long timeToSleep = 15000;
				boolean fail = true;
				int attempt = 1;
				while(fail && attempt > attempts) {
					try {
						System.out.println("attempt #"+attempt+" sleeping "+timeToSleep+"ms");						
						Thread.sleep(timeToSleep);
						api.downloadSub(video);
						fail = false;
					} catch (Exception e1) {
						fail = true;
						timeToSleep *= 2;
					}
					attempt++;
				}
				if(attempt >= attempts && fail) {
					timeBetween += timeToAdd;
					if(timeBetween > maxWait) {
						timeBetween = maxWait;
					}
					System.out.println("Time to wait: "+timeBetween);
					throw new RuntimeException("Max number of attempts");
					//TODO re-estability connection?
				}
			} else {
				throw new RuntimeException(e);
			}		
		}
	}
	
	private static boolean isSkipped(File file) {
		String path = file.getAbsolutePath().toLowerCase();
		return skipWords.stream().anyMatch(word -> path.contains(word));
	}
	
	private static boolean isVideo(File file) {
		String filename = file.getName().toLowerCase();
		return videoExtenstions.stream().anyMatch(ext -> filename.endsWith(ext));
	}
	
	private static boolean isSubtitle(Path path) {
		String filename = path.toFile().getName().toLowerCase();
		return subtitleExtensions.stream().anyMatch(ext -> filename.endsWith(ext));
	}
	
	private static boolean hasSubtitles(File video) {
		boolean hasSubtitles = false;
		try {
			hasSubtitles = Files.find(video.getParentFile().toPath(), 1, 
						(path,attrs) -> isSubtitle(path))
				.findFirst()
				.isPresent();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return hasSubtitles;
	}
	
	private static void writeFailed() throws Exception {
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(failedFile))) {
		    for (String path : failed) {
		        pw.println(path);
		    }
		}
	}
	
	private static void readFailed() {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(failedFile)))) {
		    String line = null;
		    while ((line = br.readLine()) != null) {
		       failed.add(line);
		       System.out.println("Adding failed: "+line);
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
