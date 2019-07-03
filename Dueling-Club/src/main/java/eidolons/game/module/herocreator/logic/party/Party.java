package eidolons.game.module.herocreator.logic.party;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.macro.entity.party.MacroParty;
import eidolons.game.module.herocreator.logic.HeroCreator;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.math.MathMaster;

import java.util.*;
import java.util.stream.Collectors;

public class Party extends Obj {
    // TODO ++ EMBLEM!
    // arcade/skirmish mode

    // used in dc dynamically and in hc as well; to be save into xml and loaded
    // back

    public List<Unit> members = new ArrayList<>();
    public Unit leader;
    private Unit middleHero;
    private Map<Unit, Coordinates> partyCoordinates;
    private MacroParty macroParty;

    // private ARCADE_STATUS arcadeStatus;

    public Party(ObjType type, Unit hero) {
        super(type, hero.getOwner(), type.getGame(), new Ref(type.getGame()));
        addMember(leader);
        setLeader(hero);
    }

    /**
     * load
     */
    public Party(ObjType type) {
        super(type, Player.NEUTRAL, type.getGame(), new Ref(type.getGame()));
        initMembers();
        // HeroCreator.initHero(type.getProperty(PROPS.LEADER));
    }

    @Override
    public void addToState() {
        super.addToState();
    }

