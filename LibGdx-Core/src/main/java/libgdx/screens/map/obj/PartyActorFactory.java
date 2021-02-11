package libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.macro.entity.party.MacroParty;
import eidolons.macro.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.screens.map.ui.tooltips.PartyTooltip;
import libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PartyActorFactory extends MapObjFactory<PartyActor, MacroParty> {

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

    public PartyActor get(MacroParty party) {
        PartyActorParameters parameters = new PartyActorParameters();
        Unit leader = party.getLeader();
        parameters.party = party;
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
        parameters.color = GdxColorMaster.getColor(party.getLeader().getOwner().getFlagColor());

        PartyActor actor = new PartyActor(parameters);
        actor.addListener(new PartyTooltip(party, actor).getController());
        return actor;
    }

    public static class PartyActorParameters {
        public TextureRegion emblem;
        public TextureRegion border;
        public MAP_OBJ_INFO_LEVEL visibility;
        public Vector2 position;
        public Color color;
        MacroParty party;
        TextureRegion mainTexture;
    }
}
