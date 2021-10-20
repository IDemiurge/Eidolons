package libgdx.gui.generic.btn;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.tooltips.SmartClickListener;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import libgdx.gui.tooltips.SmartClickListener;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

public class ButtonStyled extends ImageContainer {

    Runnable runnable;

    public ButtonStyled(STD_BUTTON b, Runnable runnable) {
        this(b);
        this.runnable = runnable;
        addListener(new SmartClickListener(this) {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            runnable.run();
                        }
                    }
        );


    }

    public ButtonStyled(STD_BUTTON b) {
        super(b.path);
        initResolutionScaling();
    }



//    @Override
//    public void setDisabled(boolean isDisabled) {
//        super.setDisabled(isDisabled);
//    }

    public enum STD_BUTTON {
        OK(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" ,
         "ok.png")),
        CANCEL(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" ,
         "no.png")),
        UNDO(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" ,
         "back.png")),
        UP(StrPathBuilder.build(PathFinder.getUiPath(),
                "components", "generic",
                "buttons" ,
                "up.png")),
        NEXT(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" ,
         "forward.png")),

        MENU(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" ,
         "button.png")),
        HELP(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" , "question.png"))
        ,

        UNARMED(StrPathBuilder.build(PathFinder.getUiPath(),
         "components","dc" ,          "quick weapon" ,"unarmed.png")),
        PULL(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
          "buttons" ,"special" ,"pull.png")),

        OPTIONS(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
         "buttons" ,"special" ,"menu.png")),

        ITEM_ALL(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "tiny", "all.png")),
        ITEM_WEAPONS(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "tiny", "WEAPONS.png")),
        ITEM_ARMOR(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "tiny", "ARMOR.png")),
        ITEM_USABLE(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "tiny", "usable.png")),
        ITEM_JEWELRY(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "tiny", "JEWELRY.png")),
        ITEM_QUEST(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "tiny", "quest items.png")),

        //        NEXT, LEVEL_UP,
        PAUSE(StrPathBuilder.build(PathFinder.getMacroUiPath(), "components", "time panel", "pause.png")),
        SPEED_UP(StrPathBuilder.build(PathFinder.getMacroUiPath(), "components", "time panel", "SPEED UP.png")),
        SPEED_DOWN(StrPathBuilder.build(PathFinder.getMacroUiPath(), "components", "time panel", "SPEED DOWN.png")),

        STAT(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "hq" ,"stats" ,"cross.png")),
        HIGHLIGHT_ALT(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic", "tabs", "alt" ,
                "highlight.png")),
        TAB_HIGHLIGHT(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic", "tabs", "highlight.png")),
        TAB_HIGHLIGHT_COLUMN(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic", "tabs/opaque", "highlight.png")),

        EXTRA_ATK(StrPathBuilder.build(PathFinder.getComponentsPath(), "dc",
                "bottom panel", "atks.png")),
        EXTRA_MOVES(StrPathBuilder.build(PathFinder.getComponentsPath(), "dc",
                "bottom panel", "moves.png")),

        SPELLBOOK(StrPathBuilder.build(PathFinder.getComponentsPath(), "dc",
         "bottom panel", "spellbook btn.png")),
        INV(StrPathBuilder.build(PathFinder.getComponentsPath(), "dc",
         "bottom panel", "INV btn.png")),
        EYE(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic",
         "buttons", "special", "eye.png")),
        CIRCLE(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic",
         "buttons","special", "circle.png")),
        CHEST(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic",
         "buttons","special", "chest.png")),
        REPAIR(
          PathFinder.getUiPath()+
         "components/hq/inv/repair.png"),
        LORD_BTN(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic",
                "buttons","special", "lord.png")),
        PALE_BTN(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic",
                "buttons","special", "pale.png")),
        SOULS_BTN(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic",
                "buttons","special", "souls.png")),

        BUTTON_ZARK("ui/components/generic/buttons/zark/std/btn.png"),
        BUTTON_ZARK_SMALL("ui/components/generic/buttons/zark/small/btn.png"),

        LE_UNDO("ui/level_editor/anew/buttons/undo.png"),
        LE_REDO("ui/level_editor/anew/buttons/redo.png"),
        LE_CTRL("ui/level_editor/anew/buttons/control.png"),
        LE_PALETTE("ui/level_editor/anew/buttons/palette.png"),
        LE_STRUCT("ui/level_editor/anew/buttons/struct.png"),
        LE_BRUSH("ui/level_editor/anew/buttons/brush.png"),
        LE_VIEWS("ui/level_editor/anew/buttons/view.png"),
        LE_AV("ui/forge4.png"),
        ARROW(Images.ROLL_ARROW), EMPTY("");

        String path;
        private Drawable texture;

        STD_BUTTON(String path) {
            this.path = path;
        }

        public boolean isVersioned() {
            return true;
        }

        public String getPath() {
            return path;
        }

        public Drawable getTexture() {
            if (texture == null) {
                texture = TextureCache.getOrCreateTextureRegionDrawable(path);
            }
            return texture;
        }

        public Drawable getTextureDown() {
            return TextureCache.getOrCreateTextureRegionDrawable(StringMaster.getTextureDown (path ));
        }

        public Drawable getTextureOver() {
            return TextureCache.getOrCreateTextureRegionDrawable(StringMaster.getAppendedImageFile(path, " over"));
        }

        public Drawable getTextureDisabled() {
            return TextureCache.getOrCreateTextureRegionDrawable(StringMaster.getAppendedImageFile(path,
             " disabled"));

        }

        public Drawable getTextureChecked() {
            return TextureCache.getOrCreateTextureRegionDrawable(StringMaster.getAppendedImageFile(path,
             " checked", true));
        }
        public Drawable getTextureCheckedOver() {
            return TextureCache.getOrCreateTextureRegionDrawable(StringMaster.getAppendedImageFile(path,
             " checked over", true));
        }
    }
}