    public void initMembers() {
        members.clear();
        addToState();
        for (String heroName : ContainerUtils.openContainer
         (type.getProperty(PROPS.MEMBERS))) {
            //TODO refactor
            if (DC_Game.game.getMetaMaster() != null)
                heroName = DC_Game.game.getMetaMaster().getPartyManager().
                 checkLeveledHeroVersionNeeded(heroName);

            try {
                addMember(HeroCreator.initHero(heroName));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (getLeader()==null )
            if (!getMembers().isEmpty()) {
                setLeader(getMembers().get(0)); // how safe is that?
        }
    }

    public String getMemberString() {
        return ContainerUtils.constructContainer(ListMaster.toNameList(getMembers()));
    }

    public void addFallenHeroes(Collection<Entity> fallenHeroes) {
        addProperty(PROPS.STATS_FALLEN_HEROES, DataManager.toStringList(fallenHeroes), false);
        // TODO
    }

    public void addUnitsSlain(Collection<Entity> slainUnits) {
        addProperty(PROPS.STATS_SLAIN_ENEMIES, DataManager.toStringList(slainUnits), false);

    }


    public void resetMembers() {
        for (Unit hero : members) {
            hero.fullReset(Simulation.getGame());
        }
    }

    public void addMember(Unit hero) {
        if (hero == null)
            return;
        if (members.contains(hero))
            return;
        if (checkDuplicateHero(hero))
            return;
        if (leader == null)
            setLeader(hero);
        members.add(hero);
        addProperty(PROPS.MEMBERS, hero.getName()); //no duplicates ?
//        type.addProperty(PROPS.MEMBERS, hero.getName());
        if (!getType().getProperty(G_PROPS.EMBLEM).isEmpty())
            hero.setProperty(G_PROPS.EMBLEM, getType().getProperty(G_PROPS.EMBLEM), true);
        hero.getRef().setID(KEYS.PARTY, getId());
    }

    private boolean checkDuplicateHero(Unit hero) {
        for (Unit sub : getMembers()) {
            if (hero.getName().contains(" v"))
                if (sub.getName().contains(StringMaster.
                 cropLastSegment(hero.getName(), " "))) {
                    return true;
                }
        }
        return false;
    }

    public void removeMember(Unit hero) {
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

    public List<Unit> getMembers() {
        members.removeIf(m -> m == null);
        return members;
    }

    public void setMembers(List<Unit> members) {
        this.members = members;
    }

    public Unit getLeader() {
        return leader;
    }

    public void setLeader(Unit leader) {
        this.leader = leader;
        setProperty(G_PROPS.IMAGE, leader.getImagePath(), true);
//      WTF??? TODO   setProperty(PROPS.MEMBERS, leader.getProperty(G_PROPS.NAME), true);
        setProperty(PROPS.LEADER, leader.getProperty(G_PROPS.NAME), true);
        this.type.setParam(PARAMS.LEVEL, leader.getParam(PARAMS.LEVEL), true);
        setParam(PARAMS.LEVEL, leader.getIntParam(PARAMS.LEVEL), true);
        setOBJ_TYPE_ENUM(DC_TYPE.PARTY);
        if (leader.isHero()) {
            leader.setMainHero(true);
        }
        leader.setLeader(true);
    }

    @Override
    public Player getOwner() {
        if (owner != null)
            return owner;
        return leader.getOwner();
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
        if (members != null && owner != null)
            members.forEach(hero -> hero.setOwner(owner));
    }

    @Override
    public void setOriginalOwner(Player owner) {
        this.originalOwner = owner;
        if (members != null && owner != null)
            members.forEach(hero -> hero.setOriginalOwner(owner));
    }

    public boolean isArcade() {
        return type.checkProperty(G_PROPS.GROUP, StringMaster.ARCADE);
    }

    public int getGlory() {
        return getIntParam(PARAMS.GLORY);
    }

    public Unit getRandomMember() {

        return members.get(RandomWizard.getRandomIndex(members));
    }

    public Unit getMiddleHero() {
        return middleHero;
    }

    public void setMiddleHero(Unit middleHero) {
        this.middleHero = middleHero;
    }

    public Unit getNextHero(Unit hero) {
        int i = getMembers().indexOf(hero);
        if (i == -1) {
            return null;
        }
        if (i == getMembers().size() - 1) {
            i = -1;
        }
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

        for (Unit m : members) {
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
        for (Unit hero : members) {
            if (hero.isDead()) {
                continue;
            }
            i++;
            if (hero.getIntParam(PARAMS.TACTICS_MASTERY) > maxTactics) {
                maxTactics = hero.getIntParam(PARAMS.TACTICS_MASTERY);
            }

            intelligence += hero.getIntParam(PARAMS.INTELLIGENCE);
            if (hero.getIntParam(PARAMS.INTELLIGENCE) < minIntelligence) {
                minIntelligence = hero.getIntParam(PARAMS.INTELLIGENCE);
            }
        }
        if (i == 0) {
            return;
        }
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
        for (Unit hero : members) {
            // principle clash?
            // getOrCreate condition, preCheck per unit, add up on false!
            if (hero.isDead()) {
                continue;
            }
            i++;
            ref.setMatch(hero.getId());

            for (PRINCIPLES principle : HeroEnums.PRINCIPLES.values()) {
                Integer hero_identity = hero.getIntParam(DC_ContentValsManager
                 .getIdentityParamForPrinciple(principle));
                for (Unit m : members) {
                    if (m == hero || m.isDead()) {
                        continue;
                    }
                    Integer member_identity = m.getIntParam(DC_ContentValsManager
                     .getIdentityParamForPrinciple(principle));
                    if (hero_identity > 0) {
                        if (member_identity < 0) {
                            principleClashes += Math.min(hero_identity, Math.abs(member_identity));
                        } else {
                            sharedPrinciples += Math.min(hero_identity, (member_identity));
                        }
                    } else {
                        if (member_identity > 0) {
                            principleClashes += Math.min(Math.abs(hero_identity), member_identity);
                        } else {
                            sharedPrinciples += Math.min(Math.abs(hero_identity), Math
                             .abs(member_identity));
                        }
                    }

                    if (m.getDeity() == hero.getDeity()) {
                        sharedDeities += 2;
                    } else if (m.getDeity().getAllyDeities().contains(hero.getDeity())) {
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

            int maxLeadership = getMaxParam(ContentValsManager
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
        if (game.isSimulation()) {
            afterEffects();
        } else {
            // apply macro mode effects!
//            if (MacroManager.isMacroGame()) {
//                for (Unit h : members) {
//                    RestMasterOld.applyMacroModeContinuous(h);
//                }
//            }
        }
        setParam(MACRO_PARAMS.CONSUMPTION, getParamSum(MACRO_PARAMS.CONSUMPTION, false));
        setParam(MACRO_PARAMS.TRAVEL_SPEED, getMinParam(MACRO_PARAMS.TRAVEL_SPEED, false));
        setParam(MACRO_PARAMS.EXPLORE_SPEED, getMaxParam(MACRO_PARAMS.EXPLORE_SPEED, false));

        if (macroParty != null) {
            for (PROPERTY p : propMap.keySet()) {
                if (p instanceof MACRO_PROPS) {
                    macroParty.setProperty(p, getProperty(p));
                }
            }
            for (PARAMETER p : paramMap.keySet()) {
                if (p instanceof MACRO_PARAMS) {
                    macroParty.setParam(p, getParam(p));
                }
            }
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
        for (Unit hero : members) {
            if (hero.getIntParam(p) < min) {
                min = hero.getIntParam(p);
            }
        }

        return min;
    }

    public int getMaxParam(PARAMETER p, boolean units) {
        int max = Integer.MIN_VALUE;
        for (Unit hero : members) {
            if (hero.getIntParam(p) > max) {
                max = hero.getIntParam(p);
            }
        }

        return max;
    }

    public int getParamSum(PARAMETER p, boolean units) {
        int sum = 0;
        for (Unit hero : members) {
            sum += hero.getIntParam(p);
        }

        return sum;
    }

    public boolean checkMembersProperty(PROPS p, String value) {
        for (Unit hero : members) {
            if (hero.checkProperty(p, value)) {
                return true;
            }
        }
        return false;
    }


    public Map<Unit, Coordinates> getPartyCoordinates() {
        return partyCoordinates;
    }

    public void setPartyCoordinates(Map<Unit, Coordinates> partyCoordinates) {
        this.partyCoordinates = partyCoordinates;
    }


    public void setMacroParty(MacroParty macroParty) {
        this.macroParty = macroParty;
    }


    public String getMission() {
        return getProperty(PROPS.PARTY_MISSION);
    }

    public List<ObjType> getMemberTypes() {
        return ContainerUtils.openContainer(getProperty(PROPS.MEMBERS)).
                stream().map(member -> DataManager.getType(member, DC_TYPE.CHARS)).collect(Collectors.toList());
    }
}
