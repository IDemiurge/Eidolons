package main.libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.entity.obj.unit.Unit;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import main.libgdx.GdxColorMaster;
import main.libgdx.GdxMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.screens.map.ui.tooltips.PartyTooltip;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PartyActorFactory extends MapObjFactory<PartyActor, MacroParty>{

    public static class PartyActorParameters{
        public TextureRegion emblem;
        public TextureRegion border;
        public MAP_OBJ_INFO_LEVEL visibility;
        public  Vector2 position;
        public Color color;
        TextureRegion mainTexture;
    }
    private static PartyActorFactory instance;

    public static PartyActorFactory getInstance() {
        if (instance == null) {
            instance = new PartyActorFactory();
        }
        return instance;
    }



    public static PartyActor getParty(MacroParty party) {
        return getInstance().create(party);
    }
        public   PartyActor get(MacroParty party) {
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
         TextureCache.getOrCreateRoundedRegion(
         party.getLeader().getImagePath());
        parameters.color=  GdxColorMaster.getColor(party.getLeader().getOwner().getFlagColor());

        PartyActor actor = new PartyActor(parameters);
            actor.addListener(new PartyTooltip(party, actor).getController());
        ActorMaster.addMoveToAction(actor, GdxMaster.getWidth(), GdxMaster.getHeight(), 50);
        return actor;
    }
}
