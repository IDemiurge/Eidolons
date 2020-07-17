package eidolons.libgdx.assets.utils;

import java.io.File;
import java.util.List;

public class SteamDataModel {

    protected List<File> filesRes;
    protected List<File> filesXml;
    protected String[] imageFiles;
    protected  String[] textFiles;
    protected  String[] soundFiles;
    protected  String[] musicFiles;
    protected  String[] xmlFiles;

    public SteamDataModel(String[] imageFiles, String[] textFiles, String[] soundFiles, String[] musicFiles, String[] xmlFiles) {
        this.imageFiles = imageFiles;
        this.textFiles = textFiles;
        this.soundFiles = soundFiles;
        this.musicFiles = musicFiles;
        this.xmlFiles = xmlFiles;
    }

    public SteamDataModel(List<File> filesRes, List<File> filesXml) {
        this.filesRes = filesRes;
        this.filesXml = filesXml;
    }
}
