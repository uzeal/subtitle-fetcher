package com.uzeal.subtitles;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import com.uzeal.subtitles.util.Initializer;

public class SubtitleFetcher {
	private static final ArrayList<String> subtitleExtensions = new ArrayList<String>();
	private static final ArrayList<String> fileDirs = new ArrayList<String>();
	static {
		subtitleExtensions.add(".srt");
		subtitleExtensions.add(".sub");
	}
	
	public static void main(String[] args) {
		try {
			Initializer.initDB();
			File currentDir = new File(".");
			System.out.println("Reading file from: "+currentDir.getAbsolutePath()+"main-dirs.properties");
			Properties sources = null;
			try(FileInputStream fis = new FileInputStream("main-dirs.properties")) {
				sources = new Properties();
				sources.load(fis);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(sources != null && !sources.isEmpty()) {
				sources.forEach((souurceName,path) -> processSource((String)souurceName,(String)path));
			} else {
				System.out.println("Empty or null main-dirs.properties");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
					
	}
	
	private static void processSource(String sourceName, String path) {
		System.out.println("Processing Source: "+sourceName+" with path: "+path);
	}
	
	private static boolean hasSubtitles(File video) {
		boolean subExits = false;
		String filename = video.getName();
		String  basename = FilenameUtils.getBaseName(filename);
		for(String ext : subtitleExtensions) {
			String subname = basename+ext;
			File sub = new File(video.getParentFile(),subname);
			if(sub.exists()) {
				subExits = true;
				break;
			}
		}
		return subExits;
	}
}
