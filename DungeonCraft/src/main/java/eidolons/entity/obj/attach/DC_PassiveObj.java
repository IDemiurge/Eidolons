package eidolons.entity.obj.attach;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DC_ContentValsManager.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.entity.ConditionMaster;
import main.system.math.Formula;
import main.system.text.TextParser;

import java.util.HashMap;
import java.util.Map;

// Includes Skills and Classes
    public class DC_PassiveObj extends DC_HeroAttachedObj {

    private boolean paramStringParsed;
    private Map<PARAMETER, String> modMap;
    private Map<PARAMETER, String> bonusMap;
    private final int tier;

        public DC_PassiveObj(ObjType featType, Player originalOwner, GenericGame game, Ref ref) {
        super(featType, originalOwner, game, ref);
        tier = getIntParam(PARAMS.CIRCLE);
    }

    public DC_PassiveObj(ObjType type, Ref ref) {
        this(type, ref.getSourceObj().getOwner(), (GenericGame) ref.getGame(), ref);
    }

    @Override
    protected void addDefaultValues() {
    }

    protected void initHero() {
        String TARGET_KEYWORD = getProperty(G_PROPS.KEYS);
        if (!StringMaster.isEmpty(TARGET_KEYWORD)) {
            setOwnerObj((Unit) getGame().getObjectById(ref.getId(TARGET_KEYWORD)));
        } else {
            super.initHero();
        }
    }

    @Override
    public void apply() {
        if (!checkApplyReqs()) {
            return;
        }
        if (game.isSimulation() || !paramStringParsed) {
            try {
                parseParamBonusString();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        initHero();
        for (ATTRIBUTE attr : DC_ContentValsManager.getAttributeEnums()) {
            initAttr(attr);
        }
        // modifyHeroParameters();
        for (PARAMETER param : DC_ContentValsManager.getFeatModifyingParams()) {
            modifyHeroParam(param);
        }
        try {
            addParamBonuses();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        try {
            applyParamMods();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        activatePassives();
    }

    private boolean checkApplyReqs() {

        if (!checkProperty(PROPS.APPLY_REQS)) {
            return true;
        }

        for (String s : ContainerUtils.open(getProperty(PROPS.APPLY_REQS))) {
            Condition condition = ConditionMaster.toConditions(s);
            if (!condition.preCheck(ref)) {
                return false;
            }
        }

        return true;
    }

    private void parseParamBonusString() {
        String prop = getProperty(PROPS.PARAMETER_BONUSES);
        if (!prop.endsWith(";")) {
            prop = prop + ";";
        }
        prop = prop + getProperty(PROPS.ATTRIBUTE_BONUSES);
        for (String substring : ContainerUtils.open(prop)) {
            // String[] array = substring.split(" ");
            String bonus = // array[array.length - 1];
             VariableManager.getVar(substring);
            boolean mod = false;
            if (bonus.contains("%")) {
                mod = true;
                bonus = bonus.replace("%", "");
            } else if (bonus.contains("mod")) {
                mod = true;
                bonus = bonus.replace("mod", "");
            }
            String paramName = VariableManager.removeVarPart(substring);
            // substring.replace(bonus, "");
            PARAMETER param = ContentValsManager.getPARAM(paramName);
            if (param == null) {
                param = ContentValsManager.getMastery(paramName);
            }
            if (param == null) {
                param = ContentValsManager.findPARAM(paramName);
            }
            if (param == null) {
                param = ContentValsManager.findMastery(paramName);
            }
            if (param == null) {
                continue;
            }
            getRef().setInfoEntity(this);
            bonus = TextParser.parse(bonus, getRef(), TextParser.ABILITY_PARSING_CODE);
            Integer amount = new Formula(bonus).getInt(getRef());// StringMaster.getInteger(bonus);
            if (amount == 0) {
                continue;
            }
            if (mod) {
                getModMap().put(param, bonus);
            } else {
                getBonusMap().put(param, bonus);
            }
            paramStringParsed = true;
        }
        paramStringParsed = true;
    }

    private void addParamBonuses() {
        float quotientSum = 0;
        for (PARAMETER param : getBonusMap().keySet()) {
            Integer amount = NumberUtils.getIntParse(getBonusMap().get(param), getRef());
            float d = (float) amount   ;

            if (param.isAttribute()) {
                quotientSum += d - Math.floor(d);
            } else {
                amount += Math.round(d);
            }
            getHero().modifyParameter(param, amount, getName());
        }
        if (quotientSum != 0) {

            String paramName;

            if (getOBJ_TYPE_ENUM() == DC_TYPE.CLASSES) {
                paramName = DC_ContentValsManager.getMainAttributeForClass(this);
                if (paramName.contains(Strings.AND)) {
                    quotientSum = quotientSum / paramName.split(Strings.AND).length;
                }
            } else {
                paramName = ContainerUtils.openContainer(
                 VariableManager.removeVarPart(getProperty(PROPS.ATTRIBUTE_BONUSES))).get(0);
            }

            int rounded = Math.round(quotientSum);
            getHero().modifyParameter(paramName, "" + rounded);
        }
        // save .x
    }

    private void applyParamMods() {
        for (PARAMETER param : getModMap().keySet()) {
            Integer amount = NumberUtils.getIntParse(modMap.get(param));
            getHero().modifyParamByPercent(param, amount); // TODO feat name for
            // valModMap!
        }
    }

    @Override
    public void setRef(Ref ref) {
        Integer skillId = ref.getId(KEYS.SKILL);
        super.setRef(ref);
        if (skillId != null) {
            ref.setID(KEYS.SKILL, skillId);
        }
        ref.setID(KEYS.ACTIVE, null);
        ref.setID(KEYS.SPELL, null);
    }

    protected boolean isConstructOnInit() {
        return false;
    }

    protected boolean isActivatePassives() {
        return true;
    }

    protected KEYS getKey() {
        return KEYS.SKILL;
    }

    private void initAttr(ATTRIBUTE attr) {
        modifyHeroParam(DC_ContentValsManager.getBaseAttr(attr));
        modifyHeroParam(attr.getParameter());
    }

    private void modifyHeroParam(PARAMETER param) {
        int amount;
        try {
            amount = getIntParam(param);
        } catch (NumberFormatException e) {
            amount = new Formula(getParam(param)).getInt(ref);
        }
        if (amount != 0 && getHero() != null) {
            getHero().modifyParameter(param, amount);
        }
    }

    @Override
    protected void addDynamicValues() {
    }

    @Override
    public void newRound() {
    }

    public Map<PARAMETER, String> getModMap() {
        if (modMap == null) {
            modMap = new HashMap<>();
        }
        return modMap;
    }

    public Map<PARAMETER, String> getBonusMap() {
        if (bonusMap == null) {
            bonusMap = new HashMap<>();
        }
        return bonusMap;
    }

    public void setBonusMap(Map<PARAMETER, String> bonusMap) {
        this.bonusMap = bonusMap;
    }

    public int getTier() {
        return tier;
    }

}
