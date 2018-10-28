package com.uzeal.subtitles;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.uzeal.subtitles.opensub.OpenSubtitleAPI;

public class SubtitleFetcher {
	private static final List<String> subtitleExtensions = Arrays.asList(".srt",".sub");
	private static final List<String> videoExtenstions = Arrays.asList(".mkv",".mp4",".avi",".mov",".m4v");
	private static final List<String> skipWords = Arrays.asList("anime");
	
	public static void main(String[] args) {
		try {
			//Initializer.initDB();
			File currentDir = new File(".");
			System.out.println("Reading file from: "+currentDir.getAbsolutePath()+"main-dirs.properties");
			Properties sources = null;
			try(FileInputStream fis = new FileInputStream("main-dirs.properties")) {
				sources = new Properties();
				sources.load(fis);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(sources != null && !sources.isEmpty() && args.length > 1) {
				OpenSubtitleAPI api = new OpenSubtitleAPI();
				try {
					api.login(args[0], args[1]);
					sources.forEach((sourceName,path) -> processSource(api, (String)sourceName,(String)path));
				} finally {
					api.logOut();
				}
			} else {
				System.out.println("Empty args or empty main-dirs.properties");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
					
	}
	
	private static void processSource(OpenSubtitleAPI api, String sourceName, String path) {
		System.out.println("Processing Source: "+sourceName+" with path: "+path);
		try {
			Files.walk(new File(path).toPath())
				.map(filePath -> filePath.toFile())
				.filter(file -> !isSkipped(file))
				.filter(file -> !file.isDirectory() && isVideo(file))
				.filter(video -> !hasSubtitles(video))
				.forEach(video -> api.downloadSub(video));
		} catch (Exception e) {
			e.printStackTrace();
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
}
