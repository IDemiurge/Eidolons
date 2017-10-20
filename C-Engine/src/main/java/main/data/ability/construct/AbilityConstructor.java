package main.data.ability.construct;

import main.ability.*;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.values.properties.G_PROPS;
import main.data.XLinkedMap;
import main.data.ability.Mapper;
import main.data.xml.XML_Converter;
import main.elements.targeting.FixedTargeting;
import main.elements.targeting.Targeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
import main.entity.obj.BfObj;
import main.entity.type.XmlHoldingType;
import main.game.core.game.Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.Err;
import main.system.auxiliary.log.LogMaster;
import main.system.text.TextParser;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbilityConstructor {
    private static final String ACTIVE_ABILITY = "ActiveAbility";
    private static final String PASSIVE_ABILITY = PassiveAbility.class.getSimpleName();

    private static final String ONESHOT_ABILITY = OneshotAbility.class.getSimpleName();

    private static final String TARGETING = "Targeting";
    private static final String EFFECTS = "Effect";
    private static Map<Game,Map<Integer, Map<String, AbilityObj>>> safeCache = new XLinkedMap<>();

    //if Game is reinitialized, static id-caches must be destroyed!
    //TODO get rid of static
    public static Map<Integer, Map<String, AbilityObj>> getAbilCache() {
        Map<Integer, Map<String, AbilityObj>> cache = safeCache.get(Game.game);
        if (cache==null ){
            safeCache.clear();
            cache = new XLinkedMap<>();
            safeCache.put(Game.game, cache);
        }
        return cache;
    }

    /**
     * a method per OBJ_TYPE? passives and actives
     *
     */

    @SuppressWarnings("rawtypes")
    public static Abilities constructAbilities(Node node) {
        Abilities abilities = new Abilities();
        List<Node> nodeList = XML_Converter.getNodeList(node);
        if (nodeList.isEmpty())
            return abilities;
        Node unwrappedNode = nodeList.get(0);
        while (nodeList.size() < 2 //?!
         && unwrappedNode.getNodeName().equals(Mapper.ABILITIES)) {
            nodeList = XML_Converter.getNodeList(unwrappedNode);
            if (nodeList.isEmpty())
                return abilities;
            unwrappedNode = nodeList.get(0);
        }

        for (Node NODE : nodeList) {
            Ability abil = constructAbility(NODE);
            abilities.add(abil); // return Abilities
            abil.setXml(XML_Converter.getStringFromXML(node));
        }

        // for (Node ABILITIES : XML_Converter.getNodeList(node)) {
        // if (!ABILITIES.getFirstChild().getNodeName()
        // .equals(Mapper.ABILITIES)) {
        // abilities.add(constructAbility(ABILITIES));
        // } else
        //
        // for (Node NODE : XML_Converter.getNodeList(ABILITIES)) {
        // // if there are multiple abilities in this node? TODO
        // abilities.add(constructAbility(NODE)); // return Abilities
        // // and override
        // // add()
        // } // the problem was that Abilities seems to have a single child that
        // is in fact 2 passives?
        // }
        return abilities;

    }

    private static Ability constructAbility(Node node) {

        Effect effects = null;
        Targeting targeting = null;
        for (Node NODE : XML_Converter.getNodeList(node)) {
            if (NODE.getNodeName().equals(EFFECTS) || NODE.getNodeName().contains(EFFECTS)) {
                effects = constructEffects(NODE);

            }

            if (NODE.getNodeName().equals(TARGETING) || NODE.getNodeName().contains(TARGETING)) {
                targeting = constructTargeting(NODE);

            }
        }
        if (effects == null) {
            LogMaster.log(1,
             "null abil effects!");
            effects = new Effects();
        }

        if (targeting == null) {
            LogMaster.log(1,
             "null abil targeting!");
            targeting = new FixedTargeting();
        }

        Ability abil=null ;
        if (node.getNodeName().equals(ACTIVE_ABILITY)) {
            abil= new ActiveAbility(targeting, effects);
        } else
        if (node.getNodeName().equals(ONESHOT_ABILITY)) {
            abil= new OneshotAbility(targeting, effects);
        } else
        if (node.getNodeName().equals(PASSIVE_ABILITY)) {
            abil= new PassiveAbility(targeting, effects);
        }
        abil.setXml(XML_Converter.getStringFromXML(node));
        return abil;
    }

    private static Targeting constructTargeting(Node node) {

        return (Targeting) ConstructionManager.construct(node);
    }

    private static Effect constructEffects(Node node) {
        Effect effects = (Effect) ConstructionManager.construct(node);
        if (effects == null) {
            Err.info("null effects! " + node.toString());
        }
        // effects.setDoc(node);
        return effects;
    }

    public static void constructXml(XmlHoldingType type) {
        if (type.getDoc() == null) {
            String xml = type.getXml();
            Node doc = null;
            try {
                doc = XML_Converter.getDoc(xml);
            } catch (Exception e) {
                // XML_Master.printOutXml(xml); //in a readable form...
                e.printStackTrace();
            }
            type.setDoc(doc);

        }
    }

    public static void construct(AbilityType type) {
        constructXml(type);
        Abilities abilities = constructAbilities(type.getDoc());
        type.setAbilities(abilities);

    }

    public static synchronized void constructObj(Entity entity) {
        checkAbilsMerge(entity, true);
        checkAbilsMerge(entity, false);
        constructPassives(entity);
        if (!entity.getGame().isSimulation()) {
            constructActives(entity);
            entity.setConstructed(true);
        }
    }

    private static void checkAbilsMerge(Entity entity, boolean PASSIVES) {
        G_PROPS property = PASSIVES ? G_PROPS.PASSIVES : G_PROPS.ACTIVES;
        String prop = entity.getProperty(property);
        List<String> list = StringMaster.openContainer(prop);
        List<String> addList = new LinkedList<>();
        List<String> removeList = new LinkedList<>();
        for (String s : list) {
            String varPart = VariableManager.getVarPart(s);
            if (varPart.isEmpty()) {
                continue;
            }
            if (varPart.contains(",")) {
                continue;
            }
            String abilName = VariableManager.removeVarPart(s);
            // TODO WHAT IF THERE ARE SOME NON-PARAMETER ARGUMENTS OR JUST 2+ OF
            // THEM?
            for (String s1 : StringMaster.openContainer(prop.replaceFirst(abilName, ""))) {
                if (!VariableManager.removeVarPart(s1).equalsIgnoreCase(abilName)) {
                    continue;
                }
                String mergedAbility = abilName
                 + StringMaster.wrapInParenthesis(StringMaster
                 .wrapInParenthesis(StringMaster.cropParenthesises(varPart))
                 + "+"
                 + StringMaster.wrapInParenthesis(StringMaster
                 .cropParenthesises(varPart)));

                removeList.add(s1);
                removeList.add(s);
                addList.add(mergedAbility);
            }

        }
        for (String s : removeList) {
            list.remove(s);
        }
        for (String s : addList) {
            list.add(s);
        }
        if (list.isEmpty())
            entity.removeProperty(property);
        else entity.setProperty(property, StringMaster.constructContainer(list));

    }

    public static void constructActives(Entity entity) {
        List<ActiveObj> abilities;
        boolean action = !(entity instanceof BfObj);
        if (action) {
            abilities = getAbilitiesList(G_PROPS.ACTIVES, entity, false);
            if (abilities != null) {
                entity.setActives(abilities);
            }
        } else {
            if (!entity.getType().isModel()) {
                entity.getGame().getActionManager().resetActions(entity);
            }
        }

    }

    public static void constructPassives(Entity entity) {
        Chronos.mark("construct passives for " + entity.getName());
        List<AbilityObj> passives = new LinkedList<>();

        for (String passive : StringMaster.openContainer(entity.getProperty(G_PROPS.PASSIVES))) {
            AbilityObj abil = getPassive(passive, entity);
            if (abil != null) {
                passives.add(abil);
            }
        }
        entity.setPassives(passives);
        Chronos.logTimeElapsedForMark(Chronos.CONSTRUCT, "construct passives for "
         + entity.getName());
        // so the passives cannot be removed via OBJ-REMOVAL...
        // they can be removed if some item is removed
    }

    public static AbilityObj getPassive(String passive, Entity entity) {
        AbilityObj ability = null;

        if (getAbilCache().get(entity.getId()) != null) {
            try {
                ability = getAbilCache().get(entity.getId()).get(passive);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // if (entity.getGame().isSimulation())
            if (ability == null || TextParser.checkHasRefs(passive)) {
                try {
                    TextParser.setAbilityParsing(true);
                    entity.getRef().setID(KEYS.INFO, entity.getId());
                    passive = TextParser.parse(passive, entity.getRef());
                    ability = getAbilCache().get(entity.getId()).get(passive);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    TextParser.setAbilityParsing(false);
                }
            }
            if (ability != null) {
                return ability;
            }
        } else {
            getAbilCache().put(entity.getId(), new HashMap<>());
        }
        return (AbilityObj) newAbility(passive, entity, true);
    }

    private static List<ActiveObj> getAbilitiesList(G_PROPS prop, Entity entity, boolean passive) {
        if (entity.getProperty(prop) == "") {
            return null;
        }
        List<ActiveObj> list = new LinkedList<>();

        for (String abilTypeName : StringMaster.openContainer(entity.getProperty(prop))) {
            if (abilTypeName.isEmpty()) continue;
            ActiveObj ability;
            ability = newAbility(abilTypeName, entity, passive);
            if (ability != null) {
                list.add(ability);
            }
        }

        return list;
    }

    public static Abilities getAbilities (String data, Ref ref) {
        Abilities a = new Abilities();
        for (String abilTypeName : StringMaster.openContainer(
         data)) {
            a.add(new AbilityObj(VariableManager.getVarType(abilTypeName), ref));
        }
        return a;
    }


    private static TARGETING_MODE getTargetingMode(Entity entity) {
        try {
            return (new EnumMaster<TARGETING_MODE>().retrieveEnumConst(TARGETING_MODE.class, entity
             .getProperty(G_PROPS.TARGETING_MODE)));
        } catch (Exception e) {
            return null;
        }

    }

    public static ActiveObj newAbility(String abilTypeName, Entity entity, boolean passive) {
        TextParser.setAbilityParsing(true);
        if (!passive) {
            TextParser.setActiveParsing(true);
        }
        Ref ref = entity.getRef().getCopy();
        ref.setID(KEYS.INFO, entity.getId());
        try {
            if (passive) {
                abilTypeName = TextParser.parse(abilTypeName, ref, TextParser.ABILITY_PARSING_CODE,
                 TextParser.VARIABLE_PARSING_CODE);
            } else {
                abilTypeName = TextParser.parse(abilTypeName, ref, TextParser.ACTIVE_PARSING_CODE,
                 TextParser.VARIABLE_PARSING_CODE, TextParser.ABILITY_PARSING_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TextParser.setAbilityParsing(false);
            TextParser.setActiveParsing(false);
        } // TODO
        AbilityType type = null;
        try {

            type = getNewAbilityType(abilTypeName);
        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(1, "Failed to create new ability: " + abilTypeName);

        }
        if (type == null) {
            return null;
        }
        // if (type.getAbilities() == null)
        construct(type);
        Map<String, AbilityObj> map = getAbilCache().get(entity.getId());
        if (map == null) {
            map = new HashMap<>();
            getAbilCache().put(entity.getId(), map);
        }
        AbilityObj ability = map.get(abilTypeName);
        if (ability == null) {
            if (passive) {
                ability = new PassiveAbilityObj(type, entity.getRef(), entity.getOwner(), entity
                 .getGame());
            } else {
                ability = new ActiveAbilityObj(type, entity.getRef(), entity.getOwner(), entity
                 .getGame());
            }

            entity.getGame().getState().addObject(ability);
            map.put(abilTypeName, ability);

        }
        return ability;

    }

    private static AbilityType getNewAbilityType(String typeName) {
        return VariableManager.getVarType(typeName);

    }

}
