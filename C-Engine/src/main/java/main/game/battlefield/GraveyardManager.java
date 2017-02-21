package main.game.battlefield;

import main.content.enums.GenericEnums;
import main.content.values.parameters.G_PARAMS;
import main.entity.obj.BfObj;
import main.entity.obj.Cell;
import main.entity.obj.Obj;
import main.game.core.game.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Death Logic Store the dead: automatically; accessibility (for selecting and
 * checks):
 * <p>
 * Zone Targeting:
 * <p>
 * Destroy corpse if toughness or endurance reach -100%
 *
 * @param unit
 */
public interface GraveyardManager {
      int GRAVE_ROWS = 4;

    String getRipString(Obj obj);

    void init();

    ZCoordinates getZCoordinate(Coordinates c);

    Obj removeCorpse(Obj unit);

    void unitDies(Obj unit);

    void addCorpse(Obj unit);

    void updateGraveIndices();

    int getGraveIndex(BfObj obj);

    Obj getTopDeadUnit(Coordinates c);

    List<Coordinates> getCorpseCells();

    List<Obj> getDeadUnits(Coordinates c);

    Obj destroyTopCorpse(Coordinates c);

    boolean checkForCorpses(Obj obj);
}
