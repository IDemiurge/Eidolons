package eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper;

import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.core.game.DC_Game;
import main.data.tree.LayeredData;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LE_Floor implements LayeredData<LE_Module> {


    private final Supplier<Dungeon> dungeon;
    private FloorData data;

    public LE_Floor(Supplier<Dungeon> dungeon) {
        this.dungeon = dungeon;
    }

    public Set<Module> getModules() {
        return dungeon.get().getGame().getMetaMaster().getModuleMaster().getModules();
    }

    @Override
    public Set<LE_Module> getChildren() {
        return getModules().stream().map(module -> new LE_Module(module)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toString() {
        return dungeon.get().getName() +
                " Floor with " +
                getModules().size() +
                " modules"+ ", Data: " + getData();
    }

    public Dungeon getDungeon() {
        return dungeon.get();
    }

    public DC_Game getGame() {
        return dungeon.get().getGame();
    }

    public void setData(FloorData data) {
        this.data = data;
    }

    @Override
    public Object getLevelLayer() {
        return getDungeon();
    }

    public FloorData getData() {
        return data;
    }
}
