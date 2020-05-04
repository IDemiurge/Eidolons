package main.system.auxiliary.secondary;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class DevHelper implements HotkeyListener {

    public static void initMusicKeys() {

        JIntellitype.getInstance().addHotKeyListener(new DevHelper());
        int i = 1;
        for (PLAYLIST_TYPE value : PLAYLIST_TYPE.values()) {
            int keycode = 111 + i;
            JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_SHIFT, keycode);
        }
    }

    @Override
    public void onHotKey(int i) {
        main.system.auxiliary.log.LogMaster.log(1,"gotcha "+i );
        switch (i) {
            case 1:
                main.system.auxiliary.log.LogMaster.log(1,"gotcha " );
//                FileManager.getFilesFromDirectory()
//                new ProcessBuilder().
                break;
        }
    }
    public enum PLAYLIST_TYPE{
        deep,
        ost,
        fury,
        gym,
        warmup,
        auto,
    }
}
