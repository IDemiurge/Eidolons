package eidolons.game.netherflame.main.soul.panel.sub.imbue;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.netherflame.main.soul.eidola.EidolonImbuer;
import eidolons.game.netherflame.main.soul.eidola.Soul;
import eidolons.game.netherflame.main.soul.panel.LordPanel;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.enums.GenericEnums;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ArrayMaster;
import main.system.graphics.FontMaster;

public class ImbuePanel extends TablePanelX {
    String tooltip = "Imbue\n" +
            "Select up to 4 souls to imbue the selected item with special traits. \n" +
            "Each trait is linked to Eidolon Aspects, so the more value the chosen souls have in its Aspects, the more likely your item is to receive it. \n" +
            "Experiment to find out how they are linked! \n" +
            "You can destroy imbued items to gain Soulforce - 80% of that you would gain if you Consumed the souls used in its creation.";
    private  SmartButton imbueBtn;
    private EidolonImbuer imbuer;
    ImbueSoulSlots soulSlots;
    ImbueItems items;
    ImbueItemInfo itemInfo;
    ImbueTraitsInfo traitsInfo;

    public ImbuePanel() {
        super(900, 800);
        imbuer = new EidolonImbuer();

        TablePanelX  table = createInnerTable();
        table.setFixedMaxSize(true);
        table.setFixedMinSize(true);

        table.add( soulSlots = new ImbueSoulSlots(this)).center().row();
        table.add(traitsInfo = new ImbueTraitsInfo()).center().row();
        table.add(imbueBtn = new SmartButton("Imbue", ButtonStyled.STD_BUTTON.MENU, () ->
        {
             imbuer.imbue(getSelectedItem(), getSouls());
            GuiEventManager.trigger(GuiEventType.SHOW_FULLSCREEN_ANIM,
                    new FullscreenAnimDataSource(FullscreenAnims.FULLSCREEN_ANIM.GATES, 1,
                            FACING_DIRECTION.NORTH, GenericEnums.BLENDING.SCREEN));
            soulSlots.resetSouls();
            LordPanel.getInstance().update();
        }

        )).row();
        table.add( items = new ImbueItems(this)).bottom();

        add(table);
        add(itemInfo = new ImbueItemInfo()).row();
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
    }

    @Override
    public void layout() {
        super.layout();
        items.setY(imbueBtn.getY()-100-items.getHeight());
        itemInfo.setX(items.getX()+items.getWidth()-100);
    }

    private Soul[] getSouls() {
        return soulSlots.getSouls();
    }

    private DC_HeroItemObj getSelectedItem() {
        return items.getSelected();
    }

    public void addSoul(Soul soul) {
        soulSlots.addSoul(soul);
    }


    @Override
    public void update() {
        setUserObject(getUserObject());
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        itemInfo.setUserObject(getSelectedItem());
    }

    private String initTraitInfo() {
        return tooltip;
    }

    private String initAspectInfo() {
        String info = "\n" + EidolonImbuer.getAspects(getSouls());

        return info;
    }

    public EidolonImbuer getImbuer() {
        return imbuer;
    }

    public ImbueSoulSlots getSoulSlots() {
        return soulSlots;
    }

    public ImbueItems getItems() {
        return items;
    }

    public ImbueItemInfo getItemInfo() {
        return itemInfo;
    }

    public ImbueTraitsInfo getTraitsInfo() {
        return traitsInfo;
    }





    private class ImbueTraitsInfo extends TablePanelX {
        LabelX aspectInfo;
        LabelX traitInfo;

        public ImbueTraitsInfo() {
            super(600, 300);
            setBackground(NinePatchFactory.getHqDrawable());

            Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 19);
            add(  new LabelX("Eidolon Aspects", StyleHolder.getHqLabelStyle(18))).center().row();
            add(aspectInfo = new LabelX("", style)).center().row();
            add(traitInfo = new LabelX(initTraitInfo(), style).width(600)).width(600).growY().row();
            traitInfo.setWrap(true);
        }

        @Override
        public void updateAct(float delta) {
            super.updateAct(delta);
            imbueBtn.setDisabled(getSelectedItem()==null
            || !ArrayMaster.isNotEmpty(getSouls()));

            aspectInfo.setText(initAspectInfo());
            traitInfo.setText(initTraitInfo());
        }
    }

}
