package logic.core.game;

import eidolons.game.core.Core;
import gdx.dto.FrontFieldDto;
import gdx.dto.FrontLineDto;
import gdx.dto.LaneFieldDto;
import gdx.dto.UnitDto;
import logic.content.test.TestUnitContent;
import logic.core.Aphos;
import logic.core.LaunchData;
import logic.core.game.handlers.AiHandler;
import logic.core.game.handlers.GameHandler;
import logic.core.game.handlers.RoundHandler;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.combat.HeroMoveLogic;
import logic.functions.meta.core.CoreHandler;
import logic.lane.HeroPos;
import logic.lane.LanePos;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {

    private RoundHandler roundHandler;
    private AiHandler aiHandler;
    private List<GameHandler> handlers = new ArrayList<>();
    private GameController gameController;
    private DequeImpl<Unit> units = new DequeImpl<>();
    private DequeImpl<Hero> heroes = new DequeImpl<>();
    private CoreHandler coreHandler;

    // New Game just via this?!
    public Game() {
        init();
    }

    private void init() {
        gameController = new GameController(this);
        handlers.add(roundHandler = new RoundHandler(this));
        handlers.add(coreHandler = new CoreHandler(this));
        handlers.add(aiHandler = new AiHandler(this));
    }

    public void start(LaunchData data) {
        triggerEvents(data);

        Core.onNewThread(() ->
                {
                    boolean gameOver = false;
                    while (!gameOver) {
                        gameOver = roundHandler.newRound();
                    }
                }
        );
        //TODO
    }

    private void triggerEvents(LaunchData data) {
        FrontFieldDto ffDto = data.initFFDto(this);
        FrontLineDto heroDto = data.initHeroDto(this);
        LaneFieldDto laneFieldDto = data.initLfDto(this);

        Aphos.hero = heroDto.getActiveHero();
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.GUI_READY);
        GuiEventManager.trigger(AphosEvent.DTO_FrontField, ffDto);
        GuiEventManager.trigger(AphosEvent.DTO_HeroZone, heroDto);
        GuiEventManager.trigger(AphosEvent.DTO_LaneField, laneFieldDto);
    }

    public List<UnitDto> createUnitsOnLane(int i, String[][] laneData) {
        List<UnitDto> list = new ArrayList<>();
        int n = -1;
        for (String s : laneData[i]) {
            n++;
            if (s.isEmpty())
                continue;
            LanePos pos = new LanePos(i, n);
            list.add(new UnitDto(createUnit(pos, s)));
        }
        return list;
    }

    public int add(Entity entity) {
        if (entity instanceof Hero) {
            heroes.add((Hero) entity);
            return heroes.size() + units.size() - 1;
        }
        if (entity instanceof Unit) {
            units.add((Unit) entity);
            return units.size() - 1;
        }
        return 0;
    }

    private Unit createUnit(LanePos pos, String s) {
        TestUnitContent.TestUnit template = TestUnitContent.TestUnit.valueOf(s);
        Map<String, Object> values = template.getValues();
        Unit unit = new Unit(pos, values);
        return unit;
    }

    public Entity createHeroOrObject(String s, HeroPos pos) {
        TestUnitContent.TestHero template = TestUnitContent.TestHero.valueOf(s);
        Map<String, Object> values = template.getValues();
        Hero hero = new Hero(pos, values);
        HeroMoveLogic.newPosition(hero, pos);
        return hero;
    }

    public RoundHandler getRoundHandler() {
        return roundHandler;
    }

    public AiHandler getAiHandler() {
        return aiHandler;
    }

    public DequeImpl<Unit> getUnits() {
        return units;
    }

    public DequeImpl<Hero> getHeroes() {
        return heroes;
    }

    public GameController getController() {
        return gameController;
    }

    public CoreHandler getCoreHandler() {
        return coreHandler;
    }

}
