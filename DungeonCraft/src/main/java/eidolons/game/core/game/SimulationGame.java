package eidolons.game.core.game;

import eidolons.entity.DC_IdManager;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import main.content.values.properties.PROPERTY;
import main.data.XLinkedMap;
import main.entity.type.ObjType;

import java.util.Map;

/**
 * Created by JustMe on 5/9/2017.
 */
public class SimulationGame extends DC_Game {

    private Map<BattleFieldObject, Map<String, DC_HeroAttachedObj>> simulationCache; //to simGame!
    private DC_Game realGame;

    public SimulationGame() {
        super(true, false);
        idManager = new DC_IdManager(){
            @Override
            public Integer getNewId() {
                ID--;
                return ID;
            }
        };
    }

    @Override
    public void init() {
        initMasters();
        super.init();
    }

    //how should these be called properly?
    public DC_HeroAttachedObj getSimulationObj(Unit dc_HeroObj, ObjType type, PROPERTY prop) {
        try {
            return getSimulationCache().get(dc_HeroObj).get(type.getName() + prop.getShortName());
        } catch (Exception e) {
            return null;
        }
    }

    public void addSimulationObj(Unit dc_HeroObj, ObjType type, DC_HeroAttachedObj item,
                                 PROPERTY prop) {

        Map<String, DC_HeroAttachedObj> cache = getSimulationCache().get(dc_HeroObj);
        if (cache == null) {
            cache = new XLinkedMap<>();
            getSimulationCache().put(dc_HeroObj, cache);
        }
        cache.put(type.getName() + prop.getShortName(), item);

    }

    public Map<BattleFieldObject, Map<String, DC_HeroAttachedObj>> getSimulationCache() {
        if (simulationCache == null) {
            simulationCache = new XLinkedMap<>();
        }
        return simulationCache;
    }

    public void setRealGame(DC_Game realGame) {
        this.realGame = realGame;
    }

    public DC_Game getRealGame() {
        return realGame;
    }

    @Override
    public DC_Player getPlayer(boolean me) {
        return realGame.getPlayer(me);
    }
}
