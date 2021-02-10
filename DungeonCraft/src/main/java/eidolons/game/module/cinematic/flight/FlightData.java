package eidolons.game.module.cinematic.flight;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.consts.libgdx.GdxColorMaster;
import main.system.data.DataUnit;

import static eidolons.game.module.cinematic.flight.FlightData.FLIGHT_VALUE.hue;

public class FlightData extends DataUnit<FlightData.FLIGHT_VALUE> {
    public FlightData(String text) {
        super(text);
    }

    public Color getHue() {
        return GdxColorMaster.getColorByName(getValue(hue));
    }

    /**
     * In LE - on zone level perhaps, accessible by entering a cell, generate script Or maybe platform => flight! So,
     * link Flights to platforms pretty much Interesting: nested edit() - edit another dataUnit when clicking a value?
     * Save as x;y;..
     */
    public enum FLIGHT_VALUE {
        objs_over,   objs_under,
        angle, //
        background,
        camera_shake,
        camera_displace,
        soundscape,
        trail, hue,
        trail_omni, //water waves
       //weightmap via simple text?
        fixed_duration,
        angle_delta, //can do a  bit of turning randomly?
    }

}
