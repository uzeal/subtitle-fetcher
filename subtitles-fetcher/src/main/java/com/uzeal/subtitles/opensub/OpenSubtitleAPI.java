package com.uzeal.subtitles.opensub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


public class OpenSubtitleAPI {
    private static String OPEN_SUBTITLES_SERVER="http://api.opensubtitles.org/xml-rpc";

    private XmlRpcClientConfigImpl xmlRpcClientConfig;
    private XmlRpcClient xmlRpcClient;
    private String strToken="";


    public OpenSubtitleAPI(){
        xmlRpcClientConfig=new XmlRpcClientConfigImpl();
        xmlRpcClient=new XmlRpcClient();

        try {
            xmlRpcClientConfig.setServerURL(new URL(OPEN_SUBTITLES_SERVER));
            xmlRpcClient.setConfig(xmlRpcClientConfig);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }  
    
    @SuppressWarnings("unchecked")
	public String login(String username, String password) throws XmlRpcException{
        List<String> params=new ArrayList<String>();
        HashMap<String,String> retVal;

        params.add(username);
        params.add(password);
        params.add("eng");
        params.add("moviejukebox 1.0.15");
            retVal=(HashMap<String, String>)xmlRpcClient.execute("LogIn", params);
            strToken = (String) retVal.get("token");
        return strToken;

    }

    public void logOut(){
        List<String> params=new ArrayList<String>();
        params.add(strToken);
        try {
            xmlRpcClient.execute("LogOut",params);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void downloadSub(File video) {
    	try {
	    	System.out.println("Downloading sub for: "+video.getName());
	    	List<SubtitleInfo> subtitles = search(video);
	    	if(subtitles != null && !subtitles.isEmpty()) {
				SubtitleInfo sub = chooseBestSubtitle(subtitles);
				if(sub != null) {
					downloadSubtitle(video, sub);
				} else {
					System.out.println("Error choosing best sub");
				}
	    	} else {
	    		System.out.println("No subs found");
	    	} 
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}    	
    }

    public List<SubtitleInfo> search(File video) throws XmlRpcException {
        String fileHash = OpenSubtitleHasher.computeHash(video);
        System.out.println("Hash: "+fileHash);
        List<SubtitleInfo> infos=new ArrayList<SubtitleInfo>();
        Map<String, String> parameterMap = new HashMap<String,String>();
        HashMap<?, ?> retVal;
        parameterMap.put("sublanguageid", Locale.getDefault().getISO3Language());
        parameterMap.put("moviehash", fileHash);
        Object[] paramsArray = new Object[]{strToken, new Object[]{parameterMap}};
        retVal = (HashMap<?, ?>) xmlRpcClient.execute("SearchSubtitles", paramsArray);
        if (retVal.get("data") instanceof Object[]) {
            Object[] data = (Object[]) retVal.get("data");
            for (int i = 0; i < data.length; i++) {
                @SuppressWarnings("unchecked")
				SubtitleInfo info = new SubtitleInfo((HashMap<String, String>) data[i]);
                infos.add(info);
            }
        }
        System.out.println("Total subs length is " + infos.size());

        return infos;
    }

    public SubtitleInfo chooseBestSubtitle(List<SubtitleInfo> subtitles) {
    	SubtitleInfo best = null;
    	for(SubtitleInfo info : subtitles) {
    		if(best == null) {
    			best = info;
    		} else if("0".equals(info.getSubBad())) {
    			double rating = Double.parseDouble(info.getSubRating());
    			long downloads = Long.parseLong(info.getSubDownloadsCnt());
    			
    			double bestRating = Double.parseDouble(best.getSubRating());
    			long bestDownloads = Long.parseLong(best.getSubDownloadsCnt());
    			
    			double diff = rating-bestRating;
    			if(diff > 1.0) {
    				best = info;
    			} else if(diff > -1.0 && downloads > bestDownloads) {
    				best = info;
    			}
    			
    		}
    	}
    	if(best != null) {
    		System.out.println("Choose subtitle with rating: "+best.getSubRating()+" and downloads: "+best.getSubDownloadsCnt());
    	}
    	return best;
    }

    public void downloadSubtitle(File video, SubtitleInfo subtitle) throws IOException {
    	URL url = new URL(subtitle.getSubDownloadLink());
    	System.out.println("Downloading: "+subtitle.getSubDownloadLink());
        URLConnection conn = url.openConnection();
        
        byte[] byteBuffer = new byte[1024];
        String filename = FilenameUtils.getName(subtitle.getSubDownloadLink());
    	
    	File subtitleFile = new File(filename);
    	int total = 0;
    	try(InputStream is = conn.getInputStream();
    		FileOutputStream fileOutputStream = new FileOutputStream(subtitleFile)) {
    		int length = 0;
    		while ((length = conn.getInputStream().read(byteBuffer)) > 0) {
    			total += length;
	            fileOutputStream.write(byteBuffer, 0, length);
	        }
    		fileOutputStream.flush();
    	}
    	System.out.println("Downloaded bytes: "+total+" file: "+filename);
    	
    	if(total > 0) {
	        try (FileInputStream fis = new FileInputStream(subtitleFile);
	    		 GZIPInputStream zipInputStream = new GZIPInputStream(fis)) {
		        
		        File subFile = new File(getSubName(video, subtitle.getSubFileName()));
		        try(FileOutputStream fos = new FileOutputStream(subFile)) {
		        	int length = 0;
			        while ((length = zipInputStream.read(byteBuffer)) > 0) {
			            fos.write(byteBuffer, 0, length);
			        }
			        fos.flush();
		        }
		        System.out.println("Unzipped: "+subFile.getAbsolutePath());
	        } finally {
	        	subtitleFile.delete();
	        }
    	} else {
    		System.out.println("Failed to download sub");
    	}
    }
    
    private String getSubName(File video, String subtitleName) {
    	String videoName = video.getAbsolutePath();
        return FilenameUtils.getFullPath(videoName)+FilenameUtils.getBaseName(videoName)+"."+FilenameUtils.getExtension(subtitleName);
    }
    
    /*public List<SubtitleInfo>SearchMoviesOnImdb(String moviename) throws XmlRpcException {

    List <SubtitleInfo> infos=new ArrayList<SubtitleInfo>();
    HashMap<String,Object[]> retVal;
    List<String> params=new ArrayList<String>();
    params.add(strToken);
    params.add(moviename);
    retVal=(HashMap<String,Object[]>)xmlRpcClient.execute("SearchMoviesOnIMDB", params);
//    System.out.println("ServerInfo"+retVal.toString());
    if(retVal.get("data") instanceof Object[]){
        Object[] data=(Object [])retVal.get("data");
        for (int i=0;i<data.length;i++) {
            SubtitleInfo info=new SubtitleInfo((HashMap<String,String>) data[i]);
            System.out.println("Id is"+info.getIDMovie());
            System.out.println("title is"+info.getMovieName());
//            System.out.println("Link is"+info.getSubDownloadLink());
            infos.add(info);
        }
    }
    return infos;
}

public List<SubtitleInfo> getMovieSubsByName(String moviename,String limit,String language) throws XmlRpcException {

    List<SubtitleInfo> infos=new ArrayList<SubtitleInfo>();
    HashMap<String,Object[]> retVal;
    List<String> params=new ArrayList<String>();
    params.add(strToken);
    HashMap<String,Object> query = new HashMap<String,Object>();
    query.put("query",moviename);
    query.put("sublanguageid",language);
    HashMap<String,Object> query2=new HashMap<String,Object>();
    query2.put("limit", limit);
    Object[] paramsArray = new Object[]{strToken, new Object[]{query},query2};
    retVal=(HashMap<String,Object[]>)xmlRpcClient.execute("SearchSubtitles", paramsArray);
    System.out.println("Status code is "+retVal.get("status"));
    if(retVal.get("data") instanceof Object[]){
        Object[] data=(Object [])retVal.get("data");
        for (int i=0;i<data.length;i++) {
            SubtitleInfo info=new SubtitleInfo((HashMap<String, String>) data[i]);
            System.out.println("Id is "+info.getIDMovieImdb());
            System.out.println("title is "+info.getMovieName());
            System.out.println("Link is "+info.getSubDownloadLink());
            System.out.println("Language is "+info.getLanguageName());
            System.out.println("IMDB rating is "+info.getMovieImdbRating());
            System.out.println("Year is "+info.getMovieYear());
            System.out.println("Sub file name "+info.getSubFileName());
            System.out.println("Date is "+info.getSubAddDate());
            System.out.println("Rating is "+info.getSubRating());
            System.out.println("Downloads is "+info.getSubDownloadsCnt());
            System.out.println("Actual CD name "+info.getSubActualCD());
            System.out.println("Bad is "+info.getSubBad());
            infos.add(info);
        }
    }
    System.out.println("Total subs length is " + ((Object[]) retVal.get("data")).length);
    return infos;
}

public List<SubtitleInfo> getTvSeriesSubs(String TvseriesName,String season,String episode,String limit,String language) throws XmlRpcException {

    List <SubtitleInfo> infos=new ArrayList<SubtitleInfo>();
    HashMap<String,?> retVal;
    List params=new ArrayList();
    params.add(strToken);
    HashMap <String,Object> query = new HashMap<String,Object>();
    query.put("query",TvseriesName);
    query.put("season", season);
    query.put("episode",episode);
    query.put("sublanguageid", language);
    HashMap <String,Object> query2=new HashMap<String,Object>();
    query2.put("limit", limit);
    Object[] paramsArray = new Object[]{strToken, new Object[]{query},query2};
    retVal=(HashMap)xmlRpcClient.execute("SearchSubtitles", paramsArray);
    System.out.println("Status code is " + retVal.get("status"));
    if(retVal.get("data") instanceof Object[]){
        Object[] data=(Object [])retVal.get("data");
        for (int i=0;i<data.length;i++) {
            SubtitleInfo info=new SubtitleInfo((HashMap<String, String>) data[i]);
            System.out.println("Id is "+info.getIDMovieImdb());
            System.out.println("title is "+info.getMovieName());
            System.out.println(info.getSubDownloadLink());
            infos.add(info);
        }
    }
    System.out.println("Total subs length is " + ((Object[]) retVal.get("data")).length);
    return infos;
}
public void getIMDBmovieDetails(String imdbId) throws XmlRpcException {

    HashMap<String,?> retVal;
    List<String> params=new ArrayList<String>();
    params.add(strToken);
    params.add(imdbId);
    retVal=(HashMap)xmlRpcClient.execute("SearchMoviesOnIMDB", params);
    if(retVal.get("data") instanceof Object[]) {
        Object[] data = (Object[]) retVal.get("data");
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i].toString());
        }
    }

}

public void getDetailsFromOmdb(String imdbid) throws IOException {
    URL oracle = new URL("http://www.omdbapi.com/?i="+"tt"+imdbid);
    URLConnection yc = oracle.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(
            yc.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null)
        System.out.println(inputLine);
    in.close();
}

public void getSubLanguages() throws XmlRpcException {
    HashMap<String,?> retVal;
    retVal=(HashMap)xmlRpcClient.execute("GetSubLanguages", (Object[]) null);
//    System.out.println("ServerInfo"+retVal.toString());
    if(retVal.get("data") instanceof Object[]) {
        Object[] data = (Object[]) retVal.get("data");
        for (int i = 0; i < data.length; i++) {
            System.out.println(((HashMap<String,?>)data[i]).toString());
        }
        System.out.println("Length is "+data.length);
    }

}*/

}