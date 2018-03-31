package main.game.bf;

import main.entity.obj.BfObj;
import main.entity.obj.Obj;

import java.util.List;

/**
 * Death Logic Store the dead: automatically; accessibility (for selecting and
 * checks):
 * <portrait>
 * Zone Targeting:
 * <portrait>
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
