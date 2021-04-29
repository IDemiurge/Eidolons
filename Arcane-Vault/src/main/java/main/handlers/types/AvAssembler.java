package main.handlers.types;

import eidolons.entity.TypeCombiner;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.launch.ArcaneVault;
import main.v2_0.AV2;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;

public class AvAssembler extends AvHandler implements IAvAssembler {

    public AvAssembler(AvManager manager) {
        super(manager);
    }

    /*
    what other handlers?
     */

    public void bakeInEntities(ObjType type) {
        // retain un-baked version with a suffix? Or bake-in only at runtime?
    }

    public void createComboType(boolean secondTable) {
        ObjType base = secondTable ? ArcaneVault.getPreviousSelectedType() : ArcaneVault.getSelectedType();
        //only 2 or any depth? Upgrades - should be separate and independent to support the logic of 'change once'

        ObjType type = getModelHandler().addType(null, secondTable, false);
        if (type != null) {
            type.setParentType(base);
        }
        getSimulationHandler().initUnitObj(type.getName());
        AV2.getMainBuilder().getTreeBuilder().newType(type, null);
        AV2.getMainBuilder().getEditViewPanel().resetData(type);
    }

    @Override
    public void loaded() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.UNITS)) {
            checkInitComboType(type);
        }
    }

    public void checkInitComboType(ObjType type) {
        // if (isBaseType(type))
        //     return;
        ObjType base = getBaseType(type);
        if (base != null) {
            type.setParentType(base);
        }

    }

    private ObjType getBaseType(ObjType type) {
        String property = type.getProperty(G_PROPS.PARENT_TYPE);
        if (property.isEmpty()) {
            return null;
        }
        return DataManager.getType(property, type.getOBJ_TYPE_ENUM());
    }

    public void refreshComboType(ObjType type) {
        if (type.getParentType() != null) {
            refreshComboType(type.getParentType());
            TypeCombiner.applyType(type, type.getParentType());
        }
    }

    public void applyType() {
        ObjType type = getSelected();
        type.toBase();
        ObjType applied = getPrevious(); ////TODO how to choose?
        TypeCombiner.applyType(type, applied);
    }


    @Override
    public void construct() {
        String typesString = input("");
        ObjType base = getSelected();
        TypeAssemblyData data = new TypeAssemblyData(typesString);
        String value = data.getValue(TEMPLATE_TYPE.BASE);
        for (String name : ContainerUtils.openContainer(data.getValue(TEMPLATE_TYPE.GROUP))) {
            ObjType type = getType(name, DC_TYPE.UNITS);
            TypeCombiner.applyType(base, type);
        }

    }


    @Override
    public void levelUp() {
        ObjType leveledType = UnitLevelManager.getLeveledType(getSelected(), 1, true);
        int lvl = inputInt("lvl");
        boolean confirm = confirm("");
        // overrideDisplay(leveledType);
    }


    @Override
    public void preview() {
        AV2.getSimulationHandler().refreshType(getSelected());
    }

    public class TypeAssemblyData extends DataUnit<TEMPLATE_TYPE> {
        public TypeAssemblyData(String text) {
            super(text);
        }
    }

}
