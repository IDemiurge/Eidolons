package main.libgdx.launch.transparent;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/**
 * Created by JustMe on 11/28/2017.
 */
public class DWM {
    static {
        Native.register("Dwmapi");
    }

    public static native WinNT.HRESULT DwmEnableBlurBehindWindow(WinDef.HWND hWnd, DWM_BLURBEHIND pBlurBehind);

}
