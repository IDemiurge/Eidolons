package main.level_editor.backend.handlers.selection;

import main.game.bf.Coordinates;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class LE_Selection  implements Serializable {

    Set<Integer> ids=new LinkedHashSet<>();
    Set<Coordinates> coordinates=new LinkedHashSet<>();
    boolean meta;
    String layer;

    public LE_Selection() {

    }
    public LE_Selection(LE_Selection selection) {
        this(selection.ids, selection.coordinates, selection.meta, selection.layer);
        //can be - block, layer, ... but those cases should be separate?
    }

    public LE_Selection(Set<Integer> ids, Set<Coordinates> coordinates, boolean meta, String layer) {
        this.ids = ids;
        this.coordinates = coordinates;
        this.meta = meta;
        this.layer = layer;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
    }

    public Set<Coordinates> getCoordinates() {
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

}
