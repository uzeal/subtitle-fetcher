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
			openSubtitle.Search("C:\\Users\\Matt\\Desktop\\test\\The Big Bang Theory\\Season 11\\The.Big.Bang.Theory.S11E15.The.Novelization.Correlation.HDTV-720p.mkv");
			openSubtitle.logOut();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
}
