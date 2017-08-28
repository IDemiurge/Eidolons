package main.libgdx.bf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface Borderable {
    TextureRegion getBorder();

    void setBorder(TextureRegion texture);

    public boolean isTeamColorBorder();

    public void setTeamColorBorder(boolean teamColorBorder);

    public void setTeamColor(Color teamColor);

    public Color getTeamColor();
}
