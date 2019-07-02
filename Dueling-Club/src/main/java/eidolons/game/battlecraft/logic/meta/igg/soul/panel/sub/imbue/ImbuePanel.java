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

public class ImbuePanel extends TablePanelX {
    private EidolonImbuer imbuer;
    ImbueSoulSlots soulSlots;
    ImbueItems items;
    ImbueItemInfo itemInfo;
    ImbueTraitsInfo traitsInfo;

    public ImbuePanel() {
        imbuer = new EidolonImbuer();

      add( soulSlots = new ImbueSoulSlots(this)) ;
        add(itemInfo = new ImbueItemInfo()).row();
        add( items = new ImbueItems());
        add(traitsInfo = new ImbueTraitsInfo());
    }

    private Soul[] getSouls() {
        return soulSlots.getSouls();
    }

    private DC_HeroItemObj getSelectedItem() {
        return items.getSelected();
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

            add(new SmartButton("Imbue", () -> imbuer.imbue(getSelectedItem(), getSouls())));
//            addActor(new SmartButton("Imbue", () -> imbuer.imbue(getSelectedItem(), getSouls())));
        }

        @Override
        public void updateAct(float delta) {
            super.updateAct(delta);

            aspectInfo.setText(initAspectInfo());
            aspectInfo.setText(initTraitInfo());
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
