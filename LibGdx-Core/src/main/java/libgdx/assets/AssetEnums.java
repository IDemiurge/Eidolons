package libgdx.assets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import libgdx.texture.SmartTextureAtlas;
import main.data.filesys.PathFinder;

public class AssetEnums {
    static final String SEPARATOR = "_";
    static final String ANIM = "anim";
    static final String[][] substitutesWeapons = {
            {"golem fist", "armored fist"},
            {"tail", "insect claws"},
            {"paws", "claws"},
            {"tentacle", "insect claws"},
            {"lance", "spear"},
            {"glaive", "battle spear"},
            {"sickle", "hand axe"},
            {"orcish arrows", "arrows"},
            {"elven arrows", "arrows"},
            {"heavy bolts", "bolts"},
            {"heavy crossbow", "crossbow"},
            {"hand crossbow", "crossbow"},
            {"longbow", "short bow"},
    };
    static final String[][] substitutesActions = {
            {"fist swing", "punch"},
            {"aimed shot", "quick shot"}
    };

    public enum ATLAS {
        UI_BASE,
        UI_MACRO,
        UI_DC,

        TEXTURES,
        UNIT_VIEW,

        SPRITES_GRID,
        SPRITES_ONEFRAME,
        SPRITES_UI,

        BOSS_ARIUS,

        VFX_AMBI,
        VFX_SPELL,

        ;
        public final String path;
        public SmartTextureAtlas file;
        public String prefix;

        ATLAS() {
            prefix = PathFinder.getAtlasGenPath() +name().toLowerCase();
            path = prefix+"/"+ name().toLowerCase() + ".txt";
            //temp

        }

        public TextureAtlas.AtlasRegion findRegion(String name) {
            if (file == null) {
                return null;
            }
            return file.findRegion(name);
        }
        //filter? Or manual gen?

    }
}
