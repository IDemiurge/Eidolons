package eidolons.libgdx.gui.generic.btn;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

public class ButtonStyled extends Image implements EventListener {

    Runnable runnable;

    public ButtonStyled(STD_BUTTON b, Runnable runnable) {
        this(b);
        this.runnable = runnable;
        addListener(this);
    }

    public ButtonStyled(STD_BUTTON b) {
        super(TextureCache.getOrCreate(b.path)
//         !b.isVersioned() ?
//          new Image(
//           TextureCache.getOrCreate(b.path))
//          :null ,
//
//          b.isVersioned() ?
//           StyleHolder.getCustomButtonStyle(b.path)
//           : StyleHolder.getTextButtonStyle()
        );
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
        OK("UI/components/generic/buttons/ok.png"),
        CANCEL("UI/components/generic/buttons/no.png"),
        UNDO("UI/components/generic/buttons/back.png"),
        NEXT("UI/components/generic/buttons/forward.png"),
        MENU("UI/components/generic/buttons/button.png"),
        UNARMED("UI/components/dc/quick weapon/unarmed.png"),

        HELP("UI/components/generic/buttons/question.png") ,
        OPTIONS(VISUALS.MENU_BUTTON.getImgPath()) {
            @Override
            public boolean isVersioned() {
                return false;
            }
        },
        //        NEXT, LEVEL_UP,
        PAUSE(StrPathBuilder.build(PathFinder.getMacroUiPath(), "component", "time panel", "pause.png")),
        SPEED_UP(StrPathBuilder.build(PathFinder.getMacroUiPath(), "component", "time panel", "SPEED UP.png")),
        SPEED_DOWN(StrPathBuilder.build(PathFinder.getMacroUiPath(), "component", "time panel", "SPEED DOWN.png")),
        STAT("UI\\components\\hq\\stats\\cross.png"),
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
