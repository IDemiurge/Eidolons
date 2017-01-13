package main.swing.generic.components.editors;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileChooser implements EDITOR {

    JFileChooser fc = new JFileChooser();
    String fileLocation;

    private boolean directoryOnly;
    private String defaultFileLocation;
    private boolean multi;
    private String value = "";

    public FileChooser() {
        this(false);
    }

    public FileChooser(boolean directoryOnly) {
        this(directoryOnly, false);
    }

    public FileChooser(boolean directoryOnly, boolean multi) {
        this.directoryOnly = directoryOnly;
        this.multi = multi;
        if (directoryOnly) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
        }
    }

    public FileChooser(String folder) {
        this(false);
        setDefaultFileLocation(folder);
    }

    @Override
    public String launch(String valueName, String prevValue) {
        if (multi)
            value = prevValue;
        return launch(valueName, fc.getComponentAt(0, 0));
    }

    // @Override
    // public String launch(String prevValue, String valueName) {
    // return launch(valueName, fc.getComponentAt(0, 0));
    // }
    public String launch(String v, Component parent) {
        // fc.get setSize(800, 600);
        fc.setCurrentDirectory(new File(getDefaultFileLocation()));
        if (!directoryOnly) {
            fileLocation = v;
            if (checkFile(fileLocation))
                fc.setCurrentDirectory(new File(getDefaultFileLocation() + fileLocation));
        }

        int returnVal = fc.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            String selected = file.getPath();
if (getDefaultFileLocation()!=null )
            selected =StringMaster.replaceFirst(selected,
             getDefaultFileLocation()
             , "");
            selected =StringMaster.replaceFirst(selected,
             PathFinder.getEnginePathPlusNewResourceProject()
             , "");
            selected =StringMaster.replaceFirst(selected,
             PathFinder.getEnginePath()
             , "");
            if (multi) {
                if (value.isEmpty())
                    return selected;
                value = value + ";" + selected;
                return value;
            }
            if (!directoryOnly)
                if (file.isFile())
                    fileLocation = selected;
            return selected;
        }
        return fileLocation;
    }

    protected boolean checkFile(String fileLocation) {
        if (directoryOnly)
            return new File(getDefaultFileLocation() + "\\" + fileLocation).isDirectory();
        return new File(getDefaultFileLocation() + "\\" + fileLocation).isFile();
    }

    protected String getDefaultFileLocation() {
        if (defaultFileLocation != null)
            return defaultFileLocation;
        return PathFinder.getImagePath();
    }

    // PathFinder.getEnginePath() + PathFinder.getRES_PATH() +
    public void setDefaultFileLocation(String defaultFileLocation) {
        this.defaultFileLocation = defaultFileLocation;
    }

    @Override
    public void launch(JTable table, int row, int column, String v) {

        String selected = launch(v, (String) table.getModel().getValueAt(row, 1));
        if (table != null)
            table.getModel().setValueAt(selected, row, 1);
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

}
