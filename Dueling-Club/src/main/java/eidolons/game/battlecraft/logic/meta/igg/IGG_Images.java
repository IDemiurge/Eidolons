package eidolons.game.battlecraft.logic.meta.igg;

import main.data.filesys.PathFinder;

public class IGG_Images {
    public static final String SHADOW = PathFinder.getArtFolder()+"Shadow.jpg";

    public enum PROMO_ART{
        THE_HALL,
        ;

        String path;

        public String getPath() {
            return PathFinder.getArtFolder()+"/promo/"+ path;
        }

        PROMO_ART() {
            path= (name())+".png";
        }

        PROMO_ART(String path) {
            this.path = path;
        }
    }
    public enum BRIEF_ART{
        LEVI_FIGHT, EIDOLONS_CENTER, APHOLON
;
        String path;

        public String getPath() {
            return PathFinder.getArtFolder()+"/brief/"+ path;
        }

        BRIEF_ART() {
            path= (name())+".png";
        }

        BRIEF_ART(String path) {
            this.path = path;
        }
    }
}
