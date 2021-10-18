package libgdx.gui.overlay.choice;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import eidolons.game.core.EUtils;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.LabelX;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.TablePanelX;
import libgdx.stage.OverlayPanel;
import eidolons.content.consts.Images;
import eidolons.content.consts.Sprites;
import libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

public class VisualChoice extends TablePanelX implements OverlayPanel {

    private final TablePanelX moving;
    SpriteX background, blotchTitle, blotchDescr;
    GroupX choicePanel;
    FadeImageContainer staticBackground, moveBtn, bottomHeader, decor;
    LabelX title, descr;
    SymbolButton ok;
    //light_ray

    private final List<VC_Item> items = new ArrayList<>();
    private VC_Option selected;
    private final int itemWidth=250;
    private final TablePanelX descrContainer;

    public VisualChoice() {
       addActor(background = new SpriteX(Sprites.BG_DEFAULT));
        addActor(staticBackground = new FadeImageContainer(Images.VC_BG));
        addActor(bottomHeader = new FadeImageContainer(Images.VC_BOTTOM));
        addActor(decor = new FadeImageContainer(Images.VC_DECOR_GATE));
        addActor(blotchTitle = new SpriteX(Sprites.INK_BLOTCH));
        addActor(blotchDescr = new SpriteX(Sprites.INK_BLOTCH));
        blotchDescr.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        blotchTitle.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        addActor(title = new LabelX("Title", StyleHolder.getAVQLabelStyle(25)));
        addActor(descrContainer = new TablePanelX( ));
        descrContainer. addActor(descr = new LabelX("", StyleHolder.getHqLabelStyle(19)));
        addActor(moving = new TablePanelX());
        moving.addBackgroundActor(moveBtn = new FadeImageContainer(ButtonStyled.STD_BUTTON.MENU.getPath()));
        moving.addActor(ok = new SymbolButton(ButtonStyled.STD_BUTTON.OK, () -> {
            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.VISUAL_CHOICE, selected.arg);
            done();
        }
        ));
        addActor(choicePanel = new GroupX());
        GuiEventManager.bind(GuiEventType.VISUAL_CHOICE, p -> choice((VC_DataSource) p.get()));

        setSize(GdxMaster.getWidth(), GdxMaster.getHeight()  );
        GdxMaster.center(this);

        ok.setY(-5);
        GdxMaster.right(ok);

        blotchTitle.setRotation(90);
        blotchDescr.setRotation(90);
        blotchTitle. setPosition(GdxMaster.centerWidth(blotchDescr),
                GdxMaster.getHeight()-300);
        blotchDescr. setPosition(GdxMaster.centerWidth(blotchDescr),
                211);
        background.setFps(15);

        background.setPosition(background.getWidth()/2, background.getHeight()/2);

        choicePanel.setSize(staticBackground.getWidth(), staticBackground.getHeight());
        descrContainer.setSize(bottomHeader.getWidth(), bottomHeader.getHeight());
        GdxMaster.center(staticBackground);
        GdxMaster.center(choicePanel);
        decor.setX(GdxMaster.centerWidth(decor));
         GdxMaster.top(decor);
         GdxMaster.top(title);
        bottomHeader.setPosition(GdxMaster.centerWidth(bottomHeader), 190);
        descrContainer.setX(GdxMaster.centerWidth(descrContainer));
        descrContainer.setBackground(TextureCache.getOrCreateTextureRegionDrawable(Images.TEXT_BORDER_DECOR));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    public void setSelected(VC_Option selected) {
        this.selected = selected;
        int i = getUserObject().options.indexOf(selected);
        moveSelectionTo(i);

    }

    private void moveSelectionTo(int i) {
        float x=getPosX(i);
        ActionMasterGdx.addMoveToAction(moving, x, moving.getY(), 2f);
    }


    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        // userObject.type.switch bg
        items.clear();
        for (VC_Option option : getUserObject().options) {
            VC_Item item = new VC_Item(this, option);
            choicePanel.addActor(item);
            items.add(item);
        }
        title.setText(getUserObject().type.getTitle());
        title.setX(GdxMaster.centerWidth(title));
        fadeIn();
        animate(false);
        EUtils.waitAndRun(2000, this::show);
    }

    @Override
    public VC_DataSource getUserObject() {
        return (VC_DataSource) super.getUserObject();
    }

    public void choice(VC_DataSource dataSource) {
        setUserObject(dataSource);
    }

    private float getPosX(int i) {
        int middle = items.size() / 2 + 1;
        int dif =(middle - i);
        int middleX = (GdxMaster.getWidth() - itemWidth) / 2; //where middle item is placed
        int gap = (GdxMaster.getWidth()-150) / (items.size()+1)/ (items.size()+1);
        return middleX+ (itemWidth + gap) * dif;

    }
    @Deprecated
    public void animate(boolean out) {
        float v = getAnimDuration();
        float y = 0;
        int index = 0;
        int middle = items.size() / 2 + 1;
        int middleX = (GdxMaster.getWidth() - itemWidth) / 2;

        for (VC_Item item : items) {
            float x1 = index>middle ? getPosX(index-1): getPosX(index+1);
            if (index==middle) {
                x1=middleX;
            }
            float x = getPosX(index++);
            if (out) {
                float buff = x;
                x = x1;
                x1 = buff;
            }
            item.setPosition(x, y);
            if (item.chosen) {
                MoveToAction moveToAction = ActionMasterGdx.addMoveToAction(item, x1, y, v);
                moveToAction.setInterpolation(Interpolation.sine);
            }

            if (out) {
                if (item.chosen) {
                    //retain!
                } else
                    item.fadeOut();
            } else {
                item.reset();
            }
        }
    }

    private float getAnimDuration() {
        return 3;
    }


    public void done() {
        //wait for anims to finish
        fadeOut();
        animate(true);
        EUtils.waitAndRun(2000, this::hide);
    }

    public VC_Option getSelected() {
        return selected;
    }

    public void showDescription(String tooltip) {
        descrContainer.fadeOut();
        ActionMasterGdx.addAfter(descrContainer, new Action() {
            @Override
            public boolean act(float delta) {
                descr.setText(tooltip);
                GdxMaster.center(descr);
                descrContainer.fadeIn();
                return true;
            }
        });
    }

    @Override
    public boolean keyDown(int keyCode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        return true;
    }
}








