package main.game.core.state;

import main.ability.effects.Effect;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XML_Writer;
import main.elements.triggers.Trigger;
import main.entity.obj.Obj;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 8/1/2017.
 */
public class Saver {
    /*
    apart from objects, what should be there?
    dungeon
    mission should be initialized first... but some 'mission data' must be there of course

    save types
    >> single mission/battle
    >> scenario (e.g. demo)
     */
    public static final String OBJ_NODE = "Objects";
    public static final String PROPS_NODE = "Props";
    public static final String PARAMS_NODE = "Params";
    public static final String TRIGGERS_NODE = "Triggers";
    public static final String EFECTS_NODE = "Effects";
    public static final String DUNGEON_NODE = "Dungeon";
    public static final String WRAPPER_NODE = "Save";
    public static final boolean TEST_MODE = false;

    public static String save(String saveName) {
        String xml = getXmlFromState(DC_Game.game.getState());
        //overwrite
        XML_Writer.write(XML_Converter.wrap(WRAPPER_NODE, xml),
         getSavePath() + saveName + ".xml");
        return xml;
    }

    public static String getSavePath() {
        return PathFinder.getXML_PATH() + "duel-club\\saves\\";
    }

    public static String getTriggersNodeXml(DC_GameState state) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(XML_Converter.openXml(TRIGGERS_NODE));
        for (Trigger sub : state.getTriggers()) {
            builder.append(sub.toXml());
        }
        builder.append(XML_Converter.closeXml(TRIGGERS_NODE));
        return builder.toString();
    }

    public static String getEffectsNodeXml(DC_GameState state) {
        StringBuilder builder = new StringBuilder(3000);
        builder.append(XML_Converter.openXml(EFECTS_NODE));
        for (Effect sub : state.getEffects()) {
            builder.append(sub.toXml());
        }
        //effect layers?
        builder.append(XML_Converter.closeXml(EFECTS_NODE));
        return builder.toString();
    }

    private static String getObjNodeXml(DC_GameState state) {
        StringBuilder builder = new StringBuilder(80000);
        builder.append(XML_Converter.openXml(OBJ_NODE));
        for (OBJ_TYPE TYPE : state.getObjMaps().keySet()) {
            if (state.getObjMaps().get(TYPE).isEmpty())
                continue;
            builder.append(XML_Converter.openXml(TYPE.getName()));
            for (Integer id : state.getObjMaps().get(TYPE).keySet()) {
                Obj obj = state.getObjMaps().get(TYPE).get(id);
                obj.setProperty(G_PROPS.ID, id + "");
                builder.append(XML_Converter.openXmlFormatted(obj.getName()));
                builder.append(XML_Converter.openXml(PROPS_NODE));
                for (PROPERTY property : obj.getPropMap().keySet()) {
                    if (property.isDynamic() ||
                     !obj.getProperty(property).equals(obj.getType().getProperty(property))) {
                        //only write values that aren't at base
                        builder.append(XML_Formatter.getValueNode(obj, property));

                    }
                }

                builder.append(XML_Converter.closeXml(PROPS_NODE));
                builder.append(XML_Converter.openXml(PARAMS_NODE));
                for (PARAMETER parameter : obj.getParamMap().keySet()) {
                    if (parameter.isDynamic() ||
                     !obj.getParam(parameter).equals(obj.getType().getParam(parameter))) {
                        //only write values that aren't at base
                        builder.append(XML_Formatter.getValueNode(obj, parameter));
                    }
                }
                builder.append(XML_Converter.closeXml(PARAMS_NODE));
                //REF !
                builder.append(XML_Converter.closeXmlFormatted(obj.getName()));
            }
            builder.append(XML_Converter.closeXml(TYPE.getName()));
        }
        builder.append(XML_Converter.closeXml(OBJ_NODE));
        return builder.toString();
    }


    public static String getXmlFromState(DC_GameState state) {
        StringBuilder builder = new StringBuilder(90000);
        state.getManager().allToBase();
        builder.append(getDungeonNodeXml(state)).
         append(getObjNodeXml(state)).
         append(getEffectsNodeXml(state)).
         append(getTriggersNodeXml(state));


        return builder.toString();
    }

    private static String getDungeonNodeXml(DC_GameState state) {
        return XML_Converter.wrap(DUNGEON_NODE,
         state.getGame().getDungeonMaster().getDungeon().toXml());


    }

}
