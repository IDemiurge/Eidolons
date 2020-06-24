package eidolons.game.battlecraft.ai.advanced.engagement;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.grid.handlers.GridAnimHandler;
import main.content.enums.rules.VisionEnums;
import main.data.xml.XmlStringBuilder;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;

public class EngageEvent {
    public static Integer ID = 0;
    protected Object arg;
    protected int id;
    protected Unit source;
    protected BattleFieldObject target;
    protected ENGAGE_EVENT type;
    protected VisionEnums.ENGAGEMENT_LEVEL level;
    protected Event.EVENT_TYPE event_type;
    protected String logMsg, soundPath, popupText;
    protected GraphicData graphicData;
    protected Coordinates c;
    protected GridAnimHandler.VIEW_ANIM anim;
    protected VisionEnums.PLAYER_STATUS status;
    // normally we'd only have one of these, DO NOT cluster stuff

    protected float delay;
    //++ float text?

    public EngageEvent(Unit source, BattleFieldObject target, Object... args) {
        this(args);
        this.source = source;
        this.target = target;
    }

    public EngageEvent(Object... args) {
        for (Object o : args) {
            //instance
            if (o instanceof VisionEnums.ENGAGEMENT_LEVEL) {
                this.level = (VisionEnums.ENGAGEMENT_LEVEL) o;
            } else if (o instanceof VisionEnums.PLAYER_STATUS) {
                this.status = (VisionEnums.PLAYER_STATUS) o;
            } else if (o instanceof GridAnimHandler.VIEW_ANIM) {
                this.anim = (GridAnimHandler.VIEW_ANIM) o;
            } else if (o instanceof ENGAGE_EVENT) {
                this.type = (ENGAGE_EVENT) o;
            } else if (o instanceof Event.EVENT_TYPE) {
                this.event_type = (Event.EVENT_TYPE) o;
            } else if (o instanceof GraphicData) {
                this.graphicData = (GraphicData) o;
            } else if (o instanceof Coordinates) {
                this.c = (Coordinates) o;
            } else if (o instanceof Float) {
                this.delay = (Float) o;
            } else {
                arg = o;
            }
        }
        id = ID++;
    }


    public enum ENGAGE_EVENT {
        status_change,
        ai_status_change,
        combat_start,
        combat_end,
        popup,
        log,
        sound,
        view_anim,
        comment,
        camera, engagement_change, precombat, precombat_end, combat_ended, combat_started,

        // IDEA: sync up with script events?
    }

    @Override
    public String toString() {
        return new XmlStringBuilder2().append("Event-" + id +
                ":" +
                "\n").append("source: ", source)
                .append(", target: ", target).append(", type: ", type).append(", event_type: ", event_type).append("arg: ", arg)
                .append(", status: ", status)
                .append(", level: ", level)
                .append(", logMsg: '", logMsg).append(", soundPath: " + soundPath).
                        append(", popupText: ", popupText).append(", graphicData: ", graphicData).
                        append(", c: ", c).append(", anim: ", anim).toString();
    }

    public class XmlStringBuilder2 extends XmlStringBuilder {
        public XmlStringBuilder2 append(String s, Object o) {
            if (o == null) {
                return this;
            }
            if (o instanceof Obj) {
                super.append(s +
                        ((Obj) o).getNameAndCoordinate());
            }
            super.append(s + o);
            return this;
        }

        @Override
        public XmlStringBuilder2 append(String s) {
            return (XmlStringBuilder2) super.append(s);
        }
    }
}
