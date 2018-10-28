package com.uzeal.subtitles.opensub;

import org.apache.xmlrpc.XmlRpcException;

public class OpenSubMain {
	public static void main(String[] args) {
		OpenSubtitle openSubtitle=new OpenSubtitle();
	    try {
			openSubtitle.login("uzeal07","SlayerFLCL7ope00");
			openSubtitle.ServerInfo();
			openSubtitle.getSubLanguages();
			//openSubtitle.getTvSeriesSubs(TvseriesName, season, episode, limit, language)
			openSubtitle.Search("\\\\mediapc\\Cylon\\TV\\True Detective\\Season 02\\True.Detective.S02E01.The.Western.Book.of.the.Dead.mkv");
			openSubtitle.logOut();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
}
