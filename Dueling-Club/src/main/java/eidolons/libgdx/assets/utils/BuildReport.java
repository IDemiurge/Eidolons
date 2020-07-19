package eidolons.libgdx.assets.utils;

import main.data.filesys.PathFinder;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;

public class BuildReport {
    StringBuilder builder = new StringBuilder();
    private String buildId;
    String time = TimeMaster.getTimeStamp();
    private final boolean fast;

    public BuildReport(boolean fast) {
        this.fast = fast;
    }

    public void append(String s) {
        builder.append(s).append("\n");
    }

    public void write() {
        String contents = "Report for build #" + buildId + "\n" +
                builder.toString();
        FileManager.write(contents, getPath());
        String update="\nBuild #"+buildId + " finished at " + time+"\n";
        FileManager.appendToTextFile(update, getPathAll());
    }

    private String getPathAll() {
        return PathFinder.getBuildsInfoPath() + "reports/all reports history.txt";
    }

    private String getPath() {
        return PathFinder.getBuildsInfoPath() + "reports/" +
                SteamPacker.getVersionName() + " " + time + ".txt";
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

}
