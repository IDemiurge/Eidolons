package libgdx.gui.overlay.choice;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMaster;
import libgdx.bf.SuperActor;
import eidolons.content.consts.GraphicData;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.LabelX;
import eidolons.content.consts.Images;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.AudioEnums;
import main.system.sound.SoundMaster;

public class VC_Item extends SuperActor {
    /*
    SELECT: 9patch hl
    HOVER: screen and fade out black overlay
     */
    private final VC_Option option;
    private final FadeImageContainer image;
    LabelX title;
    ImageContainer highlight, blackout;
    public boolean chosen;

    public VC_Item(VisualChoice container, VC_Option option) {
        setSize(280, 400);
        this.option = option;
        addActor(highlight = new ImageContainer(Images.HL_250_350));
        addActor(image = new FadeImageContainer(option.image));
        addActor(blackout = new ImageContainer(Images.BLACK_250_350));
        addActor(title= new LabelX(option.title, StyleHolder.getHqLabelStyle(20)));
        title.getColor().a=0;
        highlight.getColor().a=0;
        blackout.getColor().a=getDefaultBlack(option.checkDisabled());

        title.setX(GdxMaster.centerWidth(title));
        title.setY(21);
        // blackout.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.SUN);
        // highlight.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.SUN);
        addListener(new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (option.checkDisabled()) {
                    SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__CLICK_DISABLED);
                    return;
                }
                container.setSelected(option);
                container.showDescription(option.tooltip);
                highlight.fadeIn();
                blackout.fadeOut();
                chosen = true;
                GuiEventManager.triggerWithParams
                        (GuiEventType.GRID_SCREEN, this, new GraphicData("dur:0.25f;alpha:0.9f;"));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (option.checkDisabled()) {
                    return;
                }
                ActionMaster.addFadeInAction(title, 1.5f);
                blackout.fadeTo(0.1f, 1.1f);
                GuiEventManager.triggerWithParams
                        (GuiEventType.GRID_SCREEN, image, new GraphicData("dur:1.5f;alpha:0.5f;"));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (option.checkDisabled()) {
                    return;
                }
                reset();
            }
        });
    }

    public void reset() {
           blackout.fadeTo(getDefaultBlack(option.checkDisabled()), 0.1f);
    }

    private float getDefaultBlack(boolean checkDisabled) {
        return  checkDisabled? 0.7f : 0.3f;
    }
}
