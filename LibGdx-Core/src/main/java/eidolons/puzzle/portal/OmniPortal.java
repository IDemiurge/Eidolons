package eidolons.puzzle.portal;

import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.panels.TablePanelX;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.MapMaster;

import java.util.HashMap;
import java.util.Map;

public class OmniPortal extends Portal{
    /*
    list of buttons like in menu?
    usage:
    - testing
    - special action (travel between shrines?!)
    - town / Aexenon

     */

    Map<String, String> destinations;

    TablePanelX  destinationsMenu;
    private final Map<Coordinates, Portal> portals= new HashMap<>();

    public OmniPortal(Coordinates coordinates,
                      String data
                      ) {
        super(null , coordinates, PortalMaster.PORTAL_TYPE.DARK, true); //or allow back?
        destinations = //I think I had a parser somewhere
        MapMaster.createStringMap(data);
        //name(coordinate) - and btw, name doesn't really matter at this point!

        destinationsMenu = new TablePanelX();
        for (String name : destinations.keySet()) {
            new SmartTextButton(name, ButtonStyled.STD_BUTTON.MENU, () ->
                    goTo(Coordinates.get(destinations.get(name))));
        }
    }

    @Override
    public boolean checkVisible() {
        return super.checkVisible();
    }

    private void goTo(Coordinates coordinates) {
        pair = getOrCreatePortal(coordinates);
        entered(Core.getMainHero());
        GuiEventManager.trigger(GuiEventType.HIDE_CUSTOM_PANEL, destinationsMenu);
    }

    private Portal getOrCreatePortal(Coordinates coordinates) {
        Portal portal =portals.get(coordinates);
        if (portal == null) {
            portal = new Portal(null, coordinates, type, oneWay);//gotta be closed...
            portals.put(coordinates, portal);
        }

        return portal;
    }

    @Override
    public void entering(Unit unit) {
//update destinations !
        GuiEventManager.trigger(GuiEventType.SHOW_CUSTOM_PANEL, destinationsMenu);
        //fade /blacken ui?
    }



}






















