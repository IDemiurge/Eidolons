package eidolons.libgdx.gui.menu.selection.town.quest;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster.BUTTON_SOUND_MAP;

/**
 * Created by JustMe on 10/5/2018.
 * <p>
 * separate info:
 * <p>
 * reward
 * objective
 * description
 */
public class QuestInfoPanel extends ItemInfoPanel {
    public static final int WIDTH = 920;
    public static final int HEIGHT = 720;
    private final SmartButton accept;
    private final SmartButton cancel;
    private boolean disabled;

    public QuestInfoPanel(SelectableItemData o) {
        super(o);
        row();
        addActor(accept = new SmartButton("Accept Quest",
         STD_BUTTON.MENU, () -> accept()) {
            @Override
            protected BUTTON_SOUND_MAP getSoundMap() {
                return BUTTON_SOUND_MAP.OK;
            }
        });
        addActor(cancel = new SmartButton("Cancel Quest",
         STD_BUTTON.MENU, () -> cancel()) {
            @Override
            protected BUTTON_SOUND_MAP getSoundMap() {
                return BUTTON_SOUND_MAP.CANCEL;
            }
        });

    }

    @Override
    protected String getEmptyImagePath() {
        return ImageManager.getEmptyUnitIconPath();
    }

    @Override
    protected String getEmptyImagePathFullSize() {
        return ImageManager.getEmptyUnitIconFullSizePath();
    }

    @Override
    public void layout() {
        super.layout();
        accept.setPosition(GdxMaster.centerWidth(accept) + GdxMaster.adjustWidth(200), NINE_PATCH_PADDING.SAURON.bottom);
        cancel.setPosition(GdxMaster.centerWidth(cancel) + GdxMaster.adjustWidth(200), NINE_PATCH_PADDING.SAURON.bottom);
    }

    protected void initSize() {
        setSize(GdxMaster.adjustSize(WIDTH), GdxMaster.adjustSize(HEIGHT));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        cancel.setVisible(disabled);
        accept.setVisible(!disabled);
    }

    @Override
    protected String getTitle() {
        return super.getTitle();
    }

    private void accept() {
        GuiEventManager.trigger(GuiEventType.QUEST_TAKEN, getItem().getName());
    }

    private void cancel() {
        GuiEventManager.trigger(GuiEventType.QUEST_CANCELLED, getItem().getName());
    }

    @Override
    protected void initHeader(TablePanel<Actor> header) {
        super.initHeader(header);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        accept.setDisabled(disabled);
    }
}
