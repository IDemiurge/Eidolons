package eidolons.game.netherflame.igg;

import eidolons.game.EidolonsGame;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.content.enums.DungeonEnums;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

public class IGG_Images {
    public static final String SHADOW = PathFinder.getArtFolder() + "Shadow.jpg";
    public static final String INSCRIPTION =   "sprites/bf/hanging/rune inscription_09.png";

    public static String getBackground() {
        return getBackground(IGG_Demo.MISSION);
    }
        public static String getBackground(IGG_Demo.IGG_MISSION mission) {
        String path=null ;
            if (EidolonsGame.BOSS_FIGHT) {
                return Sprites.BG_DEFAULT;
            }
        if (!CoreEngine.isLiteLaunch() && !OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.BACKGROUND_SPRITES_OFF)) {
            switch (mission) {
                case TUTORIAL:
                    path = Sprites.BG_GATEWAY;
                    break;
                case ACT_I_MISSION_I:
                    path = Sprites.BG_VALLEY;
                    break;
                case ACT_I_MISSION_II:
                case ACT_I_BOSS:
                    path = Sprites.BG_DUNGEON;
                    break;
                case ACT_II_MISSION_I:
                case ACT_II_BOSS:
                    path = Sprites.BG_BASTION;
                    break;
                case FINALE:
                    break;
            }
        } else {
            switch (mission) {
                case ACT_I_MISSION_I:
                    path = DungeonEnums.MAP_BACKGROUND.TOWER.getBackgroundFilePath();
                    break;
                case ACT_I_MISSION_II:
                case ACT_I_BOSS:
                    path = DungeonEnums.MAP_BACKGROUND.TUNNEL.getBackgroundFilePath();
                    break;
                case ACT_II_MISSION_I:
                    path = DungeonEnums.MAP_BACKGROUND.BASTION_DARK.getBackgroundFilePath();
                    break;
                case ACT_II_BOSS:
                    path = DungeonEnums.MAP_BACKGROUND.BASTION.getBackgroundFilePath();
                    break;
                case FINALE:
                    break;
            }
        }
        return path;
    }

    public enum MAIN_ART {
        HALL2,
        HALL3,
        ;

        String path;


        MAIN_ART(String path) {
            this.path = path;
        }
        public String getPath() {
            return PathFinder.getArtFolder() + "/" + path;
        }

        MAIN_ART() {
            path = StringMaster.getWellFormattedString(name()) + ".jpg";
        }
    }

    public enum PROMO_ART {
        THE_HALL,
        ;

        String path;

        public String getPath() {
            return PathFinder.getArtFolder() + "/promo/" + path;
        }

        PROMO_ART() {
            path = StringMaster.getWellFormattedString(name()) + ".png";
        }

        PROMO_ART(String path) {
            this.path = path;
        }
    }

    public enum BRIEF_ART {
        LEVI_FIGHT, EIDOLONS_CENTER, APHOLON,
        ENTER_GATE
        , RITUAL
        , GATE,
        STONE_WARDEN,
        HARVESTER,
        BLACK_WATERS,
        SENTRIES,
        APHOLON_SMALL
        ;
        String path;

        public String getPath() {
            return PathFinder.getArtFolder() + "/brief/" + path;
        }

        BRIEF_ART() {
            path = StringMaster.getWellFormattedString(name()) + ".png";
        }

        BRIEF_ART(String path) {
            this.path = path;
        }
    }

    public enum HERO_ART {
        GRIMBART_128,
        GWYN_128,
        GORR_128
        ;

        public String getPath() {
            return "demo/heroes/" + StringMaster.getWellFormattedString(name())+".png";
        }
    }
}
