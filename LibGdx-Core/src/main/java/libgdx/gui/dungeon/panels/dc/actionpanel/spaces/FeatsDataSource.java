package libgdx.gui.dungeon.panels.dc.actionpanel.spaces;

import java.util.List;

public class FeatsDataSource {
    private List<FeatSpaceDataSource> featSpacesDS;

    public List<FeatSpaceDataSource> getFeatSpacesDS() {
        return featSpacesDS;
    }

    public void setFeatSpacesDS(List<FeatSpaceDataSource> featSpacesDS) {
        this.featSpacesDS = featSpacesDS;
    }

    public CharSequence getSwitchesLeft(boolean spellSpaces) {
        return null;
    }
}
