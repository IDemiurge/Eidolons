package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.content.DC_ContentManager;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.elements.conditions.Condition;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;

import java.util.List;

public class BackgroundChoiceView extends EntityChoiceView {

    public BackgroundChoiceView(ChoiceSequence choiceSequence, DC_HeroObj hero) {
        super(choiceSequence, hero);
    }

    protected boolean isSaveHero() {
        return true;
    }

    @Override
    public String getInfo() {
        return InfoMaster.CHOOSE_BACKGROUND;
    }

    protected int getPageSize() {
        return 24;
    }

    protected int getColumnsCount() {
        return 4;
    }

    @Override
    protected Condition getFilterConditions() {
        // return new StringComparison(
        // StringMaster.getValueRef(KEYS.MATCH, G_PROPS.RACE),
        // RACE.HUMAN.toString(), true);
        return null;
    }

    @Override
    protected String getGroup() {
        return StringMaster.BACKGROUND;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.CHARS;
    }

    @Override
    protected void applyChoice() {
        CharacterCreator.getHeroManager().saveHero(hero);
        ObjType type = data.get(getSelectedIndex());
        // HeroManager.applyChangedTypeStatic(hero, type);
        applyBackground(hero, type);
    }

    private void applyBackground(DC_HeroObj hero, ObjType bgType) {
        ObjType type = hero.getType();
        type.setProperty(G_PROPS.BASE_TYPE, bgType.getName());
        type.setProperty(G_PROPS.BACKGROUND_TYPE, bgType.getName());
        List<VALUE> backgroundParams = DC_ContentManager.getBackgroundStaticValues();
        for (VALUE v : backgroundParams) {
            if (v instanceof PARAMETER) {
                if (v.isWriteToType()) {
                    hero.modifyParameter((PARAMETER) v, bgType.getIntParam((PARAMETER) v));
                } else {
                    type.modifyParameter((PARAMETER) v, bgType.getIntParam((PARAMETER) v));
                }

                // type.multiplyParamByPercent((PARAMETER) v, 50, false);// TODO
                // feeble
                // fix!
            } else if (v instanceof PROPERTY) {
                type.setProperty((PROPERTY) v, bgType.getProperty((PROPERTY) v));
            }
        }
        hero.toBase();
    }

    @Override
    protected PROPERTY getPROP() {
        return null;
    }

    @Override
    protected VALUE getFilterValue() {
        return G_PROPS.GROUP;
    }

}
