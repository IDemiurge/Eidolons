package libgdx.screens.map.town.navigation.data;

import java.util.Set;

/**
 * Created by JustMe on 11/21/2018.
 */
public interface Navigable {

    void interact();

    String getTooltip();

    String getDescription();

    String getIconPath();
    String getName();
    String getPreviewImagePath();

    Navigable getParent();
    Set<Navigable > getChildren();
    void setChildren(Set<Navigable > set);

    String getDefaultActionTip();

    default boolean isLeaf(){
        return getChildren().isEmpty();
    }
}
