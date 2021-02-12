package libgdx.gui.panels.headquarters.creation;

import eidolons.entity.obj.unit.Unit;
import eidolons.system.libgdx.datasource.HeroDataModel;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.data.MapMaster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcHeroModel extends HeroDataModel {

    Map<HeroCreationSequence.HERO_CREATION_ITEM, List<HeroOperation>> operations = new LinkedHashMap<>();

    public HcHeroModel(Unit hero) {
        super(hero);
        HeroCreationMaster.setModel(this);
    }

    public void rollback(HeroCreationSequence.HERO_CREATION_ITEM until) {
        List<HeroOperation> list =     new ArrayList<>() ;
        Map<HeroCreationSequence.HERO_CREATION_ITEM, List<HeroOperation>> newMap= new LinkedHashMap<>();
        for (HeroCreationSequence.HERO_CREATION_ITEM item : HeroCreationSequence.HERO_CREATION_ITEM.values()) {
            if (item==until)
                break;
            if (!operations.containsKey(item))
                continue;
            list.addAll(operations.get(item));
            newMap.put(item, operations.get(item));

        }
        this.operations=newMap;
        HqDataMaster.getInstance(getHero()).undo_(list);
    }

    @Override
    public ObjType getBackgroundType() {
        if (getBackground()!=null )
        backgroundType = DataManager.getType(getBackground().getMale().getTypeName(), DC_TYPE.CHARS);
        return super.getBackgroundType();
    }

    @Override
    public HeroOperation modified(HERO_OPERATION operationType, Object... arg) {
        HeroOperation operation = super.modified(operationType, arg);
        if (operationType==HERO_OPERATION.LEVEL_UP){
            if (operations.get(operationType)!=null )
            if (operations.get(operationType).
             removeIf(item-> item.getOperation()==HERO_OPERATION.LEVEL_UP));
        }
        MapMaster.addToListMap(operations, HeroCreationMaster.getCurrentItem(), operation);
        return operation;
    }

}
