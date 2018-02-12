package main.libgdx.screens.map.obj;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.GdxMaster;
import main.libgdx.screens.map.obj.PartyActorFactory.PartyActorParameters;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PartyActor extends MapActor {
    private Image emblem;
    private Image modeIcon;

    public PartyActor(PartyActorParameters parameters) {
        super(parameters.mainTexture);
        init(parameters);
    }

    private void init(PartyActorParameters parameters) {
        setPosition(parameters.position.x, parameters.position.y);
        setTeamColor(parameters.color);
        emblem = new Image(parameters.emblem);
        addActor(emblem);
        emblem.setPosition(GdxMaster.right(emblem), GdxMaster.top(emblem) );
       getPortrait(). setSize(96*GdxMaster.getFontSizeMod(),
         96*GdxMaster.getFontSizeMod() );
        emblem.setSize(32*GdxMaster.getFontSizeMod(),
         32*GdxMaster.getFontSizeMod() );
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }
}
