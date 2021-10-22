package libgdx.gui.panels.headquarters.datasource.tree;

import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.attach.Perk;
import eidolons.entity.unit.Unit;
import eidolons.netherflame.eidolon.heromake.passives.HeroClassMaster;
import eidolons.netherflame.eidolon.heromake.passives.PerkMaster;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/6/2018.
 */
public class ClassesDataSource extends HeroTreeDataSource {

    public ClassesDataSource(Unit hero) {
        super(hero);
    }

    @Override
    public List getSlotData(int tier) {
        return new ArrayList<>(HeroClassMaster.getClasses(hero, tier));
    }

    public List getPerkSlots(int tier) {
        List linkData = new ArrayList<>();
        List<DC_PassiveObj> classes = HeroClassMaster.getClasses(hero, tier);
        List<Perk> perks = PerkMaster.getPerks(hero, tier);

        for (DC_PassiveObj class1 : classes) {
            DC_PassiveObj class2 = null;
            int i = classes.indexOf(class1);
            if (classes.size() >i + 1)
                class2 = classes.get(i + 1);
            Perk perk= null ;
            if (perks.size()> i)
                perk = perks.get(i);

            linkData.add(new ImmutableTriple<>(
             perk, class1, class2));
        }
        return linkData;
    }

    public List getLinkData(int tier) {
        return getPerkSlots(tier);
    }


}
