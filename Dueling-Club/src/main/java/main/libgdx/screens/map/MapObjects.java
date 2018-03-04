package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.map.Place;
import main.libgdx.screens.map.obj.*;
import main.system.GuiEventManager;

import static main.system.MapEvent.*;

/**
 * Created by JustMe on 3/4/2018.
 */
public class MapObjects extends Stage {
    private MacroParty mainParty;
    private PartyActor mainPartyActor;

    public MapObjects(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1)
                    if (mainParty != null)
                        mainPartyActor.moveTo(x, y);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        bindEvents();
    }

    protected void bindEvents() {
        GuiEventManager.bind(CREATE_PARTY, param -> {
            MacroParty party = (MacroParty) param.get();
            if (party == null) {
                return;
            }
            PartyActor partyActor = PartyActorFactory.getParty(party);
            addActor(partyActor);

            if (party.isMine()) {
                setMainParty(party);
                setMainPartyActor(partyActor);
            }
        });
        GuiEventManager.bind(CREATE_PLACE, param -> {
            Place place = (Place) param.get();
            PlaceActor placeActor = PlaceActorFactory.getPlace(place);
            addActor(placeActor);
        });
        GuiEventManager.bind(REMOVE_MAP_OBJ, param -> {
            MapActor actor = (MapActor) param.get();
            actor.remove();
        });
    }

    public MacroParty getMainParty() {
        return mainParty;
    }

    public void setMainParty(MacroParty mainParty) {
        this.mainParty = mainParty;
    }

    public PartyActor getMainPartyActor() {
        return mainPartyActor;
    }

    public void setMainPartyActor(PartyActor mainPartyActor) {
        this.mainPartyActor = mainPartyActor;
    }
}
