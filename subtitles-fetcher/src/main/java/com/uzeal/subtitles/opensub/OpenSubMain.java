package com.uzeal.subtitles.opensub;

import java.io.File;

public class OpenSubMain {
	public static void main(String[] args) {
		OpenSubtitleAPI openSubtitle=new OpenSubtitleAPI();
	    try {
			openSubtitle.login(args[0],args[1]);
			File video = new File(args[2]);
			openSubtitle.downloadSub(video);		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			openSubtitle.logOut();
		}
	}
}
