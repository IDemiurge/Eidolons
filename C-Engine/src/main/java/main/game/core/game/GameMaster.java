package main.game.core.game;

import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.log.LogMaster;

import java.util.Collection;
import java.util.Set;

/**
 * Created by JustMe on 2/15/2017.
 */
public class GameMaster {
    protected Game game;

    public GameMaster(Game game) {
        this.game= game;
    }

    public Obj getObjectById(Integer id) {
        if (id == null) {
            LogMaster.log(LogMaster.CORE_DEBUG,
             "searching for obj by null id");
            return null;
        }
        try {
            return  game.getState().getObjMap().get(id);
        } catch (Exception e) {
            LogMaster.log(LogMaster.CORE_DEBUG_1, "no obj found by id " + id);
            return null;
        }

    }

    public Game getGame() {
        return game;
    }

    public ObjType getTypeById(Integer id) {
        return game.getManager().getStateManager().getTypeById(id);
    }

    public Collection<Obj> getUnitsForCoordinates(Set<Coordinates> coordinates) {
        return null ;
    }
}
