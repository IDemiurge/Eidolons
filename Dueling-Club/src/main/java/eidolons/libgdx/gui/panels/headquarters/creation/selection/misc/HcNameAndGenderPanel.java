package eidolons.libgdx.gui.panels.headquarters.creation.selection.misc;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.core.EUtils;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionTable;
import eidolons.libgdx.texture.Images;
import eidolons.system.text.NameMaster;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/25/2018.
 */
public class HcNameAndGenderPanel extends SelectionTable<SmartTextButton> {

    public HcNameAndGenderPanel() {
        super(2, 36);
        EUtils.bind(GuiEventType.HC_GENDER_CHOSEN, p -> {
            randomize();
        });
        setUpdateRequired(true);
    }

    private void randomize() {
        setUpdateRequired(true);
    }

    @Override
    public void update() {
        randomize();
    }

    @Override
    public void updateAct(float delta) {
        if (isUpdateRequired()) {
            super.updateAct(delta);
            setUpdateRequired(false);
        }
    }

    @Override
    public void init() {
        TablePanelX<Actor> upper = new TablePanelX<>();
        add(upper).colspan(wrap);
        upper.add(new SmartTextButton("Male",
         StyleHolder.getHqTextButtonStyle(STD_BUTTON.TAB_HIGHLIGHT, 22), () -> {
            HeroCreationMaster.modified(G_PROPS.GENDER, "Male");
            randomize();
        }, STD_BUTTON.MENU)).top();
        upper.add(new SmartTextButton("Female",
         StyleHolder.getHqTextButtonStyle(STD_BUTTON.TAB_HIGHLIGHT, 22), () -> {
            HeroCreationMaster.modified(G_PROPS.GENDER, "Female");
            randomize();
        }, STD_BUTTON.MENU)).top();
        row();
        add(new ImageContainer(Images.SEPARATOR)).colspan(wrap);
        row();
        if (HeroCreationMaster.getModel().getGender() == null) {
            return;
        }
        super.init();
        add(new ImageContainer(Images.SEPARATOR)).colspan(wrap);
        row();
        add(new SmartTextButton("Randomize", STD_BUTTON.MENU, this::randomize)).colspan(3);
    }

    protected int getDynamicWrap(int i) {
        return 0;// (i+1)%2; wanted to interleave 3 and 2 column rows
    }

    @Override
    public void setUserObject(Object userObject) {

    }

    @Override
    protected SelectableItemData[] initDataArray() {

        data = new SelectableItemData[size];
        ObjType type = HeroCreationMaster.getModel().getType();
        for (int i = 0; i < size; i++) {
            data[i] = new SelectableItemData(
             NameMaster.generateName(type), type);
        }
        return data;
    }

    @Override
    protected SmartTextButton createElement(SelectableItemData datum) {
        SmartTextButton btn = new SmartTextButton(datum.getName(), STD_BUTTON.TAB_HIGHLIGHT, () -> {
        });
        btn.setRunnable(() -> {
            selected(datum);
            for (SmartTextButton actor : actors) {
                if (actor != btn)
                    actor.setChecked(false);
            }
        });

        return btn;
    }

    @Override
    protected void selected(SelectableItemData item) {
        super.selected(item);
    }

    @Override
    protected GuiEventType getEvent() {
        return null;
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.NAME;
    }


    @Override
    protected SmartTextButton[] initActorArray() {
        return new SmartTextButton[size];
    }
}
