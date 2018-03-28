package main.system.hotkey;

public class HotKey {
    private static int ID = -1;
    public int MODE;
    public char CHAR;
    public int id;
    private HOTKEYS hotKey;

    public HotKey(HOTKEYS hotKey) {
        this.hotKey = hotKey;
        this.CHAR = hotKey.CHAR;
        this.MODE = hotKey.MODE;
        this.id = getID();
    }

    public HotKey(char CHAR1) {
        this.CHAR = CHAR1;
        this.MODE = DC_KeyManager.DEFAULT_MODE;
        this.id = getID();
    }

    public HotKey(int MODE1, char CHAR1) {
        this.CHAR = CHAR1;
        this.MODE = MODE1;
        this.id = getID();
    }

    public HotKey(char c, boolean b) {
        this.CHAR = c;
        this.MODE = (b) ? DC_KeyManager.ALT_MODE : DC_KeyManager.DEFAULT_MODE;
        this.id = getID();
    }

    public static int getID() {
        ID++;
        return ID;
    }

    public static void setID(int iD) {
        ID = iD;
    }

    @Override
    public String toString() {
        return id + " = " + CHAR
         + ((hotKey != null) ? hotKey.name() : " special ");
    }

    public HOTKEYS getHOTKEY() {
        return hotKey;
    }

    public int getMODE() {
        return MODE;
    }

    public void setMODE(int mODE) {
        MODE = mODE;
    }

    public char getCHAR() {
        return CHAR;
    }

    public void setCHAR(char cHAR) {
        CHAR = cHAR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HOTKEYS getHotKey() {
        return hotKey;
    }

    public void setHotKey(HOTKEYS hotKey) {
        this.hotKey = hotKey;
    }
}
