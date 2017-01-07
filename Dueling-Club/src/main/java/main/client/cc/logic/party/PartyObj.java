package main.client.cc.logic.party;

import main.client.battle.arcade.ArcadeManager.ARCADE_STATUS;
import main.client.cc.logic.HeroCreator;
import main.client.dc.Simulation;
import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.*;
import main.content.parameters.MACRO_PARAMS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.travel.MacroParty;
import main.game.logic.macro.travel.RestMaster;
import main.game.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PartyObj extends Obj {
    // TODO ++ EMBLEM!
    // arcade/skirmish mode

    // used in dc dynamically and in hc as well; to be save into xml and loaded
    // back

    public List<DC_HeroObj> members = new LinkedList<>();
    public List<DC_HeroObj> mercs = new LinkedList<>();
    public DC_HeroObj leader;
    private DC_HeroObj middleHero;
    private Map<DC_HeroObj, Coordinates> partyCoordinates;
    private MacroParty macroParty;

    // private ARCADE_STATUS arcadeStatus;

    public PartyObj(ObjType type, DC_HeroObj hero) {
        super(type, hero.getOwner(), type.getGame(), new Ref(type.getGame()));
        this.leader = hero;
        addMember(leader);
        setProperty(G_PROPS.IMAGE, leader.getImagePath(), true);
        setProperty(PROPS.MEMBERS, leader.getProperty(G_PROPS.NAME), true);
        setProperty(PROPS.LEADER, leader.getProperty(G_PROPS.NAME), true);
        this.type.setParam(PARAMS.LEVEL, leader.getParam(PARAMS.LEVEL), true);
        setParam(PARAMS.LEVEL, leader.getIntParam(PARAMS.LEVEL), true);
        setOBJ_TYPE_ENUM(OBJ_TYPES.PARTY);
        if (leader.isHero())
            leader.setMainHero(true);
        leader.setLeader(true);
    }

    /**
     * load
     */
    public PartyObj(ObjType type) {
        super(type, Player.NEUTRAL, type.getGame(), new Ref(type.getGame()));
        initMembers();
        if (!getMembers().isEmpty())
            this.leader = getMembers().get(0); // how safe is that?
        // HeroCreator.initHero(type.getProperty(PROPS.LEADER));
    }

    public String getMemberString() {
        return StringMaster.constructContainer(ListMaster.toNameList(getMembers()));
    }

    public void addFallenHeroes(Collection<Entity> fallenHeroes) {
        addProperty(PROPS.STATS_FALLEN_HEROES, DataManager.toStringList(fallenHeroes), false);
        // TODO
    }

    public void addUnitsSlain(Collection<Entity> slainUnits) {
        addProperty(PROPS.STATS_SLAIN_ENEMIES, DataManager.toStringList(slainUnits), false);

    }

    public void initMembers() {
        members.clear();
        for (String heroName : StringMaster.openContainer(type.getProperty(PROPS.MEMBERS))) {
            addMember(HeroCreator.initHero(heroName));
        }
    }

    public void resetMembers() {
        for (DC_HeroObj hero : members) {
            hero.fullReset(Simulation.getGame());
        }
    }

    public void addMember(DC_HeroObj hero) {
        members.add(hero);
        addProperty(PROPS.MEMBERS, hero.getName());
        type.addProperty(PROPS.MEMBERS, hero.getName());
        hero.getRef().setID(KEYS.PARTY, getId());
    }

    public void removeMember(DC_HeroObj hero) {
        removeProperty(PROPS.MEMBERS, hero.getName());
        type.removeProperty(PROPS.MEMBERS, hero.getName());
        members.remove(hero);
        hero.getGame().remove(hero);
    }

    public void subtractGlory(int glory) {
        modifyParameter(PARAMS.GLORY, glory, true);
    }

    public void addGlory(int glory) {
        glory = glory / members.size();
        modifyParameter(PARAMS.GLORY, glory, true);

    }

    @Override
    public void init() {

    }

    public List<DC_HeroObj> getMembers() {
        return members;
    }

    public void setMembers(List<DC_HeroObj> members) {
        this.members = members;
    }

    public DC_HeroObj getLeader() {
        return leader;
    }

    public void setLeader(DC_HeroObj leader) {
        this.leader = leader;
    }

    public boolean isArcade() {
        return type.checkProperty(G_PROPS.GROUP, StringMaster.ARCADE);
    }

    public ARCADE_STATUS getArcadeStatus() {

        return new EnumMaster<ARCADE_STATUS>().retrieveEnumConst(ARCADE_STATUS.class,
                getProperty(PROPS.ARCADE_STATUS));

    }

    public int getGlory() {
        return getIntParam(PARAMS.GLORY);
    }

    public DC_HeroObj getRandomMember() {

        return members.get(RandomWizard.getRandomListIndex(members));
    }

    public DC_HeroObj getMiddleHero() {
        return middleHero;
    }

    public void setMiddleHero(DC_HeroObj middleHero) {
        this.middleHero = middleHero;
    }

    public DC_HeroObj getNextHero(DC_HeroObj hero) {
        int i = getMembers().indexOf(hero);
        if (i == -1)
            return null;
        if (i == getMembers().size() - 1)
            i = -1;
        return getMembers().get(i + 1);
    }

    @Override
    protected void addDynamicValues() {
    }

    @Override
    public void resetPercentages() {
    }

    @Override
    public void afterEffects() {
//        resetBattleSpirit();
//        resetOrganization();
// TODO [QUICK FIX]
        setParam(PARAMS.ORGANIZATION, 100);
        setParam(PARAMS.BATTLE_SPIRIT, 100);

        for (DC_HeroObj m : members) {
            m.setParam(PARAMS.ORGANIZATION,
                    // Math.min(
                    // DC_Formulas.INTELLIGENCE_ORGANIZATION_CAP_MOD
                    // * m.getIntParam(PARAMS.INTELLIGENCE),
                    MathMaster.applyMod(getIntParam(PARAMS.ORGANIZATION), m
                            .getIntParam(PARAMS.INTEGRITY)));
            m.setParam(PARAMS.BATTLE_SPIRIT, MathMaster.applyMod(getIntParam(PARAMS.BATTLE_SPIRIT),
                    !m.isHero() ? 100 : m.getIntParam(PARAMS.INTEGRITY)));
            m.resetMorale();
        }
    }

    public boolean checkTactics() {
        // could a parameter defining the 'quality' of formation and its max
        // size
        return checkMembersProperty(PROPS.SKILLS, "Battle Formation");
    }

    public void initOrganization() {
        int minIntelligence = Integer.MAX_VALUE;
        int maxTactics = Integer.MIN_VALUE;
        int intelligence = 0;
        int i = 0;
        for (DC_HeroObj hero : members) {
            if (hero.isDead())
                continue;
            i++;
            if (hero.getIntParam(PARAMS.TACTICS_MASTERY) > maxTactics)
                maxTactics = hero.getIntParam(PARAMS.TACTICS_MASTERY);

            intelligence += hero.getIntParam(PARAMS.INTELLIGENCE);
            if (hero.getIntParam(PARAMS.INTELLIGENCE) < minIntelligence)
                minIntelligence = hero.getIntParam(PARAMS.INTELLIGENCE);
        }
        if (i == 0)
            return;
        int avrgIntelligence = intelligence / i;

        int organization = Math.round(new Float(100) - (new Float(10) - new Float(maxTactics) / 5)
                * i + 50 * Math.min(5 + new Float(maxTactics) / i, new Float(avrgIntelligence) / i)
                * (maxTactics + minIntelligence) / 100);
        setParam(PARAMS.ORGANIZATION, organization);

        if (!getOwner().isMe()) {
            setParamMin(PARAMS.ORGANIZATION, 60);
            setParamMax(PARAMS.ORGANIZATION, 200);
        }
        // TODO PERHAPS IT CANNOT BE HIGHER THAN UNIT'S INTELLIGENCE FOR EACH
        // INDIVIDUALLY!
    }

    public void initBattleSpirit() {
        // if (!getOwner().isMe()) {
        // setParam(PARAMS.BATTLE_SPIRIT, 100);
        // return;
        // }



        int principleClashes = 0;
        int sharedPrinciples = 0;
        int deityClashes = 0;
        int sharedDeities = 0;
        int i = 0;
        int pc_mod = 100;
        int sp_mod = 100;
        int dc_mod = 100;
        int sd_mod = 100;
        for (DC_HeroObj hero : members) {
            // principle clash?
            // getOrCreate condition, check per unit, add up on false!
            if (hero.isDead())
                continue;
            i++;
            ref.setMatch(hero.getId());

            for (PRINCIPLES principle : PRINCIPLES.values()) {
                Integer hero_identity = hero.getIntParam(DC_ContentManager
                        .getIdentityParamForPrinciple(principle));
                for (DC_HeroObj m : members) {
                    if (m == hero || m.isDead())
                        continue;
                    Integer member_identity = m.getIntParam(DC_ContentManager
                            .getIdentityParamForPrinciple(principle));
                    if (hero_identity > 0) {
                        if (member_identity < 0)
                            principleClashes += Math.min(hero_identity, Math.abs(member_identity));
                        else
                            sharedPrinciples += Math.min(hero_identity, (member_identity));
                    } else {
                        if (member_identity > 0)
                            principleClashes += Math.min(Math.abs(hero_identity), member_identity);
                        else
                            sharedPrinciples += Math.min(Math.abs(hero_identity), Math
                                    .abs(member_identity));
                    }

                    if (m.getDeity() == hero.getDeity())
                        sharedDeities += 2;
                    else if (m.getDeity().getAllyDeities().contains(hero.getDeity())) {
                        sharedDeities++;
                    } else if (m.getDeity().getEnemyDeities().contains(hero.getDeity())) {
                        deityClashes++;
                    }

                }
                principleClashes -= hero.getIntParam(PARAMS.PRINCIPLE_CLASHES_REMOVED);
                pc_mod -= pc_mod * hero.getIntParam(PARAMS.PRINCIPLE_CLASHES_REDUCTION) / 100;
                dc_mod -= dc_mod * hero.getIntParam(PARAMS.DEITY_CLASHES_REDUCTION) / 100;
                sp_mod += sp_mod * hero.getIntParam(PARAMS.SHARED_PRINCIPLES_BOOST);
                sd_mod += hero.getIntParam(PARAMS.SHARED_DEITIES_BOOST);

            }
            setParam(PARAMS.PRINCIPLE_CLASHES, principleClashes);
            pc_mod = Math.max(pc_mod, 1);
            sp_mod = Math.max(sp_mod, 1);
            dc_mod = Math.max(dc_mod, 1);
            sd_mod = Math.max(sd_mod, 1);

            int maxLeadership = getMaxParam(ContentManager
                    .getMasteryScore(PARAMS.LEADERSHIP_MASTERY));
            // if (!leader.getOwner().isMe()) {
            // // mod /= 2; ???
            // maxLeadership += 10;
            // // ++ TODO special bonus per wave type!
            // }
            // int max= Math.max(getMaxParam(PARAMS.PRINCIPLE_CLASH_MAXIMUM),
            // 5);
            // principleClashes = Math.min(principleClashes, max);

            int principleClashesPenalty = pc_mod / 100 * (principleClashes);
            int sharedPrinciplesBonus = sp_mod / 100 * (sharedPrinciples);
            int deityClashesPenalty = 15 * dc_mod / 100 * (deityClashes);
            int sharedDeitiesBonus = 5 * sd_mod / 100 * (sharedDeities);
            int leadershipBonus = Math.round((float) Math.sqrt(i) * maxLeadership);
            int principleBonus = MathMaster.getMinMax(sharedPrinciplesBonus
                    - principleClashesPenalty, -75, 200);
            int deityBonus = MathMaster.getMinMax(sharedDeitiesBonus - deityClashesPenalty, -50,
                    150);
            int battleSpirit = 100 + principleBonus + deityBonus
                    // should it be PC^2*5 instead?
                    + leadershipBonus;
            // TODO

            setParam(PARAMS.BATTLE_SPIRIT, Math.min(300, battleSpirit));

            if (!getOwner().isMe()) {
                setParamMin(PARAMS.ORGANIZATION, 60);
                setParamMax(PARAMS.ORGANIZATION, 200);
            }
        }
    }

    public void resetOrganization() {
        initOrganization();
        // should it be really dynamic?

    }

    public void resetBattleSpirit() {

        initBattleSpirit();
        // is it otherwise constant?
        // perhaps it should grow or degradate over time?
    }

    @Override
    public void toBase() {
        super.toBase();
        if (game.isSimulation())
            afterEffects();
        else {
            // apply macro mode effects!
            if (MacroManager.isMacroGame())
                for (DC_HeroObj h : members)
                    RestMaster.applyMacroModeContinuous(h);
        }
        setParam(MACRO_PARAMS.CONSUMPTION, getParamSum(MACRO_PARAMS.CONSUMPTION, false));
        setParam(MACRO_PARAMS.TRAVEL_SPEED, getMinParam(MACRO_PARAMS.TRAVEL_SPEED, false));
        setParam(MACRO_PARAMS.EXPLORE_SPEED, getMaxParam(MACRO_PARAMS.EXPLORE_SPEED, false));

        if (macroParty != null) {
            for (PROPERTY p : propMap.keySet())
                if (p instanceof MACRO_PROPS)
                    macroParty.setProperty(p, getProperty(p));
            for (PARAMETER p : paramMap.keySet())
                if (p instanceof MACRO_PARAMS)
                    macroParty.setParam(p, getParam(p));
        }
        // calculateWeight();
        // calculateSpeed();
    }

    public int getMaxParam(PARAMETER p) {
        return getMaxParam(p, false);
    }

    public int getMinParam(PARAMETER p) {
        return getMinParam(p, false);
    }

    public int getParamSum(PARAMETER p) {
        return getParamSum(p, false);
    }

    public int getMinParam(PARAMETER p, boolean units) {
        int min = Integer.MAX_VALUE;
        for (DC_HeroObj hero : members) {
            if (hero.getIntParam(p) < min)
                min = hero.getIntParam(p);
        }
        if (units)
            for (DC_HeroObj unit : getMercs()) {
                if (unit.getIntParam(p) < min)
                    min = unit.getIntParam(p);
            }
        return min;
    }

    public int getMaxParam(PARAMETER p, boolean units) {
        int max = Integer.MIN_VALUE;
        for (DC_HeroObj hero : members) {
            if (hero.getIntParam(p) > max)
                max = hero.getIntParam(p);
        }
        if (units)
            for (DC_HeroObj unit : getMercs()) {
                if (unit.getIntParam(p) < max)
                    max = unit.getIntParam(p);
            }
        return max;
    }

    public int getParamSum(PARAMETER p, boolean units) {
        int sum = 0;
        for (DC_HeroObj hero : members) {
            sum += hero.getIntParam(p);
        }
        if (units)
            for (DC_HeroObj unit : getMercs()) {
                sum += unit.getIntParam(p);
            }
        return sum;
    }

    public boolean checkMembersProperty(PROPS p, String value) {
        for (DC_HeroObj hero : members) {
            if (hero.checkProperty(p, value))
                return true;
        }
        return false;
    }

    public List<DC_HeroObj> getMercs() {
        return mercs;
    }

    public void setMercs(List<DC_HeroObj> mercs) {
        this.mercs = mercs;
    }

    public Map<DC_HeroObj, Coordinates> getPartyCoordinates() {
        return partyCoordinates;
    }

    public void setPartyCoordinates(Map<DC_HeroObj, Coordinates> partyCoordinates) {
        this.partyCoordinates = partyCoordinates;
    }

    public MacroParty getMacroParty() {
        return macroParty;
    }

    public void setMacroParty(MacroParty macroParty) {
        this.macroParty = macroParty;
    }

}
