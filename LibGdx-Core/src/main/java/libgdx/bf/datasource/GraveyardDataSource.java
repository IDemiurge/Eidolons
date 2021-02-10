package libgdx.bf.datasource;

import libgdx.bf.grid.cell.BaseView;

import java.util.List;

public interface GraveyardDataSource {
    List<BaseView> getGraveyard();

    int getTotalCorpsesCount();
}
