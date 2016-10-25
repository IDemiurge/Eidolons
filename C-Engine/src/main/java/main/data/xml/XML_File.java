package main.data.xml;

import main.content.OBJ_TYPE;

import java.io.File;

public class XML_File {
    OBJ_TYPE type;
    String name;
    String group;
    boolean macro;
    String contents;
    private File file;

    public XML_File(OBJ_TYPE type, String name, String group, boolean macro,
                    String contents) {
        this.type = type;
        this.contents = contents;
        this.name = name;
        this.group = group;
        this.macro = macro;
    }

    @Override
    public String toString() {

        return "Xml file: " + name + " " + group + " " + type;
    }

    public OBJ_TYPE getType() {
        return type;
    }

    public void setType(OBJ_TYPE type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isMacro() {
        return macro;
    }

    public void setMacro(boolean macro) {
        this.macro = macro;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;

    }
}
