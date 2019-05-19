package eidolons.game.battlecraft.logic.meta.igg;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

import java.io.File;

public class IGG_Images {
    public static final String SHADOW = PathFinder.getArtFolder() + "Shadow.jpg";

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
        ENTER_GATE, RITUAL;
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
