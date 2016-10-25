package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.VALUE;
import main.content.properties.PROPERTY;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game.GAME_TYPE;
import main.game.battlefield.UnitGroupMaster;
import main.game.logic.dungeon.scenario.ScenarioMaster;

import java.util.LinkedList;

public class HeroChoiceSequence extends ChoiceSequence {

    ObjType buffer;
    private DC_HeroObj hero;

    public HeroChoiceSequence(DC_HeroObj hero) {
        super();
        this.hero = hero;
        initDefaultHeroSequence();
        CharacterCreator.getHeroManager().addHero(hero);
        CharacterCreator.getHeroManager().saveHero(hero);
    }

    public void initDefaultHeroSequence() {
        views = new LinkedList<ChoiceView>();
        if (hero.getGame().getGameType() == GAME_TYPE.SCENARIO) {
            if (ScenarioMaster.getScenario() != null) {

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
                        return OBJ_TYPES.FACTIONS;
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
            e.printStackTrace();
        }
        super.back();
    }

    @Override
    protected void next() {
        super.next();
    }
}
