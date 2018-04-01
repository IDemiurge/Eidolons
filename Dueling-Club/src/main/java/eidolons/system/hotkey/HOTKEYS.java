package eidolons.system.hotkey;

public enum HOTKEYS {
    END_TURN('E'),
    SELECT_HERO(' '),
    // SELECT_NEXT,
    // ACTION_X, // 1- 9
    // SPELL_X, // 1- 9

    ;
    public int MODE;
    public char CHAR;

    HOTKEYS(char CHAR) {
        this.CHAR = CHAR;
        this.MODE = DC_KeyManager.DEFAULT_MODE;
    }

    public static HOTKEYS getHotkey(char CHAR2) {
        for (HOTKEYS hotKey : HOTKEYS.values()) {

            if (hotKey.CHAR == CHAR2) {
                return hotKey;
            }

        }
        return null;
    }

}
