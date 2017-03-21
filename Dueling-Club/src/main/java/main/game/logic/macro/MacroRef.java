package main.game.logic.macro;

import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.logic.macro.entity.MacroObj;
import main.game.logic.macro.global.World;
import main.game.logic.macro.map.Region;
import main.game.logic.macro.map.Route;
import main.game.logic.macro.travel.MacroParty;

public class MacroRef extends Ref {

    public MacroRef(MacroGame macroGame) {
        super(macroGame);
    }

    public MacroRef(Obj obj) {
        this(MacroGame.getGame());
        setSource(obj.getId());
    }

    public MacroRef() {
        this(MacroGame.getGame());
    }

    public static MacroRef getMainRefCopy() {
        return MacroGame.getGame().getRef().getCopy();
    }

    public static MacroRef getMainRef() {
        return MacroGame.getGame().getRef();
    }

    @Override
    public MacroRef getCopy() {
        return (MacroRef) super.getCopy();
    }

    @Override
    public Object clone() {
        MacroRef ref = new MacroRef(getGame());
        ref.cloneMaps(this);
        ref.setPlayer(player);
        ref.setEvent(event);
        ref.setGroup(group);
        ref.setBase(base);
        ref.setGame(game);
        ref.setEffect(effect);
        ref.setTriggered(triggered);
        ref.setAmount(getAmount());
        return ref;
    }

    // should inheritance be changed?
    public MacroObj getMacroObj(MACRO_KEYS key) {
        return (MacroObj) super.getObj(key.toString());
    }

    @Override
    public MacroGame getGame() {
        return (MacroGame) super.getGame();
    }

    public MacroParty getParty() {
        return (MacroParty) getObj(KEYS.PARTY);
    }

    public void setParty(MacroParty activeParty) {
        setID(KEYS.PARTY, activeParty.getId());

    }

    public Route getRoute() {
        return (Route) getMacroObj(MACRO_KEYS.ROUTE);
    }

    public void setRoute(Route route) {
        setID(MACRO_KEYS.ROUTE.toString(), route.getId());
    }

    public World getWorld() {
        return (World) getMacroObj(MACRO_KEYS.WORLD);
    }

    public Region getRegion() {
        return (Region) getMacroObj(MACRO_KEYS.REGION);
    }

    public void setRegion(Region region) {
        setID(MACRO_KEYS.REGION.toString(), region.getId());
    }

    public void setMacroId(MACRO_KEYS key, Integer id) {
        setID(key.toString(), id);

    }

    public enum MACRO_KEYS {
        REGION,
        ROUTE,
        PLACE,
        TOWN,
        ENCOUNTER,
        PARTY,
        COMPANION,
        FACTION,
        AREA,
        WORLD,
        CAMPAIGN
    }

}
