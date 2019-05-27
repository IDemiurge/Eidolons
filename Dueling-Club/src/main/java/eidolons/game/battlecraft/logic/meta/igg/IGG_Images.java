package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.libgdx.texture.Sprites;
import main.content.enums.DungeonEnums;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.io.File;

public class IGG_Images {
    public static final String SHADOW = PathFinder.getArtFolder() + "Shadow.jpg";

    public static String getBackground() {
        String path=null ;
        if (!CoreEngine.isLiteLaunch()) {
            switch (IGG_Demo.MISSION) {
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
            switch (IGG_Demo.MISSION) {
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
        MAIN_MENU("MAIN_MENU.png"),
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
        , GATE
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
}
