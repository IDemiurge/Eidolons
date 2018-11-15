package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * Created by JustMe on 11/14/2018.
 */
public interface AbstractValueContainer {
    Label getValueLabel();

    Label getNameLabel();

    void setNameText(CharSequence newText);

    void setNameStyle(LabelStyle labelStyle);

    void setValueStyle(LabelStyle labelStyle);

    void setStyle(LabelStyle labelStyle);

    String getValueText();

    void setValueText(CharSequence newText);

    default Actor getActor(){
        return (Actor) this;
    }
}
