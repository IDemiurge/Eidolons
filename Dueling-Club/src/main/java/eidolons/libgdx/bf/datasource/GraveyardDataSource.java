package eidolons.libgdx.bf.datasource;

import eidolons.libgdx.bf.grid.cell.BaseView;

import java.util.List;

public interface GraveyardDataSource {
    List<BaseView> getGraveyard();

    int getTotalCorpsesCount();
}
