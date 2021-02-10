package eidolons.game.netherflame.main;

import eidolons.libgdx.texture.Sprites;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

public class NF_Images {
    public static final String SHADOW = PathFinder.getArtFolder() + "Shadow.jpg";
    public static final String INSCRIPTION =   "sprites/bf/hanging/rune inscription_09.png";

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
            path = StringMaster.format(name()) + ".jpg";
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
            path = StringMaster.format(name()) + ".png";
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
            path = StringMaster.format(name()) + ".png";
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
            return "demo/heroes/" + StringMaster.format(name())+".png";
        }
    }
}
