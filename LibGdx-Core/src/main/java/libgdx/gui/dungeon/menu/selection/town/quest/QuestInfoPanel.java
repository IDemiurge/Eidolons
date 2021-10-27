package libgdx.gui.dungeon.menu.selection.town.quest;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.menu.selection.ItemInfoPanel;
import libgdx.gui.dungeon.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.TiledNinePatchGenerator;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.images.ImageManager;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.BUTTON_SOUND_MAP;

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
    private final SmartTextButton accept;
    private final SmartTextButton cancel;
    private boolean disabled;
    private SelectableItemData chosen;

    public SelectableItemData getChosen() {
        return chosen;
    }

    public QuestInfoPanel(SelectableItemData o) {
        super(o);
        row();
        addActor(accept = new SmartTextButton("Accept Quest",
         ButtonStyled.STD_BUTTON.MENU, this::accept) {
            @Override
            public BUTTON_SOUND_MAP getSoundMap() {
                return AudioEnums.BUTTON_SOUND_MAP.OK;
            }
        });
        addActor(cancel = new SmartTextButton("Cancel Quest",
         ButtonStyled.STD_BUTTON.MENU, this::cancel) {
            @Override
            public BUTTON_SOUND_MAP getSoundMap() {
                return AudioEnums.BUTTON_SOUND_MAP.CANCEL;
            }
        });


        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED, p -> {
            chosen = null;
            setDisabled(false);
        });
    }

    @Override
    protected float getDescriptionWidth() {
//        return 920 * 0.76f;
        return GDX.width(WIDTH*0.76f);
    }

    @Override
    protected float getDescriptionHeight() {
        return GDX.height(HEIGHT*0.5f);
    }

    @Override
    protected String getEmptyImagePath() {
        return ImageManager.getEmptyUnitIconPath();
    }

    //
    @Override
    protected String getEmptyImagePathFullSize() {
        return ImageManager.getReallyEmptyUnitIconFullSizePath();
    }

    @Override
    protected void afterLayout() {
        super.afterLayout();
        accept.setPosition(GdxMaster.centerWidth(accept) + GdxMaster.adjustWidth(200), TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
        cancel.setPosition(GdxMaster.centerWidth(cancel) + GdxMaster.adjustWidth(200), TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
        description.setWidth(getDescriptionWidth());
    }

    protected void initSize() {
        setSize(GdxMaster.adjustWidth(WIDTH), GdxMaster.adjustHeight(HEIGHT));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        disabled = chosen != null;
        cancel.setVisible(disabled);
        accept.setVisible(!disabled);
        accept.setChecked(false);
        cancel.setChecked(false);
    }

    @Override
    protected String getTitle() {
        return super.getTitle();
    }

    private void accept() {
        chosen = getItem();
        GuiEventManager.trigger(GuiEventType.QUEST_TAKEN, getItem().getName());
    }

    private void cancel() {
        if (chosen == null) {
            return;
        }
        GuiEventManager.trigger(GuiEventType.QUEST_CANCELLED, chosen.getName());
        chosen = null;
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
        //        accept.getClickListener()
    }
}
