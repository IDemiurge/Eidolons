package main.level_editor.backend.handlers.selection;

import com.google.inject.internal.util.ImmutableSet;
import main.game.bf.Coordinates;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class LE_Selection implements Serializable {

    Set<Integer> ids = new LinkedHashSet<>();
    Set<Coordinates> coordinates = new LinkedHashSet<>();
    boolean meta;
    String layer;
    private Coordinates lastCoordinates;
    private LE_Selection frozenSelection;

    public LE_Selection() {

    }

    public LE_Selection(LE_Selection selection) {
        this(new LinkedHashSet<>(selection.ids),
                new LinkedHashSet<>(selection.coordinates), selection.meta, selection.layer);
        //can be - block, layer, ... but those cases should be separate?
    }

    public LE_Selection(Set<Integer> ids, Set<Coordinates> coordinates, boolean meta, String layer) {
        this.ids = ids;
        this.coordinates = coordinates;
        this.meta = meta;
        this.layer = layer;
    }

    public Set<Integer> getIds() {
        if (frozenSelection != null) {
            return ImmutableSet.<Integer>builder().addAll(ids).addAll(frozenSelection.getIds()).build();
        }
        return ids;
    }

    public void addIds(Collection<Integer> set) {
        ids.addAll(set);
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
    }

    public void addCoordinates(Collection<Coordinates> set) {
        coordinates.addAll(set);
    }

    public Set<Coordinates> getCoordinates() {
        if (frozenSelection != null) {
            return ImmutableSet.<Coordinates>builder().addAll(coordinates).addAll(frozenSelection.getCoordinates()).build();
        }
        return coordinates;
    }

    public void setCoordinates(Set<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void setSingleSelection(Integer id) {
        ids.clear();
        ids.add(id);
    }

    public boolean isEmpty() {
        return getCoordinates().isEmpty() && getIds().isEmpty();
    }

    public Coordinates getLastCoordinates() {
        return lastCoordinates;
    }

    public void setLastCoordinates(Coordinates lastCoordinates) {
        this.lastCoordinates = lastCoordinates;
    }

    public void freezeCurrent() {
        frozenSelection = new LE_Selection(this);
    }

    public void setFrozenSelection(LE_Selection frozenSelection) {
        this.frozenSelection = frozenSelection;
    }

    public LE_Selection getFrozenSelection() {
        return frozenSelection;
    }

    public void clear() {
        coordinates.clear();
    }

    public void add(Integer c) {
        ids.add(c);
    }

    public void remove(Integer c) {
        ids.remove(c);
    }
    public void add(Coordinates c) {
        coordinates.add(c);
    }

    public void remove(Coordinates c) {
        coordinates.remove(c);
    }

    public Coordinates getFirstCoordinate() {
        return getCoordinates().iterator().next();
    }
}
