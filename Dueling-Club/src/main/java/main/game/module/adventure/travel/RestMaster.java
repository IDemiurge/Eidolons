package main.game.module.adventure.travel;

import main.content.CONTENT_CONSTS2.MACRO_STATUS;
import main.content.values.properties.MACRO_PROPS;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.screens.map.MapScreen;

/**
 * Created by JustMe on 2/24/2018.
 */
public class RestMaster {

    public static void endCamping(MacroParty party) {
        MacroGame.getGame().getLoop().getTimeMaster().setPlayerCamping(false);
        MacroGame.getGame().getLoop().getTimeMaster().resetSpeed();
    }

    public static void startCamping(MacroParty party) {
        if (party.isMine()) {
            if (MacroGame.getGame().getLoop().getTimeMaster().isPlayerCamping()) {
                endCamping(party);
                return;

            }
            MacroGame.getGame().getLoop().getTimeMaster().setPlayerCamping(true);
            float speed = 60f;
            MacroGame.getGame().getLoop().getTimeMaster().setSpeed(speed);
            party.getMembers().forEach(member -> {
                member.setProperty(MACRO_PROPS.MACRO_STATUS, MACRO_STATUS.CAMPING.toString());
            });
//            MacroGame.getGame().fireEvent(
//             new Event(MAP_EVENT_TYPE.CAMPING_STARTED, party.getRef()));

            //other actions are available in camping mode?
//            party.setDisabled(true);
//            MapScreen.getInstance(). setDarkened(true);
            MapScreen.getInstance().updateInputController();
            //control interrupt? or 'off' action?
            //apply visuals
            //time speed
            //disable controls

        }
        //modes for each hero
        //to be processed by timeMaster
        //disable

    }


}
