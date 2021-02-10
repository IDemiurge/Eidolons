package libgdx.gui.panels.headquarters.datasource.tree;

import eidolons.entity.obj.unit.Unit;

import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public abstract class HeroTreeDataSource {
   protected Unit hero;
    protected List slotData;
    protected List linkData;

    public HeroTreeDataSource(Unit hero) {
        this.hero = hero;
    }

    public abstract List getSlotData(int tier);
    public abstract List getLinkData(int tier);

    public Object getSlotData(int tier, int slot) {
        if (slotData==null )
            slotData = getSlotData(tier);
        if (slotData.size()<=slot)
            return null;
        return slotData.get(slot);
    }
    public Object getLinkData(int tier, int slot) {
        if (linkData==null )
            linkData = getLinkData(tier);
        if (linkData.size()<=slot)
            return null;
        return linkData.get(slot);
    }

    public   Object getData(int tier, int slot, boolean slotsOrLinks){
        return slotsOrLinks ? getSlotData(tier, slot) : getLinkData(tier, slot);
    }
}
