package main.system.graphics;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class DarkNimbus extends NimbusLookAndFeel {
    @Override
    public Color getDerivedColor(String uiDefaultParentName, float hOffset, float sOffset, float bOffset, int aOffset, boolean uiResource) {
        return ColorManager
                .getInvertedColor(super
                        .getDerivedColor(uiDefaultParentName, hOffset, sOffset, bOffset, aOffset, uiResource));
    }
}
