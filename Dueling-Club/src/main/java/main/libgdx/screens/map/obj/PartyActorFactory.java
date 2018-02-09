package main.libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.entity.obj.unit.Unit;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.GdxColorMaster;
import main.libgdx.screens.map.obj.MapActor.MAP_OBJ_INFO_LEVEL;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PartyActorFactory {
public static class PartyActorParameters{
    public TextureRegion emblem;
    public TextureRegion border;
    public MAP_OBJ_INFO_LEVEL visibility;
    public  Vector2 position;
    public Color color;
    TextureRegion mainTexture;
}
    public static PartyActor get(MacroParty party) {
        PartyActorParameters parameters = new PartyActorParameters();
        Unit leader = party.getLeader();
        parameters.emblem = TextureCache.getOrCreateR(leader.getEmblemPath());
        parameters.position = new Vector2(
         party
//          .getCurrentLocation()
          .getX(),
         party
//          .getCurrentLocation()
          .getY());
        //TODO size?
        parameters.mainTexture =
         TextureCache.getOrCreateR(
         party.getLeader().getImagePath());
        parameters.color=  GdxColorMaster.getColor(party.getLeader().getOwner().getFlagColor());
//        ToolTipManager ;
//      actor.addListener()

        PartyActor actor = new PartyActor(parameters);
        return actor;
    }
}
