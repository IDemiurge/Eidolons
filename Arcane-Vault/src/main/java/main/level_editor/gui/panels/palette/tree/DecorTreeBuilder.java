package main.level_editor.gui.panels.palette.tree;

import com.google.inject.internal.util.ImmutableList;
import main.level_editor.backend.metadata.decor.LE_DecorHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class DecorTreeBuilder {
    public List<PaletteNode> build() {
        List<PaletteNode> list=    new ArrayList<>() ;
        for (LE_DecorHandler.DECOR value : LE_DecorHandler.DECOR.values()) {
            list.add(new PaletteNode(value));
        }
        PaletteNode parent = new PaletteNode(new LinkedHashSet<>(list), "decor");
        return ImmutableList.of(parent);
    }
}
