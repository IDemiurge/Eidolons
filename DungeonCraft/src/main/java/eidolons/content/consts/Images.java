package eidolons.content.consts;

import eidolons.netherflame.campaign.assets.NF_Images;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.data.filesys.PathFinder;
import main.system.ExceptionMaster;
import main.system.auxiliary.StrPathBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 4/17/2018.
 */
public class Images {
    public static final String EMPTY_SPELL = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "spell", "empty.png");
    public static final String SPELLBOOK = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "spell", "spellbook.png");
    public static final String EMPTY_LIST_ITEM = StrPathBuilder.build(PathFinder.getUiPath(), "empty_list_item.jpg");
    public static final String EMPTY_CONTAINER_SPACE = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "hq", "inv", "empty.png");

    public static final String EMPTY_ITEM = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "generic" , "empty slots", "empty_pack.jpg");
    public static final String EMPTY_QUICK_ITEM = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "generic" , "empty slots", "empty QUICK.png");
    public static final String EMPTY_WEAPON_MAIN = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "generic", "empty slots", "empty weapon main.png");
    public static final String EMPTY_WEAPON_OFFHAND = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "generic", "empty slots", "empty weapon off.png");
    public static final String EMPTY_ARMOR = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "generic", "empty slots", "empty armor.png");
    public static final String EMPTY_AMULET = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "generic", "empty slots", "amulet_empty_slot.png");
    public static final String EMPTY_RING = StrPathBuilder.build(
     PathFinder.getComponentsPath(),
     "generic", "empty slots", "ring_empty_slot.png");








    public static final String LOGO_EDITOR_64 = "ui/main/editor.png";
    public static final String LOGO_EDITOR_32 = "ui/main/editor32.png";

    public static final String LOGO32 = "ui/main/logo32.png";
    public static final String LOGO64 = "ui/main/logo64.png";

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
     PathFinder.getComponentsPath(), "hq", "trees", "diamond overlay.png");
    public static final String DIAMOND_UNDERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "trees", "diamond underlay.png");

    public static final String COLORLESS_BORDER = StrPathBuilder.build(
     PathFinder.getUiPath(), "components","generic",  "Borders", "neo", "colorless.png");
    public static final String TARGET_BORDER = StrPathBuilder.build(
            PathFinder.getUiPath(), "components","generic", "Borders", "neo", "target.png");
    public static final String TARGET_BORDER_CIRCLE = StrPathBuilder.build(
            PathFinder.getUiPath(), "components","generic", "Borders", "neo", "target rounded.png");
    public static final String TARGET_BORDER_CIRCLE_96 = StrPathBuilder.build(
            PathFinder.getUiPath(), "components","generic", "Borders", "neo", "target rounded 96.png");
    public static final String WEAVE_LINK = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "weave", "link.png");
    public static final String WEAVE_OVERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "weave", "overlay.png");
    public static final String WEAVE_UNDERLAY = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "weave", "underlay.png");
    public static final String WEAVE_BACKGROUND = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq", "weave", "background.jpg");

    public static final String HC_SCROLL_BACKGROUND = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "hq","hc", "hc_scroll_background.jpg");
    public static final String PARTY_BACKGROUND_COLS = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic/decor/columns.png");

    public static final String COLUMNS = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic/decor/columns.png");
