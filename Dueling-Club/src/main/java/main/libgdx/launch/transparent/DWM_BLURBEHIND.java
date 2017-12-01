package main.libgdx.launch.transparent;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */

    public class DWM_BLURBEHIND extends Structure {

        public WinDef.DWORD dwFlags;
        public boolean  fEnable;
        public WinDef.HRGN hRgnBlur;
        public boolean  fTransitionOnMaximized;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwFlags", "fEnable", "hRgnBlur", "fTransitionOnMaximized");
        }
//    public class DWM {
//        static {
//            Native.register("Dwmapi");
//        }
//        public static native WinNT.HRESULT DwmEnableBlurBehindWindow(WinDef.HWND hWnd, DWM_BLURBEHIND pBlurBehind);
//    }
}
