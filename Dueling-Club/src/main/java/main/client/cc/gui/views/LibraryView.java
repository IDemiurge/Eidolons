package main.client.cc.gui.views;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.images.ImageManager.BORDER;

import java.util.Comparator;

public class LibraryView extends HeroItemView {

    public LibraryView(DC_HeroObj hero) {
        super(hero, true, true);
        init();
        itemManager.setPROP2(getPROP2());
    }

    @Override
    public void activate() {

    }

    protected Comparator<? super ObjType> getItemSorter() {
        return new Comparator<ObjType>() {
            public int compare(ObjType o1, ObjType o2) {
                if (o1.isUpgrade()) {
                    if (!o2.isUpgrade())
                        return 1;

                } else {
                    if (o2.isUpgrade())
                        return -1;
                }

                if (o1.getIntParam(getSortingParam()) > o2
                        .getIntParam(getSortingParam()))
                    return 1;
                if (o1.getIntParam(getSortingParam()) == o2
                        .getIntParam(getSortingParam()))
                    return 0;
                return -1;
            }
        };
    }

    protected PARAMETER getSortingParam() {
        return PARAMS.SPELL_DIFFICULTY;
    }

    @Override
    public PARAMS getPoolParam() {
        return PARAMS.XP;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.SPELLS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.LEARNED_SPELLS;
    }

    protected PROPERTY getPROP2() {
        return PROPS.MEMORIZED_SPELLS;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        BORDER b = WorkspaceMaster.getBorderForType(value);
        if (b != null)
            return b;
        if (StringMaster.checkContainer(hero.getProperty(PROPS.KNOWN_SPELLS),
                value.getName(), true))
            return BORDER.SPELL_HIGHLIGHTED;
        // if (StringMaster.checkContainer(hero.getProperty(getPROP2()), value
        // .getName(), true))
        // return BORDER.SPELL_HIGHLIGHTED;
        // if (StringMaster.checkContainer(hero.getProperty(getPROP2()), value
        // .getName(), true))
        // return BORDER.SPELL_HIGHLIGHTED;

        String r = hero.getGame().getRequirementsManager().check(hero, value);
        if (r == null)
            return null;

        return BORDER.HIDDEN;
    }
}
