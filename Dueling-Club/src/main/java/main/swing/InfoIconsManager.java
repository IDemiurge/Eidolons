package main.swing;

import main.entity.obj.Obj;
import main.system.images.ImageManager;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class InfoIconsManager {

    public static Collection<? extends Image> getInfoIconsForObj(Obj obj) {

        List<Image> list = new LinkedList<Image>();

        for (INFO_ICONS icon : INFO_ICONS.values()) {
            if (checkIconConditions(icon, obj)) {
                list.add(getImage(icon));
            }
        }
        return list;
    }

    private static boolean checkIconConditions(INFO_ICONS icon, Obj obj) {
        switch (icon) {
            case CUSTOM:
                break;
            case DEAD:
                break;
            case DONE:
                break;
            case HIDDEN:
                break;
            case IMMOBILE:
                break;
            case TRAP:
                break;
            default:
                break;

        }
        return false;
    }

    private static Image getImage(INFO_ICONS icon) {
        return ImageManager.getImage(icon.getImagePath());
    }

    public enum INFO_ICONS {
        DONE,
        HIDDEN,
        DEAD,
        TRAP,
        IMMOBILE,
        CUSTOM

        // STRING IMG

        ;
        private boolean ownerVisionOnly;
        private String imagePath;
        private int x_alignment;
        private int y_alignment;

        INFO_ICONS() {

        }

        INFO_ICONS(boolean ownerVisionOnly, String imagePath) {
            this.setImagePath(imagePath);
            this.setOwnerVisionOnly(ownerVisionOnly);
        }

        public boolean isOwnerVisionOnly() {
            return ownerVisionOnly;
        }

        public void setOwnerVisionOnly(boolean ownerVisionOnly) {
            this.ownerVisionOnly = ownerVisionOnly;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }
}
