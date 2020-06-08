package main.data.types;

import eidolons.game.module.herocreator.logic.UnitLevelManager;
import main.content.DC_TYPE;
import main.data.AvHandler;
import main.entity.type.ObjType;
import main.simulation.SimulationManager;
import main.system.auxiliary.ContainerUtils;
import main.system.data.DataUnit;

public class AV_Assembler extends AvHandler implements AvAssembler {

    public void applyType() {
        ObjType type = getSelected();
        ObjType applied = getPrevious(); ////TODO how to choose?
        applyType(type, applied);
    }

    private void applyType(ObjType base, ObjType applied) {
        // for (PARAMETER item : DC_ContentValsManager.getAppliedParamsModify()) {
        //     Integer n = applied.getIntParam(item);
        //     base.modifyParameter((item), n, null, true);
        // }
        // for (PARAMETER item : DC_ContentValsManager.getAppliedParamsSet()) {
        //     Integer n = applied.getIntParam(item);
        //     base.setParameter(item, n);
        // }
    }

    @Override
    public void construct() {
        String typesString = input("");
        ObjType base = getSelected();
        TypeAssemblyData data = new TypeAssemblyData(typesString);
        String value = data.getValue(TEMPLATE_TYPE.BASE);
        for (String name : ContainerUtils.openContainer(data.getValue(TEMPLATE_TYPE.GROUP))) {
            ObjType type = getType(name, DC_TYPE.UNITS);
            applyType(base, type);
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
        SimulationManager.refreshType(getSelected());
    }

    public class TypeAssemblyData extends DataUnit<TEMPLATE_TYPE> {
        public TypeAssemblyData(String text) {
            super(text);
        }
    }

}
