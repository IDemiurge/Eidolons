package logic.core;

import gdx.dto.UnitDto;
import logic.content.test.TestUnitContent;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.MoveLogic;
import logic.lane.HeroPos;
import logic.lane.LanePos;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {

    // New Game just via this?!
    public Game(LaunchData data) {
        GuiEventManager.trigger(GuiEventType.DTO_FrontField, data.initFFDto(this));
        GuiEventManager.trigger(GuiEventType.DTO_HeroZone, data.initHeroDto(this));
        GuiEventManager.trigger(GuiEventType.DTO_LaneField, data.initLfDto(this));

        startLoop();
    }

    private void startLoop() {
        //TODO
    }

    public List<UnitDto> createUnitsOnLane(int i, String[][] laneData) {

        List<UnitDto> list=     new ArrayList<>() ;
        int n =-1;
        for (String s : laneData[i]) {
            n++;
            if (s.isEmpty())
              continue;
            LanePos pos = new LanePos(i, n);
            list.add(new UnitDto(createUnit(pos, s)));
        }
        return list;
    }

    private Unit createUnit(LanePos pos, String s) {
        TestUnitContent.TestUnit template = TestUnitContent.TestUnit.valueOf(s);
        Map<String, Object> values = template.getValues();
        Unit unit= new Unit(pos, values);
        return unit;
    }

    public Entity createHeroOrObject(String s, HeroPos pos) {
        TestUnitContent.TestHero template = TestUnitContent.TestHero.valueOf(s);
        Map<String, Object> values = template.getValues();
        Hero hero= new Hero(pos, values);
        MoveLogic.newPosition(hero, pos);
        return hero;
    }
}
