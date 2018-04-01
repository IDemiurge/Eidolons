package eidolons.game.module.adventure.gui.party;

import eidolons.game.module.adventure.entity.MacroParty;
import eidolons.swing.components.panels.ValueBox;
import eidolons.swing.components.panels.page.info.PropertyPage;
import eidolons.swing.components.panels.page.info.element.ValueTextComp;
import eidolons.swing.components.panels.page.log.WrappedTextComp;
import main.content.VALUE;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.swing.generic.components.G_Panel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartyHeader extends G_Panel {
    public final static PROPERTY[] party_props = {MACRO_PROPS.REGION,
     MACRO_PROPS.AREA, MACRO_PROPS.PLACE, MACRO_PROPS.MACRO_STATUS, // route/town?
     MACRO_PROPS.ROUTE,};
    public final static VALUE[] party_params = {MACRO_PARAMS.TRAVEL_SPEED,
     MACRO_PARAMS.EXPLORE_SPEED, MACRO_PARAMS.C_PROVISIONS,
     MACRO_PARAMS.CONSUMPTION,};
    PropertyPage propPage;
    Header header;
    List<ValueBox> boxes = new ArrayList<>();
    List<ValueTextComp> propComps = new ArrayList<>();
    private MacroParty party;
    private WrappedTextComp locationComp;

    public PartyHeader(MacroParty party) {
        // visuals?
        super(VISUALS.PARTY_HEADER); // VISUALS.INFO_PANEL special size! TODO
        this.party = party;
        // propPage = new PropertyPage(Arrays.asList(party_props), party, true);
        // header = new Header(Arrays.asList(party_params), party);
        createComponents();
        // refresh();
    }

    public void createComponents() {
        locationComp = new WrappedTextComp(VISUALS.VALUE_BOX_BIG, true);

        header = new Header(new ArrayList<>(Arrays.asList(party_params)),
         party);

        add(locationComp, "@pos center_x 20, id lc");
        add(header, "@pos center_x lc.y2, id header");

        // status: en route, in town, at place, lost, in flight, in battle..

    }

    public void refresh() {
        boolean enRoute = party.getCurrentRoute() != null;
        try {
            locationComp.setText(getLocationString(enRoute));
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        header.refresh();
        locationComp.refresh();
    }

    private String getLocationString(boolean enRoute) {
        // TODO ++ STATUS!
        String locationString = party.getRegion().getName() + ", "
         + party.getArea().getDisplayedName();
        if (enRoute) {
            locationString = locationString + " , on "
             + party.getCurrentRoute().getDisplayedName() + " to "
             + party.getCurrentDestination().getDisplayedName() + "("
             + party.getIntParam(MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE)
             + "%)"; // TODO getOrCreate hours to go?
        } else {
            locationString = locationString + ", at "
             + party.getCurrentLocation().getDisplayedName();
        }
        return locationString;
    }

}
