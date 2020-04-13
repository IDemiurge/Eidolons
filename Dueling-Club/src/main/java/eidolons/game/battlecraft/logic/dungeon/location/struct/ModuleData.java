package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.MODULE_VALUE;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

public class ModuleData extends StructureData<MODULE_VALUE, Module> {

    public ModuleData(Module structure) {
        super(structure);
    }


    protected void init() {
        if (getStructure() == null) {
            return;
        }
        Module module = getStructure();
        if (module.getData() != null) {
            setData(module.getData().getData());
        } else {
            setValue(MODULE_VALUE.name, module.getName());
            setValue(MODULE_VALUE.height, module.getHeight());
            setValue(MODULE_VALUE.width, module.getWidth());
        }
        setValue(MODULE_VALUE.origin, module.getOrigin());
    }

    @Override
    public void apply() {
        Module module = getStructure();
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
                    if (!StringMaster.isEmpty(getValue(MODULE_VALUE.origin))) {
                        Coordinates c = Coordinates.get(getValue(MODULE_VALUE.origin));
                        if (!CoreEngine.isLevelEditor()) {
                            int offsetX = module.getData().getIntValue(MODULE_VALUE.width_buffer);
                            int offsetY = module.getData().getIntValue(MODULE_VALUE.height_buffer);
                            c = c.getOffset(-offsetX, -offsetY);
                        } else {
                            int offsetX = module.getData().getIntValue(MODULE_VALUE.border_width);
                            int offsetY = module.getData().getIntValue(MODULE_VALUE.border_width);
                            c = c.getOffset(-offsetX, -offsetY);
                        }
                        module.setOrigin(c);
                    }
                    break;
            }
        }
    }

    @Override
    public Class<? extends MODULE_VALUE> getEnumClazz() {
        return MODULE_VALUE.class;
    }
}
