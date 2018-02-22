package main.gui.components.controls;

import main.AV_DataManager;
import main.ability.AE_Manager;
import main.ability.ActionGenerator;
import main.client.cc.CharacterCreator;
import main.client.cc.logic.HeroCreator;
import main.content.CONTENT_CONSTS2.FACTION;
import main.content.*;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.enums.entity.ItemEnums.ITEM_RARITY;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.rules.rpg.PrincipleMaster;
import main.game.core.game.Game;
import main.game.module.adventure.travel.EncounterMaster;
import main.gui.builders.TabBuilder;
import main.launch.ArcaneVault;
import main.simulation.SimulationManager;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.DC_Formulas;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TreeMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.content.BfObjPropGenerator;
import main.system.content.ContentGenerator;
import main.system.math.DC_MathManager;
import main.system.math.Formula;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.TimerTaskMaster;
import main.system.threading.WaitMaster;
import main.system.threading.Weaver;
import main.utilities.search.TypeFinder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelManager {

    private static final long BACK_UP_PERIOD = 300000;
    private static final long RELOAD_PERIOD = 20000;
    private static final int WAIT_PERIOD = 2000;

    private static AV_DataManager manager;
    private static boolean auto;
    private static boolean saving;
    private static boolean autoSaveOff;
    private static int classId;
    private static List<ObjType> idSet = new ArrayList<>();
    private static boolean backupOnLaunch;
    private static List<String> masteriesToCleanUp = Arrays.asList("Spellcraft",
            "Spellcraft", "Spellcraft", "Affliction");

    public static void addParent() {
        add(false);

    }

    public static void addUpgrade() {
        add(true);
    }

    public static void findType() {
        ObjType type = TypeFinder.findType(false);
        if (type == null) {
            return;
        }
        adjustTreeTabSelection(type);
    }

    public static void adjustTreeTabSelection(ObjType type) {
        adjustTreeTabSelection(type, true);
    }

    public static void generateFactions() {
        for (FACTION u : FACTION.values()) {
            ObjType newType = new ObjType();
            ArcaneVault.getGame().initType(newType);
            newType.setProperty(PROPS.UNIT_POOL, u.getUnits());
            newType.setProperty(PROPS.UNIT_TYPES, u.getUnits());
            newType.setProperty(G_PROPS.NAME, u.toString());
            newType.setProperty(G_PROPS.TYPE, DC_TYPE.FACTIONS.toString());
            newType.setImage(u.getImage());
            // newType.setProperty(PROPS.ALLY_FACTIONS, u.getAllyFactions());
            newType.setProperty(G_PROPS.FACTION_GROUP, u.getGroup());
            newType.setProperty(G_PROPS.GROUP, u.getGroup());
            DataManager.addType(u.toString(), DC_TYPE.FACTIONS, newType);

        }
    }

    public static void adjustTreeTabSelection(ObjType type, boolean select) {
        int code =
                // type.getOBJ_TYPE_ENUM().getCode();
                ListMaster.getIndexString(new ArrayList<>(ArcaneVault.getMainBuilder().getTabBuilder()
                        .getTabNames()), type.getOBJ_TYPE(), true);

        ArcaneVault.getMainBuilder().getTabBuilder().getTabbedPane().setSelectedIndex(code);
        // ArcaneVault.getMainBuilder().getEditViewPanel().selectType(type);

        // Class<?> ENUM_CLASS =
        // EnumMaster.findEnumClass(type.getOBJ_TYPE_ENUM().getGroupingKey().getName());
        List<String> list = EnumMaster.findEnumConstantNames(type.getOBJ_TYPE_ENUM()
                .getGroupingKey().getName());
        int index = ListMaster.getIndexString(list, type.getGroupingKey(), true);
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS) {
            if (index > 5) {
                index--;
            }
        }
        TabBuilder tb = ArcaneVault.getMainBuilder().getTabBuilder().getSubTabs(code);
        tb.getTabbedPane().setSelectedIndex(index);
        // getOrCreate path for node? I could keep some map from type to path...
        if (!select) {
            if (tb.getTree().getTreeSelectionListeners().length != 1) {
                return;
            }
            TreeSelectionListener listener = tb.getTree().getTreeSelectionListeners()[0];
            tb.getTree().removeTreeSelectionListener(listener);
            try {
                TreeMaster.collapseTree(tb.getTree());
                DefaultMutableTreeNode node = TreeMaster.findNode(tb.getTree(), type.getName());
                if (node == null ) {
//                    tb.getTabbedPane().setSelectedIndex(prevIndex);
                    return ;
                }
                tb.getTree().setSelectionPath(new TreePath(node.getPath()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                tb.getTree().addTreeSelectionListener(listener);
            }
            return;
        }

        TreeMaster.collapseTree(tb.getTree());
        DefaultMutableTreeNode node = TreeMaster.findNode(tb.getTree(), type.getName());
        tb.getTree().setSelectionPath(new TreePath(node.getPath()));
    }

    public static void add() {
        add(null);
    }

    public static void save(ObjType type, String valName) {
        getAV_Manager().save(type);
    }

    public static void back(ObjType type) {
        getAV_Manager().back(type);
    }

    public static void add(final Boolean upgrade) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                addType(upgrade);
            }
        });
    }

    private static void addType(Boolean upgrade_parent_new) {
        DefaultMutableTreeNode node = ArcaneVault.getMainBuilder().getSelectedNode();
        String selected = ArcaneVault.getMainBuilder().getSelectedTabName();

        if (ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM().isTreeEditType() ) {
            AE_Manager.saveTreeIntoXML(ArcaneVault.getSelectedType());
        }
        String newName = DialogMaster
                .inputText("New type's name:", node.getUserObject().toString());
        if (newName == null) {
            return;
        }
        ArcaneVault.getMainBuilder().getTreeBuilder().newType(newName, node, selected,
                upgrade_parent_new);

        SoundMaster.playStandardSound(STD_SOUNDS.CLOSE);
        ArcaneVault.setDirty(true);
    }

    public static void toggle() {
        setAutoSaveOff(!isAutoSaveOff());
        ArcaneVault.setSimulationOn(!ArcaneVault.isSimulationOn());
        ArcaneVault.getMainBuilder().getEditViewPanel().AE_VIEW_TOGGLING = !ArcaneVault
                .getMainBuilder().getEditViewPanel().AE_VIEW_TOGGLING;

    }

    public static void edit() {

        setAutoSaveOff(!isAutoSaveOff());

        if (!ArcaneVault.isSimulationOn()) {
            ArcaneVault.setSimulationOn(true);
            return;
        }

        CharacterCreator.addHero(SimulationManager.getUnit(new ObjType(ArcaneVault
                .getSelectedType())));
        ArcaneVault.setSimulationOn(false);

    }

    public static void backUp() {
        XML_Writer.setBackUpMode(true);
        try {
            saveAll();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            XML_Writer.setBackUpMode(false);
        }
    }

    public static void save(OBJ_TYPE obj_type) {

        if (obj_type.isTreeEditType() || obj_type == DC_TYPE.ABILS) {
            AE_Manager.saveTreesIntoXML();
        }
        if (!auto) {
            checkTypeModifications(obj_type);
        } else {
            if (obj_type == DC_TYPE.CHARS) {
                XML_Reader.checkHeroesAdded();
            }
        }
        try {
            XML_Writer.writeXML_ForTypeGroup(obj_type);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
            return;
        }

    }

    private static void generateNewArmorParams() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.ARMOR)) {
            ContentGenerator.generateArmorParams(type);
        }
    }

    private static void updateSpells() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            ContentGenerator.generateSpellParams(type);
        }
    }

    private static void generateNewWeaponParams() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.WEAPONS)) {
            ContentGenerator.generateWeaponParams(type);
        }
    }


    private static void checkTypeModifications(OBJ_TYPE obj_type) {
        if (obj_type == DC_TYPE.CHARS || obj_type == DC_TYPE.BF_OBJ
                || obj_type == DC_TYPE.UNITS) {

            if (obj_type == DC_TYPE.CHARS || obj_type == DC_TYPE.UNITS) {
                for (ObjType type : DataManager.getTypes(obj_type)) {
                    DC_ContentManager.addDefaultValues(type, false);
                }
            }


                if (obj_type == DC_TYPE.CHARS) {
                for (ObjType type : DataManager.getTypes(obj_type)) {
                    // ContentGenerator.generateArmorPerDamageType(type, null);
                    if (type.getGroup().equals("Background")) {
                        PrincipleMaster.initPrincipleIdentification(type);
                    }
                }
            }
        }
        checkPrincipleProcessing(obj_type);

        if (obj_type == DC_TYPE.PARTY) {
            ContentGenerator.adjustParties();
        }
        if (obj_type == DC_TYPE.SPELLS) {
            updateSpells();
        }
        if (obj_type == DC_TYPE.ARMOR) {
            generateNewArmorParams();
        } else if (obj_type == DC_TYPE.WEAPONS) {
            generateNewWeaponParams();
        } else if (obj_type == DC_TYPE.DEITIES) {
            for (ObjType type : DataManager.getTypes(obj_type))
            // if (type.getGroup().equals("Background"))
            {
                PrincipleMaster.initPrincipleIdentification(type);
            }
        } else if (obj_type == DC_TYPE.ENCOUNTERS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                type.setParam(PARAMS.POWER_MINIMUM, EncounterMaster.getMinimumPower(type));
                type.setParam(PARAMS.POWER_BASE, EncounterMaster.getPower(type, null));
                type.setParam(PARAMS.POWER_MAXIMUM, EncounterMaster.getPower(type, false));
            }
        } else if (obj_type == DC_TYPE.ACTIONS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                ActionGenerator.addDefaultSneakModsToAction(type);
            }
        } else if (obj_type == DC_TYPE.SKILLS) {
            if (isSkillSdAutoAdjusting()) {
                autoAdjustSkills();
            }

        } else if (obj_type == DC_TYPE.CLASSES)
        // if (isClassAutoAdjustingOn())
        {
            autoAdjustClasses();
        }

        if (obj_type == DC_TYPE.ACTIONS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                if (type.checkProperty(G_PROPS.ACTION_TYPE, ActionEnums.ACTION_TYPE.STANDARD_ATTACK.toString())) {
                    DC_ContentManager.addDefaultValues(type, false);
                }
            }
        }
        if (obj_type == DC_TYPE.SPELLS) {
            for (ObjType type : DataManager.getTypes(obj_type))
            // if (type.getIntParam(PARAMS.XP_COST) == 0)
            {
                if (!type.getGroup().equals(StringMaster.STANDARD)) {
                    type.setParam(PARAMS.XP_COST, type.getIntParam(PARAMS.SPELL_DIFFICULTY)
                            * DC_Formulas.XP_COST_PER_SPELL_DIFFICULTY);
                } else {
                    type.setParam(PARAMS.XP_COST, type.getIntParam(PARAMS.SPELL_DIFFICULTY)
                            * DC_Formulas.XP_COST_PER_SPELL_DIFFICULTY);
                }
            }
        }

        if (obj_type == DC_TYPE.WEAPONS || obj_type == DC_TYPE.ARMOR) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                DC_ContentManager.addDefaultValues(type, false);
            }
        }
        if (obj_type == DC_TYPE.BF_OBJ) {
            generateBfObjProps();
        }
        if ( obj_type.isTreeEditType() || obj_type == DC_TYPE.CHARS || obj_type == DC_TYPE.UNITS) {

            if (obj_type == DC_TYPE.CHARS) {
                // XML_Reader.checkHeroesAdded();
            }

            for (ObjType type : DataManager.getTypes(obj_type)) {
//                if (C_OBJ_TYPE.ITEMS.equals(obj_type)) {
//                    initRarity(type);
//                }
                if (obj_type == DC_TYPE.CHARS) {
                    if (!type.isInitialized()) {
                        Game.game.initType(type);
                    }

                    type.setParam(PARAMS.POWER, DC_MathManager.getUnitPower(type));
                    if (StringMaster.isEmpty(type.getProperty(PROPS.OFFHAND_NATURAL_WEAPON))) {
                        type.setProperty((PROPS.OFFHAND_NATURAL_WEAPON), PROPS.NATURAL_WEAPON
                                .getDefaultValue());
                    }
                    if (ArcaneVault.isSimulationOn()) {
                        int girth = 0;
                        Unit unit = SimulationManager.getUnit(type);
                        RACE race = unit.getRace();
                        if (race != null) {
                            switch (race) {
                                case DEMON:
                                    girth = 200;
                                    if (unit.getBackground() == HeroEnums.BACKGROUND.INFERI_WARPBORN) {
                                        girth = 80;
                                    }
                                    break;
                                case ELF:
                                    girth = 50;
                                    break;
                                case GOBLINOID:
                                    girth = 135;
                                    break;
                                case VAMPIRE:
                                case HUMAN:
                                    girth = 100;
                                    break;
                                case DWARF:
                                    girth = 175;
                                    break;
                            }
                        }
                        if (type.getGroup().equals(StringMaster.BACKGROUND)) {
                            LogMaster.setOff(true);
                            try {

                                unit.resetDefaultAttrs();
                            } catch (Exception e) {

                            } finally {
                                LogMaster.setOff(false);
                            }

                        } else {
                            girth += DataManager.getType(HeroCreator.BASE_HERO, DC_TYPE.CHARS)
                                    .getIntParam(PARAMS.GIRTH);
                        }
                        if (!type.getName().equals(HeroCreator.BASE_HERO)) {
                            type.setParam(PARAMS.GIRTH, girth);
                        }

                    }
                } else {
                    type.setParam(PARAMS.TOTAL_XP, DC_MathManager.getUnitXP(type));

                    // XML_Transformer.adjustProgressionToWeightForm(type,
                    // true);
                    // XML_Transformer.adjustProgressionToWeightForm(type,
                    // false);
                }
            }

        }
    }

    private static void initRarity(ObjType type) {
        if (type.getProperty(PROPS.ITEM_RARITY).isEmpty()){
            type.setProperty(PROPS.ITEM_RARITY,  StringMaster.getWellFormattedString(ITEM_RARITY.COMMON.name()));
        }
    }


    private static void setDefaults(ObjType type) {
        // TODO Auto-generated method stub

    }

    private static void checkPrincipleProcessing(OBJ_TYPE obj_type) {
        if (obj_type == DC_TYPE.DEITIES || obj_type == DC_TYPE.SKILLS
                || obj_type == DC_TYPE.CLASSES || obj_type == DC_TYPE.CHARS
                || obj_type == DC_TYPE.UNITS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
//                PrincipleMaster.processPrincipleValues(type);
            }
        }

    }

    private static void generateBfObjProps() {
        for (ObjType t : DataManager.getTypes(DC_TYPE.BF_OBJ)) {
            BfObjPropGenerator.generateBfObjProps(t);
        }
    }

    private static void generateBfObjParams() {
        // for (ObjType t : DataManager.getTypes(OBJ_TYPES.BF_OBJ)) {
        // ContentGenerator.generateBfObjParams(t);
        // }

    }

    public static void autoAdjustClasses() {
        classId = 0;
        idSet.clear();
        // getChildren(), parentCirlce
        for (ObjType type : DataManager.getTypes(DC_TYPE.CLASSES)) {
            autoAdjustClass(type);
        }
    }

    private static void autoAdjustClass(ObjType type) {

        // String passives = type.getProperty(G_PROPS.PASSIVES);
        // for (String passive : StringMaster.open(passives)) {
        // if (passive.contains("paramBonus")) {
        // //shouldn't overlap, really, nevermind...
        //
        // if (passive.contains("paramMod"))
        // +="%";
        // }
        // passives.replace(passive+ ";", "");
        // passives.replace(passive, "");
        // }
        // type.setProperty(G_PROPS.PASSIVES, passives);

        // String attrString = "";
        // String paramString = "";
        // for (PARAMETER portrait : type.getParamMap().keySet()) {
        // String value = type.getParams(portrait);
        // if (value.isEmpty())
        // continue;
        // if (value.equals("0"))
        // continue;
        // if (ContentManager.isValueForOBJ_TYPE(OBJ_TYPES.CHARS, portrait)) {
        // if (portrait.isAttribute())
        // attrString += portrait.getName() + StringMaster.wrapInParenthesis(value) +
        // ";";
        // else
        // paramString += portrait.getName() + StringMaster.wrapInParenthesis(value) +
        // ";";
        //
        // type.setParam(portrait, 0);
        // }
        // }
        // type.addProperty(PROPS.ATTRIBUTE_BONUSES, attrString);
        // type.addProperty(PROPS.PARAMETER_BONUSES, paramString);
        // String value = type.getProperty(PROPS.ATTRIBUTE_BONUSES);
        // if (value.contains(";;")) {
        // String replace = value.replace(";;", ";");
        // type.setProperty(PROPS.ATTRIBUTE_BONUSES,
        // StringMaster.cropLast(replace, 1));
        // }
        // value = type.getProperty(PROPS.PARAMETER_BONUSES);
        // if (value.contains(";;")) {
        // String replace = value.replace(";;", ";");
        // type.setProperty(PROPS.PARAMETER_BONUSES,
        // StringMaster.cropLast(replace, 1));
        //
        // }

        // TODO remove empty

        ObjType parent = DataManager.getParent(type);
        if (parent == null) {
            parent = DataManager.getType(type.getProperty(PROPS.BASE_CLASSES_ONE),
                    DC_TYPE.CLASSES);
        }
        if (parent != null) {
            autoAdjustClass(parent);
            type.setParam(PARAMS.CIRCLE, parent.getIntParam(PARAMS.CIRCLE) + 1);
            // inherit principles

            if (!type.checkProperty(G_PROPS.PRINCIPLES)) {
                type.setProperty(G_PROPS.PRINCIPLES, parent.getProperty(G_PROPS.PRINCIPLES));

            }

        } else {
            type.setParam(PARAMS.CIRCLE, 0);
        }

        resetAltBaseTypes(type);

        if (idSet.contains(type)) {
            return;
        }
        int id = StringMaster.getInteger(type.getProperty(G_PROPS.ID));
        if (id % 2 != 1) {
            type.setProperty(G_PROPS.ID, "" + classId);
            classId += 2;
            idSet.add(type);
        }

        // multiclass?

        // subling count
        // id - ?

        // link variant?
        // type.getIntParam(PARAMS.CIRCLE) - parent.getIntParam(PARAMS.CIRCLE)
    }

    private static void resetAltBaseTypes(ObjType type) {
        String altBases = type.getProperty(PROPS.ALT_BASE_LINKS);
        if (!altBases.isEmpty()) {
            String altBaseNames = "";
            for (String s : StringMaster.open(altBases)) {
                altBaseNames += VariableManager.removeVarPart(s) + ";";
            }
            type.setProperty(PROPS.ALT_BASE_TYPES, "" + altBaseNames);
        }
    }

    public static void autoAdjustSkills() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SKILLS)) {
            autoAdjustSkill(type);
        }
    }

    private static void autoAdjustSkill(ObjType type) {

        int sd = type.getIntParam(PARAMS.SKILL_DIFFICULTY);
        if (sd == 0) {
            sd = DC_Formulas.getSkillDifficultyForXpCost(type.getIntParam(PARAMS.XP_COST));
            type.setParam(PARAMS.SKILL_DIFFICULTY, sd);
        }

        String reqs = type.getProperty(PROPS.REQUIREMENTS);
        if (reqs.contains("Principles")) {
            String cleanedReqs = reqs;
            for (String sub : StringMaster.open(reqs)) {
                if (!sub.contains("Principles")) {
                    continue;
                }
                cleanedReqs = cleanedReqs.replace(sub, "");
                cleanedReqs = cleanedReqs.replace(";;", ";");
            }
            type.setProperty(PROPS.REQUIREMENTS, cleanedReqs);
            LogMaster.log(1, reqs + "; cleanedReqs =" + cleanedReqs);
        }

        ObjType parent = DataManager.getParent(type);
        if (parent != null && parent != type) {
            autoAdjustSkill(parent);
            Integer parentSd = parent.getIntParam(PARAMS.SKILL_DIFFICULTY);
            if (type.getIntParam(PARAMS.CIRCLE) - parent.getIntParam(PARAMS.CIRCLE) != 1) {
                String childSd = "2*x-sqrt(x)-0.01*x^2";
                childSd = childSd.replace("x", parent.getParam(PARAMS.SKILL_DIFFICULTY));
                Formula formula = new Formula(childSd);
                sd = formula.getInt();
                // + RandomWizard.getRandomIntBetween(-1, 1)

                while (getMinCircle(sd) - getMinCircle(parentSd) < 1) {
                    sd++;
                }

                while (getMinCircle(sd) - getMinCircle(parentSd) > 1) {
                    sd--;
                }
                LogMaster.setOff(false);
                LogMaster.log(1, type.getName() + "'s difficulty auto-set: "
                        + sd + " from " + parent.getName() + "'s " + parentSd);
                type.setParam(PARAMS.SKILL_DIFFICULTY, sd);

            }
        }

        int xpCost = DC_Formulas.calculateFormula(DC_Formulas.XP_COST_PER_SKILL_DIFFICULTY, sd);
        xpCost = xpCost - xpCost % 5;
        type.setParam(PARAMS.XP_COST, xpCost);

        int circle = getMinCircle(sd);
        type.setParam(PARAMS.CIRCLE, circle);

        resetAltBaseTypes(type);

        if (!type.getParamMap().get(PARAMS.RANK_MAX.getName()).isEmpty()) {
            return;
        }

        // updated?
        if (circle == 0) {
            type.setParam(PARAMS.RANK_MAX, 5);
        } else if (circle == 1) {
            type.setParam(PARAMS.RANK_MAX, 3);
        } else if (circle == 2) {
            type.setParam(PARAMS.RANK_MAX, 2);
        }
    }

    private static int getSdForSkillCircle(int circle, boolean min) {
        if (!min) {
            circle++;
        }
        int sd = 36;
        while (true) {
            sd--;
            if (getMinCircle(sd) == circle) {
                break;
            }
            if (sd <= 0) {
                return 0;
            }
        }
        if (!min) {
            sd--;
        }

        return sd;
    }

    private static int getMinCircle(int sd) {
        if (sd > 48) {
            return 6;
        }
        if (sd > 35) {
            return 5;
        } else if (sd > 25) {
            return 4;
        } else if (sd > 18) {
            return 3;
        } else if (sd > 12) {
            return 2;
        } else if (sd > 8) {
            return 1;
        }
        return 0;
    }

    private static boolean isSkillSdAutoAdjusting() {
        return true;
    }

    public static void save() {
        if (ArcaneVault.getSelectedOBJ_TYPE() == DC_TYPE.ABILS) {
            VariableManager.setVariableInputRequesting(JOptionPane.showConfirmDialog(null,
                    "Do you want to set variables manually?") == JOptionPane.YES_OPTION);
        }
        Weaver.inNewThread(new Runnable() {
            @Override
            public void run() {
                save(ArcaneVault.getSelectedOBJ_TYPE());
            }
        });
        VariableManager.setVariableInputRequesting(false);
        return;

    }

    public static void remove() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                removeType();
            }

        });
    }

    private static void removeType() {
        if (ArcaneVault.getSelectedType().getOBJ_TYPE_ENUM() == DC_TYPE.ABILS) {
            AE_Manager.typeRemoved(ArcaneVault.getSelectedType());
        }
        ArcaneVault.getMainBuilder().getTreeBuilder().remove();
        ArcaneVault.setDirty(true);

        SoundMaster.playStandardSound(STD_SOUNDS.ERASE);

    }

    public static void saveAllIfDirty() {
        if (!isAutoSaveOff()) {
            if (ArcaneVault.isDirty()) {
                auto = true;
                try {
                    saveAll();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                } finally {
                    auto = false;
                }
            }
        }

    }

    public static void saveAll() {
        ArcaneVault.setDirty(true);
        SoundMaster.playStandardSound(STD_SOUNDS.DONE);
        Weaver.inNewThread(new Runnable() {
            public void run() {
                saveAllTypes();
            }
        });

    }


    private static void saveAllTypes() {
        ArcaneVault.getWorkspaceManager().save();
        if (saving) {
            return;
        }
        saving = true;
        try {
            if (ArcaneVault.isMacroMode()) {

                for (String type : XML_Reader.getTypeMaps().keySet()) {
                    save(ContentManager.getOBJ_TYPE(type));

                }
            } else {
                for (String type : XML_Reader.getTypeMaps().keySet()) {
                    OBJ_TYPE objType = ContentManager.getOBJ_TYPE(type);
                    if (auto) {
                        if (objType == DC_TYPE.PARTY) {
                            continue;
                        }
                    }
                    save(objType);
                    if (auto) {
                        WaitMaster.WAIT(WAIT_PERIOD);
                    }
                }
            }

            SoundMaster.playStandardSound(STD_SOUNDS.CHECK);
            ArcaneVault.setDirty(false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            saving = false;
        }
    }

    public static void checkReload() {
        if (ArcaneVault.isDirty()) {
            return;
        }
        reload();
    }

    public static void reload() {
        XML_Reader.readTypes(ArcaneVault.isMacroMode());
        ArcaneVault.getGame().initObjTypes();
    }

    public static void startReloading() {
        TimerTaskMaster.newTimer(new ModelManager(), "checkReload", null, null, RELOAD_PERIOD);

    }

    public static void startSaving() {
        if (backupOnLaunch) {
            Weaver.inNewThread(true, new Runnable() {
                @Override
                public void run() {
                    backUp();
                }
            });
        }

        TimerTaskMaster.newTimer(new ModelManager(), "saveAllIfDirty", null, null, BACK_UP_PERIOD);
    }

    public static void startBackingUp() {
        TimerTaskMaster.newTimer(new ModelManager(), "backUp", null, null, BACK_UP_PERIOD);

    }

    public static AV_DataManager getAV_Manager() {
        if (manager == null) {
            manager = new AV_DataManager();
        }
        return manager;
    }

    public static void setManager(AV_DataManager manager) {
        ModelManager.manager = manager;
    }

    public static boolean isAutoSaveOff() {
        return autoSaveOff;
    }

    public static void setAutoSaveOff(boolean autoSaveOff) {
        ModelManager.autoSaveOff = autoSaveOff;
    }

    public static void addToWorkspace() {
        addToWorkspace(false);
    }

    public static void addToWorkspace(boolean alt) {
        for (ObjType objType : ArcaneVault.getSelectedTypes()) {
            if (alt) {
                objType.setWorkspaceGroup(WORKSPACE_GROUP.DEMO);
            }
            boolean result = ArcaneVault.getWorkspaceManager().addTypeToActiveWorkspace(
             objType);
            if (!result) {
                ChangeEvent sc = new ChangeEvent(ArcaneVault.getMainBuilder().getTabBuilder()
                 .getWorkspaceTab().getTabs());
                ArcaneVault.getMainBuilder().getTabBuilder().stateChanged(sc);
            }
        }

    }

    public static void undo() {
        ObjType selectedType = ArcaneVault.getSelectedType();
        if (selectedType != null) {
            back(selectedType);
        }
        refresh();

    }

    public static void refresh() {
        ArcaneVault.getMainBuilder().getTree().getTreeSelectionListeners()[0].valueChanged(null);
    }

    public static void addDefaultValues(boolean alt) {
        for (ObjType sub : DataManager.getTypes()) {
            DC_ContentManager.addDefaultValues(sub);
        }
        if (!alt)
            saveAll();
    }
}
