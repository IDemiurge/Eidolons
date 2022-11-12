package eidolons.netherflame.generic.portrait;

/**
 * Created by Alexander on 2/1/2022
 */
public class PortraitPaths {
    public enum PortraitPiece {
        face, model, background, overlay,
    }

    public enum ColorVersion {
        //could have quite a few... what would they depend on? Player choice - how?  OR Align with BG!
    }

    public String resolvePath(PortraitPiece type, ColorVersion color, int id) {
        //id's are AE-friendly... and we can output that stuff this way too.
        // Originally on PNG with alpha, then use some SpriteCutter logic to calc offsets
        String path = "";
        return path;
    }
}
