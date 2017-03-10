package main.libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface EffectsAndAbilitiesSource {

    List<Pair<TextureRegion, String>> getEffects();

    List<Pair<TextureRegion, String>> getAbilities();
}
