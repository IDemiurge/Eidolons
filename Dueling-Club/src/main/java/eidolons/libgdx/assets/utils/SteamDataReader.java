package eidolons.libgdx.assets.utils;

import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.List;

public class SteamDataReader {
    private final String root;

    public SteamDataReader(String root) {
        this.root = root;
    }

    public SteamDataModel readDataModel() {
        //read from txt?
        String path = root+ PathFinder.RES_FOLDER_NAME;
        List<File> filesRes = FileManager.getFilesFromDirectory(path, false, true);
        path = root+  "XML" ;
        List<File> filesXml = FileManager.getFilesFromDirectory(path, false, true);
        return new SteamDataModel(filesRes, filesXml);
    }

    public void writeToTree(SteamDataModel model){

    }
}
