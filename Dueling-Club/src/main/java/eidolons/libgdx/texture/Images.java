package eidolons.libgdx.texture;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 4/17/2018.
 */
public class Images {
    public static final String EMPTY_SPELL = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "spell", "empty.png");
    public static final String SPELLBOOK = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "spell", "spellbook.png");
    public static final String EMPTY_ITEM = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "inv", "empty.png");
    public static final String EMPTY_QUICK_ITEM = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "inv", "empty QUICK.png");
    public static final String LOGO32 = "ui/arcane tower/logo32.png";
    public static final String LOGO64 = "ui/arcane tower/logo64.png";
}
