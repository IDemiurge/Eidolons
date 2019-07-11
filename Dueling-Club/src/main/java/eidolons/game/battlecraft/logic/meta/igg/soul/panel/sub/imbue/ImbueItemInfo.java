package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.imbue;

import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.ActorMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.gui.panels.ScrollPaneX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.ArmorTooltip;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.SlotItemTooltip;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponTooltip;
import eidolons.libgdx.texture.Images;

public class ImbueItemInfo extends TablePanelX {

    private final TablePanelX  table;
    private final FadeImageContainer img;
    private   ScrollPaneX scroll;

    public ImbueItemInfo() {
        super(300, 800);
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        ActorMaster.addTop(this,  new NoHitImage(Images.CIRCLE_BORDER));
        ActorMaster.addTop(this,  img = new FadeImageContainer(Images.EMPTY_LIST_ITEM));

//        add(scroll = new ScrollPaneX(table = new TablePanelX<>()));
//        scroll.setSize(300, 500);
        add( table = new TablePanelX<>()) ;
    }

    @Override
    public float getMaxHeight() {
        return getHeight();
    }

    @Override
    public float getMaxWidth() {
        return super.getWidth();
    }

    @Override
    public void updateAct(float delta) {
        table.clearChildren();
        DC_HeroItemObj item = (DC_HeroItemObj) getUserObject();
        if (item == null) {
            img.setImage("");
            return;
        }
        img.setImage(item.getImagePath().replace("icons", "sprites"));
        if (item instanceof DC_WeaponObj) {
            SlotItemTooltip tooltip = new WeaponTooltip((DC_WeaponObj) item);
            table. add(tooltip);
        } else {
            if (item instanceof DC_ArmorObj) {
                SlotItemTooltip tooltip = new ArmorTooltip((DC_ArmorObj) item);
                table.  add(tooltip);
            }
        }

        super.updateAct(delta);

    }
}
