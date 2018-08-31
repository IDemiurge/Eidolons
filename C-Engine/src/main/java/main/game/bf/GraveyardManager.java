package main.game.bf;

import main.entity.obj.Obj;

import java.util.List;
import java.util.Set;

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

    Obj getTopDeadUnit(Coordinates c);

    Set<Coordinates> getCorpseCells();

    List<Obj> getDeadUnits(Coordinates c);

    Obj destroyTopCorpse(Coordinates c);

    boolean checkForCorpses(Obj obj);
}
