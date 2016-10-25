package main.content;

import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;

public interface OBJ_TYPE {

    String getName();

    PROPERTY getGroupingKey();

    PROPERTY getSubGroupingKey();

    int getCode();

    String getImage();

    void setImage(String image);

    boolean isHidden();

    void setHidden(boolean hidden);

    boolean isTreeEditType();

    PARAMETER getParam();

    PROPERTY getUpgradeRequirementProp();

    boolean isHeroTreeType();
}
