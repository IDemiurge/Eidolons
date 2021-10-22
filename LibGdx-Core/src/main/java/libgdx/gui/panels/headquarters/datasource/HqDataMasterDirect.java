package libgdx.gui.panels.headquarters.datasource;

import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import eidolons.system.libgdx.datasource.HeroDataModel;
import libgdx.gui.panels.headquarters.HqPanel;
import libgdx.gui.panels.headquarters.town.TownPanel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 10/18/2018.
 */
public class HqDataMasterDirect extends HqDataMaster {
    static Map<Unit, HqDataMasterDirect> map = new HashMap<>();

    public HqDataMasterDirect(Unit hero) {
        super(hero);
    }

    public static HqDataMasterDirect getOrCreateInstance(Unit unit) {
        HqDataMasterDirect dataMaster;
        if (HqPanel.getActiveInstance() == null
         && TownPanel.getActiveInstance() == null)
            dataMaster = createAndSaveInstance(unit);
        else
            dataMaster =  getInstance(unit);
        return dataMaster;
    }

    public static HqDataMasterDirect createAndSaveInstance(Unit unit) {
        HqDataMasterDirect instance = new HqDataMasterDirect(unit);
        map.put(unit, instance);
        return instance;
    }

    public static HqDataMasterDirect getInstance() {
        return getInstance(Core.getMainHero());
    }

    public static HqDataMasterDirect getInstance(Unit unit) {
        HqDataMasterDirect instance = map.get(unit);
        if (instance == null) {
            instance = new HqDataMasterDirect(unit);
            map.put(unit, instance);
        }
        return instance;
    }

    @Override
    protected HeroDataModel createHeroDataModel(Unit hero) {
        return new HeroWrapper(hero);
    }

    public void applyModifications(boolean self) {

    }

    @Override
    public void applyModifications() {
        super.applyModifications();
    }
}
