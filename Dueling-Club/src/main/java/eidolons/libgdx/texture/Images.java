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
//     "hq", "inv", "empty.png");
    "dc", "dialog" ,
      "inv" ,
      "empty slots", "empty_pack.jpg");
    public static final String EMPTY_QUICK_ITEM = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "inv", "empty QUICK.png");
    public static final String LOGO32 = "ui/arcane tower/logo32.png";
    public static final String LOGO64 = "ui/arcane tower/logo64.png";

    public static final String TIER = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "hq", "trees", "tier.png");
    public static final String EMPTY_SKILL_SLOT = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "hq", "trees", "empty skill slot.png");
    public static final String EMPTY_RANK_SLOT = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "hq", "trees", "empty rank slot.png");
    public static final String EMPTY_CLASS_SLOT = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "empty class slot.png");
    public static final String CIRCLE_UNDERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "CIRCLE UNDERLAY.png");
    public static final String CIRCLE_OVERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "CIRCLE OVERLAY.png");


    public static final String SMALL_TIER = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "hq", "trees", "SMALL TIER.png");
    public static final String EMPTY_PERK_SLOT = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "hq", "trees", "empty perk slot.png");
    public static final String UNKNOWN_PERK = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "unknown perk.png");
    public static final String DIAMOND_OVERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "DIAMOND OVERLAY.png");
    public static final String DIAMOND_UNDERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "DIAMOND UNDERLAY.png");

    public static final String COLORLESS_BORDER = StrPathBuilder.build(
     PathFinder.getUiPath(), "Borders", "neo", "colorless.png");
    public static final String TARGET_BORDER = StrPathBuilder.build(
     PathFinder.getUiPath(), "Borders", "neo", "TARGET.png");
}
