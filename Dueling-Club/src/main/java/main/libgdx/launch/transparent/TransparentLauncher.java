package main.libgdx.launch.transparent;

/**
 * Created by JustMe on 11/28/2017.
 */

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import main.client.dc.Launcher;
import main.game.battlecraft.DC_Engine;
import main.game.core.Eidolons;
import main.libgdx.launch.prestart.PreLauncher;

import static com.sun.jna.platform.win32.WinUser.*;

public class TransparentLauncher extends PreLauncher {

    public static final WinDef.DWORD DWM_BB_ENABLE = new WinDef.DWORD(0x00000001);
    public static final WinDef.DWORD DWM_BB_BLURREGION = new WinDef.DWORD(0x00000002);
    public static final WinDef.DWORD DWM_BB_TRANSITIONONMAXIMIZED = new WinDef.DWORD(0x00000004);

    public static final WinDef.HWND HWND_TOPPOS = new WinDef.HWND(new Pointer(-1));

    private static final int SWP_NOSIZE = 0x0001;
    private static final int SWP_NOMOVE = 0x0002;

    private static final int WS_EX_TOOLWINDOW = 0x00000080;
    private static final int WS_EX_APPWINDOW = 0x00040000;

    public static void main(String[] arg) {
        prestart();

        WinDef.HWND hwnd;
        while ((hwnd = User32.INSTANCE.FindWindow(null,
         "Eidolons: Battlecraft v" + Launcher.VERSION
//        "TransparentLauncher"
        )) == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        System.out.println(hwnd);
        System.out.println(transparentWindow(hwnd));
    }

    public static void prestart() {
        DC_Engine.systemInit();
        Eidolons.setApplication(new LwjglApplication(new TransparentLauncher(),
         getConf()));
    }

    //    public static LwjglApplicationConfiguration getConf() {
//        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
//        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//        cfg.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width-1;
//        cfg.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height-1;
//        cfg.resizable = false;
//        cfg.fullscreen = false;
//        cfg.initialBackgroundColor = new Color(0, 0, 0, 0);
//        return cfg;
//    }
    public static boolean transparentWindow(WinDef.HWND hwnd) {
        DWM_BLURBEHIND bb = new DWM_BLURBEHIND();
        bb.dwFlags = DWM_BB_ENABLE;
        bb.fEnable = true;
        bb.hRgnBlur = null;
        DWM.DwmEnableBlurBehindWindow(hwnd, bb);

        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);

        wl &= ~(WS_VISIBLE);

        wl |= WS_EX_TOOLWINDOW;   // flags don't work - windows remains in taskbar
        wl &= ~(WS_EX_APPWINDOW);

        User32.INSTANCE.ShowWindow(hwnd, SW_HIDE); // hide the window
        User32.INSTANCE.SetWindowLong(hwnd, GWL_STYLE, wl); // set the style
        User32.INSTANCE.ShowWindow(hwnd, SW_SHOW); // show the window for the new style to come into effect
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);

        return User32.INSTANCE.SetWindowPos(hwnd, HWND_TOPPOS, 0, 0, 2560, 1440, SWP_NOMOVE | SWP_NOSIZE);
    }

}
