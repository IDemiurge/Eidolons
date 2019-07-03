package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.imbue;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.EidolonImbuer;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.Soul;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.auxiliary.data.ArrayMaster;

public class ImbuePanel extends TablePanelX {
    private  SmartButton imbueBtn;
    private EidolonImbuer imbuer;
    ImbueSoulSlots soulSlots;
    ImbueItems items;
    ImbueItemInfo itemInfo;
    ImbueTraitsInfo traitsInfo;

    public ImbuePanel() {
        super(700, 900);
        imbuer = new EidolonImbuer();

        add( soulSlots = new ImbueSoulSlots(this)) ;
        add(itemInfo = new ImbueItemInfo()).row();

        add( items = new ImbueItems());
        add(traitsInfo = new ImbueTraitsInfo());
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
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

    private class ImbueTraitsInfo extends TablePanelX {
        LabelX aspectInfo;
        LabelX traitInfo;

        public ImbueTraitsInfo() {
            super(400, 500);
            setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
            Label.LabelStyle style = StyleHolder.getDefaultLabelStyle();
            add(aspectInfo = new LabelX("", style)).center().row();
            add(traitInfo = new LabelX("", style)).growY().row();

            add(imbueBtn = new SmartButton("Imbue", () -> imbuer.imbue(getSelectedItem(), getSouls())));
//            addActor(new SmartButton("Imbue", () -> imbuer.imbue(getSelectedItem(), getSouls())));
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

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        itemInfo.setUserObject(getSelectedItem());
    }

    private String initTraitInfo() {
        return "";
    }

    private String initAspectInfo() {
        String info = "Eidolon Aspects\n" + imbuer.getAspects(getSouls());

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

}
