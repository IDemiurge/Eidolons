package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationSequence.HERO_CREATION_ITEM;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcHeroModel extends HeroDataModel {

    Map<HERO_CREATION_ITEM, List<HeroOperation>> operations = new LinkedHashMap<>();

    public HcHeroModel(Unit hero) {
        super(hero);
        HeroCreationMaster.setModel(this);
    }

    public void rollback(HERO_CREATION_ITEM until) {
        List<HeroOperation> list =     new ArrayList<>() ;
        Map<HERO_CREATION_ITEM, List<HeroOperation>> newMap= new LinkedHashMap<>();
        for (HERO_CREATION_ITEM item : operations.keySet()) {
            if (item==until)
                break;
            list.addAll(operations.get(item));
            newMap.put(item, operations.get(item));

        }
        this.operations=newMap;
        HqDataMaster.getInstance(getHero()).undo_(list);
    }

    @Override
    public HeroOperation modified(HERO_OPERATION operationType, Object... arg) {
        HeroOperation operation = super.modified(operationType, arg);
        MapMaster.addToListMap(operations, HeroCreationMaster.getCurrentItem(), operation);
        return operation;
    }

}
