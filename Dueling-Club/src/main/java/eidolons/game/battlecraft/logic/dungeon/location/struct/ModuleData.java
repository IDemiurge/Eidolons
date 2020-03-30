package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.MODULE_VALUE;
import eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper.LE_Module;
import eidolons.game.battlecraft.logic.dungeon.module.Module;

public class ModuleData extends LevelStructure.StructureData<MODULE_VALUE, LE_Module> {

    public ModuleData(LE_Module structure) {
        super(structure);
    }

    protected void init() {
        if (getStructure() == null) {
            return;
        }

    }
    @Override
    public void apply() {
        Module module = getStructure().getModule();
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
                case zones:
                    break;
                case replace_default:
                    break;
                case default_wall:
                    break;
                case default_style:
                    break;
                case ambience:
                    break;
                case lighting:
                    break;
                case fires_color:
                    break;
                case vfx_template:
                    break;
                case default_pillar_type:
                    break;
                case default_shard_type:
                    break;
                case irregular_border:
                    break;
                case border_wall:
                    break;
                case border_wall_type:
                    break;
                case border_void:
                    break;
                case entrance:
                    break;
                case tile_map:
                    break;
                case layer_data:
                    break;
            }
        }
    }

    @Override
    public Class<? extends MODULE_VALUE> getEnumClazz() {
        return MODULE_VALUE.class;
    }
}
