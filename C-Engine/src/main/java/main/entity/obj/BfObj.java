package main.entity.obj;

import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.entity.OBJ;
import main.game.bf.Coordinates;

public interface BfObj extends OBJ {

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    Coordinates getCoordinates();

    void setCoordinates(Coordinates coordinates);

    UNIT_VISION getUnitVisionStatus();

    PLAYER_VISION getActivePlayerVisionStatus();

    boolean isPlayerDetected();

    boolean isDead();
}
