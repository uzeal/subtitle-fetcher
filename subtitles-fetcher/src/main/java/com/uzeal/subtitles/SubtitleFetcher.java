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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.client.XmlRpcHttpTransportException;

import com.uzeal.subtitles.opensub.OpenSubtitleAPI;

public class SubtitleFetcher {
	private static final Set<String> subtitleExtensions = Stream.of(".srt",".sub",".ass",".ssa").collect(Collectors.toSet());
	private static final Set<String> videoExtenstions = Stream.of(".mkv",".mp4",".avi",".mov",".m4v").collect(Collectors.toSet());
	private static final Set<String> skipWords = Stream.of("anime").collect(Collectors.toSet());
	private static final String backlogFile = "backlog.txt";
	private static long timeBetween = 2000;
	private static long timeToAdd = 500;
	private static long maxWait = 15000;
	private static int maxDownloaded = 200;
	private static int downloaded = 0;
	private static Set<String> backlog;
	private static Set<String> backlogRemove;
	
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
			
			backlog = new HashSet<String>();
			backlogRemove = new HashSet<String>();
			readBacklog();
			
			if(args.length > 2 && Boolean.TRUE.equals(Boolean.parseBoolean(args[2]))) {
				OpenSubtitleAPI api = new OpenSubtitleAPI();
				try {
					api.login(args[0], args[1]);
					processBacklog(api);
				} catch(RuntimeException e) {
					throw new Exception(e);
				} finally {
					api.logOut();
				}
			} else if(sources != null && !sources.isEmpty() && args.length > 1) {
				OpenSubtitleAPI api = new OpenSubtitleAPI();
				try {
					api.login(args[0], args[1]);
					sources.forEach((sourceName,path) -> processSource(api, (String)sourceName, (String)path, false));
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
			writeBacklog();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished downloaded: "+downloaded+" subs");
	}
	
	private static void processSource(OpenSubtitleAPI api, String sourceName, String path, boolean allowNonExactMatch) {
		System.out.println("Processing Source: "+sourceName+" with path: "+path);
		try {
			Files.walk(new File(path).toPath())
				.map(filePath -> filePath.toFile())
				.filter(file -> !backlog.contains(file.getAbsolutePath()))
				.filter(file -> !isSkipped(file))
				.filter(file -> !file.isDirectory() && isVideo(file))
				.filter(video -> !hasSubtitles(video))
				.forEach(video -> waitAndDownload(api, video, allowNonExactMatch));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	private static void processBacklog(OpenSubtitleAPI api) {
		System.out.println("Processing backlog of: "+backlog.size());
		backlog.stream().map(path -> new File(path))
			.filter(file -> !file.isDirectory() && isVideo(file))
			.filter(video -> !hasSubtitles(video))
			.forEach(video -> processBacklog(api, video));
	}
	
	private static void processBacklog(OpenSubtitleAPI api, File video) {
		boolean downloaded = waitAndDownload(api, video, true);
		if(downloaded) {
			backlogRemove.add(video.getAbsolutePath());
		}
	}
	
	private static boolean waitAndDownload(OpenSubtitleAPI api, File video, boolean allowNonExactMatch) {
		boolean downloadedFile = false;
		ShowInfo show = getShowInfo(video);
		try {
			Thread.sleep(timeBetween);		
			if(!api.downloadSub(video, show, allowNonExactMatch)) {
				System.out.println("Didnt download skipping next time");
				if(!backlog.contains(video.getAbsolutePath())) {
					backlog.add(video.getAbsolutePath());
				}
			} else {
				downloadedFile = true;
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
						if(api.downloadSub(video, show, allowNonExactMatch)) {
							fail = false;
							downloadedFile = true;
							downloaded++;
							if(downloaded >= maxDownloaded) {
								throw new RuntimeException("Max Downloaded");
							}
						}
						
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
		return downloadedFile;
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
	
	private static void writeBacklog() throws Exception {
		System.out.println("Removing "+backlogRemove.size()+" from the backlog.");
		backlogRemove.stream().forEach(path -> backlog.remove(path));
		System.out.println("Writing backlog file: "+backlog.size());
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(backlogFile))) {
		    for (String path : backlog) {
		        pw.println(path);
		    }
		}
	}
	
	private static void readBacklog() {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(backlogFile)))) {
		    String line = null;
		    while ((line = br.readLine()) != null) {
		    	line = line.trim();
		    	if(!line.isEmpty()) {
			    	backlog.add(line);
		    	}
		    }
		    System.out.println("Read "+backlog.size()+" backlog entries");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static ShowInfo getShowInfo(File video) {
		ShowInfo show = new ShowInfo();
		File parent = video.getParentFile();
		if(parent.getName().startsWith("Season ")) {
			show.setMovie(false);
			show.setSeason(Integer.valueOf(parent.getName().substring(7)));
			show.setShowName(sanitizeName(parent.getParentFile().getName()));
			getEpisode(video, show);
		} else {
			getMovieInfo(video, show);
		}
		return show;
	}
	
	private static void getMovieInfo(File video, ShowInfo show) {
		show.setMovie(true);
		String movieName = video.getParentFile().getName();
		int index = movieName.indexOf('(');
		if(index > 0) {
			show.setYear(movieName.substring(index+1, movieName.indexOf(')')));
			show.setShowName(sanitizeName(movieName.substring(0,index)));
		} else {
			show.setShowName(sanitizeName(movieName));
		}
	}
	
	private static void getEpisode(File video, ShowInfo show) {	
		int index = video.getName().indexOf(buildSXXE(show.getSeason()));
		if(index > 0) {
			String ep = video.getName().substring(index+4, index+6);
			show.setEpisode(Integer.valueOf(ep));
			show.setEpisodeName(sanitizeName(FilenameUtils.getBaseName(video.getName().substring(index+6))));
		}

	}
	
	private static String buildSXXE(Integer season) {
		StringBuilder builder = new StringBuilder("S");
		if(season.intValue() < 10) {
			builder.append("0");
		}
		builder.append(season);
		builder.append("E");
		return builder.toString();
	}
	
	private static String sanitizeName(String name) {
		return StringUtils.replace(name,"."," ").trim();
	}
}
