package com.uzeal.subtitles.opensub;

import java.util.HashMap;

/**
 * Created by sachin on 3/4/16.
 */
public class SubtitleInfo {

    private String IDSubMovieFile;
    private String MovieHash,MovieByteSize,MovieTimeMS,MovieFrames,IDSubtitleFile,SubFileName,SubActualCD,SubSize,SubHash;
    private String IDSubtitle,UserID,SubLanguageID,SubFormat,SubSumCD,SubAddDate,SubDownloadsCnt,SubBad,SubRating;
    private String IDMovie,IDMovieImdb,MovieName,MovieNameEng,MovieYear,MovieImdbRating,UserNickName,ISO639,LanguageName,SubDownloadLink;

    public SubtitleInfo (HashMap<String, String> info){
        IDSubMovieFile=  info.get("IDSubMovieFile");
        MovieHash=  info.get("MovieHash");
        MovieByteSize= info.get("MovieByteSize");
        MovieTimeMS=  info.get("MovieTimeMS");
        MovieFrames= info.get("MovieFrames");
        IDSubtitleFile= info.get("IDSubtitleFile");
        SubFileName= info.get("SubFileName");
        SubActualCD= info.get("SubActualCD");
        SubSize= info.get("SubSize");
        SubHash= info.get("SubHash");
        IDSubtitle=info.get("IDSubtitle");
        UserID=info.get("UserID");
        SubLanguageID=info.get("SubLanguageID");
        SubFormat=info.get("SubFormat");
        SubSumCD=info.get("SubSumCD");
        SubAddDate=info.get("SubAddDate");
        SubDownloadsCnt=info.get("SubDownloadsCnt");
        SubBad=info.get("SubBad");
        SubRating=info.get("SubRating");
        IDMovie=info.get("IDMovie");
        IDMovieImdb=info.get("IDMovieImdb");
        MovieName=info.get("MovieName");
        MovieNameEng=info.get("MovieNameEng");
        MovieYear=info.get("MovieYear");
        MovieImdbRating="tt"+info.get("MovieImdbRating");
        UserNickName=info.get("UserNickName");
        ISO639=info.get("ISO639");
        LanguageName=info.get("LanguageName");
        SubDownloadLink=info.get("SubDownloadLink");

    }
    public String getIDSubMovieFile() {
        return IDSubMovieFile;
    }

    public void setIDSubMovieFile(String IDSubMovieFile) {
        this.IDSubMovieFile = IDSubMovieFile;
    }

    public String getMovieHash() {
        return MovieHash;
    }

    public void setMovieHash(String movieHash) {
        MovieHash = movieHash;
    }

    public String getMovieByteSize() {
        return MovieByteSize;
    }

    public void setMovieByteSize(String movieByteSize) {
        MovieByteSize = movieByteSize;
    }

    public String getMovieTimeMS() {
        return MovieTimeMS;
    }

    public void setMovieTimeMS(String movieTimeMS) {
        MovieTimeMS = movieTimeMS;
    }

    public String getMovieFrames() {
        return MovieFrames;
    }

    public void setMovieFrames(String movieFrames) {
        MovieFrames = movieFrames;
    }

    public String getIDSubtitleFile() {
        return IDSubtitleFile;
    }

    public void setIDSubtitleFile(String IDSubtitleFile) {
        this.IDSubtitleFile = IDSubtitleFile;
    }

    public String getSubFileName() {
        return SubFileName;
    }

    public void setSubFileName(String subFileName) {
        SubFileName = subFileName;
    }

    public String getSubActualCD() {
        return SubActualCD;
    }

    public void setSubActualCD(String subActualCD) {
        SubActualCD = subActualCD;
    }

    public String getSubSize() {
        return SubSize;
    }

    public void setSubSize(String subSize) {
        SubSize = subSize;
    }

    public String getSubHash() {
        return SubHash;
    }

    public void setSubHash(String subHash) {
        SubHash = subHash;
    }

    public String getIDSubtitle() {
        return IDSubtitle;
    }

    public void setIDSubtitle(String IDSubtitle) {
        this.IDSubtitle = IDSubtitle;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getSubLanguageID() {
        return SubLanguageID;
    }

    public void setSubLanguageID(String subLanguageID) {
        SubLanguageID = subLanguageID;
    }

    public String getSubFormat() {
        return SubFormat;
    }

    public void setSubFormat(String subFormat) {
        SubFormat = subFormat;
    }

    public String getSubSumCD() {
        return SubSumCD;
    }

    public void setSubSumCD(String subSumCD) {
        SubSumCD = subSumCD;
    }

    public String getSubAddDate() {
        return SubAddDate;
    }

    public void setSubAddDate(String subAddDate) {
        SubAddDate = subAddDate;
    }

    public String getSubDownloadsCnt() {
        return SubDownloadsCnt;
    }

    public void setSubDownloadsCnt(String subDownloadsCnt) {
        SubDownloadsCnt = subDownloadsCnt;
    }

    public String getSubBad() {
        return SubBad;
    }

    public void setSubBad(String subBad) {
        SubBad = subBad;
    }

    public String getSubRating() {
        return SubRating;
    }

    public void setSubRating(String subRating) {
        SubRating = subRating;
    }

    public String getIDMovie() {
        return IDMovie;
    }

    public void setIDMovie(String IDMovie) {
        this.IDMovie = IDMovie;
    }

    public String getIDMovieImdb() {
        return IDMovieImdb;
    }

    public void setIDMovieImdb(String IDMovieImdb) {
        this.IDMovieImdb = IDMovieImdb;
    }

    public String getMovieName() {
        return MovieName;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public String getMovieNameEng() {
        return MovieNameEng;
    }

    public void setMovieNameEng(String movieNameEng) {
        MovieNameEng = movieNameEng;
    }

    public String getMovieYear() {
        return MovieYear;
    }

    public void setMovieYear(String movieYear) {
        MovieYear = movieYear;
    }

    public String getMovieImdbRating() {
        return MovieImdbRating;
    }

    public void setMovieImdbRating(String movieImdbRating) {
        MovieImdbRating = movieImdbRating;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    public String getISO639() {
        return ISO639;
    }

    public void setISO639(String ISO639) {
        this.ISO639 = ISO639;
    }

    public String getLanguageName() {
        return LanguageName;
    }

    public void setLanguageName(String languageName) {
        LanguageName = languageName;
    }

    public String getSubDownloadLink() {
        return SubDownloadLink;
    }

    public void setSubDownloadLink(String subDownloadLink) {
        SubDownloadLink = subDownloadLink;
    }
}
