package main.game.battlecraft.logic.meta.scenario.hq;

import main.entity.LightweightEntity;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 5/22/2017.
 */
public class HqShop extends LightweightEntity implements ShopInterface{

    public HqShop(ObjType type) {
        super(type);
    }

    @Override
    public List<String> getTabs() {
        return null;
    }

    @Override
    public List<String> getGroupLists(String tabName) {
        return null;
    }

    @Override
    public List<String> getTextures(String groupList) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getGold() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }
}