//    public static final String COLUMNS_CROPPED = StrPathBuilder.build(
//            PathFinder.getComponentsPath(), "generic/decor/columns cropped.png");
public static final String SEPARATOR_NARROW = StrPathBuilder.build(
        PathFinder.getComponentsPath(), "generic/decor/SEPARATOR alt.png");

    public static final String SEPARATOR_METAL = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic/decor/separator metal.png");
    public static final String SEPARATOR_METAL_VERTICAL = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic/decor/separator metal vertical.png");

    public static final String SEPARATOR_NARROW_VERTICAL = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic/decor/separator alt vertical.png");
    public static final String SEPARATOR = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic","decor" ,"separator.png");
    public static final String SEPARATOR_LARGE = StrPathBuilder.build(
            PathFinder.getComponentsPath(), "generic","decor" ,"horizontal slice white.png");
    public static final  String TINY_CHEST = "ui/components/tiny/chest.png";
    public static final  String TINY_GOLD = "ui/components/tiny/gold.png";
    public static final java.lang.String GOLD_INV_ITEM_OVERLAY = "ui/components/hq/inv/gold overlay.png";
    public static final String CHEST_OPEN = "ui/components/hq/inv/stash.png";
    public static final String STASH_LANTERN = "ui/components/hq/inv/stash LANTERN.png";
    public static final  String WEIGHT = "ui/components/hq/inv/WEIGHT.png";
    public static final  String WEIGHT_BURDENED = "ui/components/hq/inv/WEIGHT burdened.png";
    public static final String SHOP_PRICES = "ui/components/hq/inv/prices.png";
    public static final String SHOP_DEBT =  "ui/components/hq/inv/prices.png";
    public static final String GOLD_PACK_LARGE = "ui/components/hq/inv/GOLD_PACK_LARGE.png";
    public static final String GOLD_PACK_AVERAGE = "ui/components/hq/inv/GOLD_PACK_AVERAGE.png";
    public static final String GOLD_PACK_SMALL = "ui/components/hq/inv/GOLD_PACK_SMALL.png";
    public static final String ITEM_BACKGROUND_GOLD = "ui/components/hq/inv/ITEM BACKGROUND GOLD.png";
    public static final String ITEM_BACKGROUND_STEEL = "ui/components/hq/inv/ITEM BACKGROUND STEEL.png";
    public static final String ITEM_BACKGROUND_STONE = "ui/components/hq/inv/ITEM BACKGROUND STONE.png";
    public static final String ITEM_BACKGROUND = "ui/components/hq/inv/ITEM BACKGROUND.png";

    public static final String ITEM_BACKGROUND_OVERLAY_LIGHT = "ui/components/hq/inv/overlays/overlay light.png";
    public static final String ITEM_BACKGROUND_OVERLAY_LIGHT2 = "ui/components/hq/inv/overlays/overlay light2.png";

    public static final String ITEM_BACKGROUND_OVERLAY_NORMAL = "ui/components/hq/inv/overlays/NORMAL.png";
    public static final String ITEM_BACKGROUND_OVERLAY_CRACKS = "ui/components/hq/inv/overlays/CRACKS.png";
    public static final String ITEM_BACKGROUND_OVERLAY_BRILLIANT = "ui/components/hq/inv/overlays/BRILLIANT.png";
    public static final String ITEM_BACKGROUND_OVERLAY_MAGIC = "ui/components/hq/inv/overlays/MAGIC.png";

    public static final String PLACEHOLDER = "ui/empty1.jpg";
    public static final String PLACEHOLDER_UNIT = "ui/empty.jpg";
    public static final String PLACEHOLDER_WALL = "main/bf/walls/ancient wall.png";
    public static final String PLACEHOLDER_DECOR = "main/bf/prop/magical/sphere_altar_dark_active.png";
    public static final String DEFAULT_SPRITE = "sprites/spells/impact/wreathe in flames 5 5.png";
    public static final String DEFEAT = "ui/big/defeat.png";
    public static final String VICTORY = "ui/big/victory.png";
    public static final String GATEWAY_GLYPH = "sprites/bf/hanging/occult_circles.png";
    public static final String COLUMNS_AND_TREE_BG = "ui/components/lord/tab bg.png";

    public static final String BG_EIDOLONS = NF_Images.MAIN_ART.HALL2.getPath();
    public static final String CIRCLE_BORDER = "ui/components/generic/borders/border circle.png";
    public static final String EMPTY_SKULL = "ui/empty1.jpg";
    public static final String DEMIURGE = "main/heroes/demiurge.jpg";
    public static final String OVERLAY_LIGHT_SKULL = "ui/cells/outlines/overlays/skull.png";
    public static final String OVERLAY_DARK = "ui/cells/outlines/overlays/dark.png";
    public static final String ROLL_ARROW = "ui/roll arrow.png";
    public static final String REALLY_EMPTY_32 ="ui/really empty 32.png" ;
    public static final String MISSING_TEXTURE = "ui/missing.png";
    public static final String COLOR_EMBLEM = "ui/color emblem.png";
    public static final String ZARK_TITLE = "ui/components/ninepatch/zark/title box.png";
    public static final String ZARK_BOX_UPSIDE_DOWN = "ui/components/ninepatch/zark/zark box down.png";
    public static final String ZARK_BOX = "ui/components/ninepatch/zark/zark box.png";
    public static final String ZARK_BTN_LARGE ="ui/components/ninepatch/zark/btn2.png" ;
    public static final String INTENT_ICON_BG = "ui/components/dc/atb/intent bg.png";
    public static final String TIME_BG = "ui/components/dc/atb/time bg.png";
    public static final String STATUS_EXPLORE ="ui/components/dc/status/status explore.png";
    public static final String STATUS_EXPLORE_DETECTED ="ui/components/dc/status/status_explore_detected.png";
    public static final String STATUS_COMBAT ="ui/components/dc/status/status_combat.png";
    public static final String STATUS_ALARM ="ui/components/dc/radial/examine.png";
    // public static final String STATUS_ALARM ="ui/components/dc/status/status_alarm.png";
    public static final String STATUS_PUZZLE ="ui/components/dc/status/puzzle.png";

    public static final String VC_BG = "ui/components/generic/dialogue/grunge bg full.png";
    // public static final String VC_BG = "ui/components/generic/vc/bg.png";
    public static final String VC_DECOR_GATE =  "ui/components/generic/vc/gate.png";
    public static final String VC_BOTTOM =  "ui/components/generic/vc/bottom.png";
    public static final String BLACK_250_350 =  "ui/components/generic/vc/black_250_350.png";
    public static final String HL_250_350 =  "ui/components/generic/vc/hl_250_350.png";
    public static final String TEXT_BORDER_DECOR = "ui/components/ninepatch/std/frame decor.png";
    public static final String PLATFORM_VESSEL =  "ui/cells/advanced/platform/visuals/vessel.png";
    public static final String PLATFORM_HORN = "ui/cells/advanced/platform/visuals/horn.png";
    public static final String PLATFORM_ISLAND = "ui/cells/advanced/platform/visuals/island.png";
    public static final String PLATFORM_ROCKS= "ui/cells/advanced/platform/visuals/rocks.png";
    public static final String BLOTCH = "ui/INK BLOTCH.png";
    public static final String BLOTCH_INVERT = "ui/INK BLOTCH INVERT.png";
    public static final String RUNE_CIRCLE = "sprites/boss/knight/runes.png";
    public static final String GLAIVE = "sprites/boss/knight/glaive.png";
    public static final String ARIUS_ORB = "sprites/boss/knight/stone.png";


    public static String getSketch(BACKGROUND background) {
        if (background == null) {
            return "";
        }
        String sketch=getSketchName(background);
        return PathFinder.getSketchPath()+
         sketch+
         ".png";
    }

    private static String getSketchName(BACKGROUND sub) {
        switch (sub) {
            case MAN_OF_KINGS_REALM:
            case WOMAN_OF_KINGS_REALM:
                return "dragoncrest";
            case MAN_OF_EAGLE_REALM:
            case WOMAN_OF_EAGLE_REALM:
                return "eagle";
            case MAN_OF_GRIFF_REALM:
            case WOMAN_OF_GRIFF_REALM:
                return "griff";
            case MAN_OF_RAVEN_REALM:
            case WOMAN_OF_RAVEN_REALM:
                return "raven";
            case MAN_OF_WOLF_REALM:
            case WOMAN_OF_WOLF_REALM:
                return "wolf";
            case DWARF:
            case STONESHIELD_DWARF:
            case IRONHELM_DWARF:
            case MOONSILVER_DWARF:
            case WILDAXE_DWARF:
                 
            case FROSTBEARD_DWARF:
                 
            case REDBLAZE_DWARF:
                 
            case GRIMBART_DWARF:
                 
            case WOLFSBANE_DWARF:
                 
            case RUNESMITH_DWARF:
                 
            case NORDHEIMER:
                return "hammer";
            case HIGH_ELF:
                 
            case FEY_ELF:
                 
            case GREY_ELF:
                 
            case DARK_ELF:
                 
            case WOOD_ELF:
                 
            case ELF:
                return "tree";
            case RED_ORC:

            case BLACK_ORC:

            case PALE_ORC:

            case GREEN_ORC:
            case VAMPIRE:
            case MAN_OF_EAST_EMPIRE:
                return "bats";
            case INFERI_CHAOSBORN:
                return "demon";
            case INFERI_HELLSPAWN:
                 
            case INFERI_WARPBORN:
                 

                 
        }
        return null;
    }

    public static String getDefaultSkillImage(String mastery) {
        return "gen/skill/mastery/"+mastery+".png";

    }

    public static String getByName(String path) {
        try {
            return (String) Images.class.getDeclaredField(path.toUpperCase().replace(" ", "_")).get(null);
        }  catch (NoSuchFieldException e) {
            main.system.auxiliary.log.LogMaster.log(1,"No such image field: " +path);
        }catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return null ;
    }

    public static List<String> getFieldsAsPaths() {
        return Arrays.stream(Images.class.getDeclaredFields()).map(field -> {
            try {
                return field.get(null).toString();
            } catch (IllegalAccessException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            return null;
        }).collect(Collectors.toList());
    }
}
