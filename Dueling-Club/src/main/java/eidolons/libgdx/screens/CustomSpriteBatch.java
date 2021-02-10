package eidolons.libgdx.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.content.enums.GenericEnums;

public interface CustomSpriteBatch extends Batch {
    void drawBlack(float alpha, boolean whiteout);

    GenericEnums.BLENDING getBlending();

    void setBlending(GenericEnums.BLENDING blending);

    void resetBlending();

    void shaderReset();

    void resetBlendingLite();
}
