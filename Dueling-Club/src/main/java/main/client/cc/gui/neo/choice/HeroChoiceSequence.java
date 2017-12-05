package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.VALUE;
import main.content.values.properties.PROPERTY;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioPrecombatMaster;
import main.game.core.game.DC_Game.GAME_TYPE;

import java.util.ArrayList;

public class HeroChoiceSequence extends ChoiceSequence {

    ObjType buffer;
    private Unit hero;

    public HeroChoiceSequence(Unit hero) {
        super();
        this.hero = hero;
        initDefaultHeroSequence();
        CharacterCreator.getHeroManager().addHero(hero);
        CharacterCreator.getHeroManager().saveHero(hero);
    }

    public void initDefaultHeroSequence() {
        views = new ArrayList<>();
        if (hero.getGame().getGameType() == GAME_TYPE.SCENARIO) {
            if (ScenarioPrecombatMaster.getScenario() != null) {

            }
            // reqs =
            // ScenarioMaster.getScenario().getHeroCreationRequirements();
        } else {

            views.add(new BackgroundChoiceView(this, hero));
            views.add(new PortraitChoiceView(this, hero));
            views.add(new DeityChoiceView(this, hero));
            views.add(new EmblemChoiceView(this, hero));
            // views.add(new PrincipleChoiceView(this, hero));
            // views.add(new PrincipleChoiceView(this, hero, true));
            views.add(new SoundsetChoiceView(this, hero));
            if (UnitGroupMaster.isFactionMode()) {
                views.add(new EntityChoiceView(null, null) {

                    @Override
                    public String getInfo() {
                        return null;
                    }

                    @Override
                    protected OBJ_TYPE getTYPE() {
                        return DC_TYPE.FACTIONS;
                    }

                    @Override
                    protected PROPERTY getPROP() {
                        return PROPS.FACTION;
                    }

                    @Override
                    protected VALUE getFilterValue() {
                        return null;
                    }
                });
            }
        }
    }

    @Override
    public void back() {
        try {
            CharacterCreator.getHeroManager().stepBack(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        super.back();
    }

    @Override
    protected void next() {
        super.next();
    }
}
