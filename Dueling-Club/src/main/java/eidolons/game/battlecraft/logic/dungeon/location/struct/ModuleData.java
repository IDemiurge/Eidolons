package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.MODULE_VALUE;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Module;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;

public class ModuleData extends StructureData<MODULE_VALUE, LE_Module> {

    public ModuleData(LE_Module structure) {
        super(structure);
    }


    protected void init() {
        if (getStructure() == null) {
            return;
        }
        Module module = getStructure().getModule();
        if (module.getData() != null) {
            setData(module.getData().getData());
        } else {
            setValue(MODULE_VALUE.name, module.getName());
            setValue(MODULE_VALUE.height, module.getHeight());
            setValue(MODULE_VALUE.width, module.getWidth());
            setValue(MODULE_VALUE.origin, module.getOrigin());
        }
        levelStruct = module;
        //defaults?
    }
    @Override
    public void apply() {
        Module module = getStructure().getModule();
        module.setData(this);
        for (MODULE_VALUE value : MODULE_VALUE.values()) {
            String val = getValue(value);
            switch (value) {
                case name:
                    module.setName(val);
                    break;
                case width:
                    module.setWidth(getIntValue(value));
                    break;
                case height:
                    module.setHeight(getIntValue(value));
                    break;
                case origin:
                    if (!StringMaster.isEmpty( getValue(MODULE_VALUE.origin)))
                         module.setOrigin(Coordinates.get(getValue(MODULE_VALUE.origin)));
                    break;
            }
        }
    }

    @Override
    public Class<? extends MODULE_VALUE> getEnumClazz() {
        return MODULE_VALUE.class;
    }
}
