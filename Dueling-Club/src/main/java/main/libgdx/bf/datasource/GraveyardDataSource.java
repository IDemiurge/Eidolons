package main.libgdx.bf.datasource;

import main.libgdx.bf.BaseView;

import java.util.List;

public interface GraveyardDataSource {
    List<BaseView> getGraveyard();

    int getTotalCorpsesCount();
}
