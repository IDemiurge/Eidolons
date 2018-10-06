package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

public class ButtonStyled extends ImageContainer implements EventListener {

    Runnable runnable;

    public ButtonStyled(STD_BUTTON b, Runnable runnable) {
        this(b);
        this.runnable = runnable;
        addListener(this);
    }

    public ButtonStyled(STD_BUTTON b) {
        super(b.path);
        initResolutionScaling();
    }

    public ButtonStyled(String name) {
//         setStyle();
        setName(name);
    }

    @Override
    public boolean handle(Event e) {
        if (runnable == null)
            return true;
        if (!(e instanceof InputEvent)) return false;
        InputEvent event = (InputEvent) e;
        if (event.getType() == Type.touchDown) {
            runnable.run();
        }
        return true;
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
         "buttons" , "question.png")),

        UNARMED(StrPathBuilder.build(PathFinder.getUiPath(),
         "components","dc" ,          "quick weapon" ,"unarmed.png")),
        PULL(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "generic",
          "buttons" ,
          "special" ,
          "pull.png")),

        OPTIONS(VISUALS.MENU_BUTTON.getImgPath()) {
            @Override
            public boolean isVersioned() {
                return false;
            }
        },
        //        NEXT, LEVEL_UP,
        PAUSE(StrPathBuilder.build(PathFinder.getMacroUiPath(), "components", "time panel", "pause.png")),
        SPEED_UP(StrPathBuilder.build(PathFinder.getMacroUiPath(), "components", "time panel", "SPEED UP.png")),
        SPEED_DOWN(StrPathBuilder.build(PathFinder.getMacroUiPath(), "components", "time panel", "SPEED DOWN.png")),

        STAT(StrPathBuilder.build(PathFinder.getUiPath(),
         "components", "hq" ,"stats" ,"cross.png")),
        HIGHLIGHT(StrPathBuilder.build(PathFinder.getComponentsPath(), "generic", "tabs", "highlight.png")),
        SPELLBOOK(StrPathBuilder.build(PathFinder.getComponentsPath(), "dc",
         "bottom panel", "spellbook btn.png")),
        INV(StrPathBuilder.build(PathFinder.getComponentsPath(), "dc",
         "bottom panel", "INV btn.png"))
        ;
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
            return TextureCache.getOrCreateTextureRegionDrawable(StringMaster.getAppendedImageFile(path, " down"));
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
