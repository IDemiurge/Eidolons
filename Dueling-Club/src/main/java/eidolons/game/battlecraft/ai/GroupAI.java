package eidolons.game.battlecraft.ai;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.explore.Patrol;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;
import main.content.enums.rules.VisionEnums.ENGAGEMENT_LEVEL;
import main.data.XStack;
import main.entity.obj.MicroObj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GroupAI {
    UNIT_GROUP_TYPE type = UNIT_GROUP_TYPE.CROWD;
    // TODO into    private Map<Unit, Coordinates> knownEnemyCoordinatesMap;
    //    private Map<Unit, List<Coordinates>> suspectedEnemyCoordinatesMap;
    private Unit leader;
    private Unit originalLeader;
    private DequeImpl<Unit> members = new DequeImpl<>();

    private ENGAGEMENT_LEVEL engagementLevel;
    private boolean followLeader;
    private boolean forceBehavior;
    private boolean clockwisePatrol;
    private boolean backAndForth;

    private Coordinates originCoordinates;
    private Object arg;
    private LevelBlock block;
    private int wanderDistance;
    private DIRECTION wanderDirection;
    private Stack<Coordinates> wanderStepCoordinateStack;
    private Patrol patrol;
    private UnitAI.AI_BEHAVIOR_MODE behavior;
    private Encounter encounter;

    public GroupAI() {

    }

    public GroupAI(Unit leader) {
        setLeader(leader);
        add(leader);
    }


    @Override
    public String toString() {
        return "AI_Group: " + ListMaster.toNameList(new ArrayList<>(getMembers())) + "; leader: "
         + leader;
    }

    public void remove(Unit unit) {
        UnitAI unitAI = unit.getUnitAI();
        members.remove(unit);
        unitAI.setGroupAI(null);
    }

    public void add(MicroObj obj) {
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            if (leader == null) {
                setLeader(unit);
                if (originCoordinates == null) {
                    originCoordinates = leader.getCoordinates();
                }
            }
            UnitAI unitAI = unit.getUnitAI();
            if (!members.contains(unit)) {
                members.add(unit);
            }
            unitAI.setGroupAI(this);
            initNewMemberAi(unitAI);
        }

    }

    private void initNewMemberAi(UnitAI unitAI) {
        unitAI.setExplorationMoveSpeedMod(type.getSpeedMod());
    }

    public Unit getLeader() {
        if (originalLeader.canAct()) {
            setLeader(originalLeader);
        } else if (!leader.canAct()) {
            for (Unit member : getMembers()) {
                if (member.canAct()) {
                    setLeader(member);
                }
            }
        }
        return leader;
    }

    public void setLeader(Unit leader) {
        if (this.leader == null) {
            originalLeader = leader;
        }
        this.leader = leader;
    }

    public UNIT_GROUP_TYPE getType() {
        return type;
    }

    public void setType(UNIT_GROUP_TYPE type) {
        this.type = type;
    }

    public Patrol getPatrol() {
        return patrol;
    }

    public void setPatrol(Patrol patrol) {
        this.patrol = patrol;
    }

    public Stack<Coordinates> getWanderStepCoordinateStack() {
        if (wanderStepCoordinateStack == null) {
            wanderStepCoordinateStack = new XStack<>();
        }
        return wanderStepCoordinateStack;
    }

    public DequeImpl<Unit> getMembers() {
        return members;
    }


    public DIRECTION getWanderDirection() {
        if (wanderDirection == null) {
            wanderDirection = getLeader().getFacing().getDirection();
        }
        return wanderDirection;
    }

    public void setWanderDirection(DIRECTION wanderDirection) {
        this.wanderDirection = wanderDirection;
    }

    public boolean isFollowLeader() {
        return followLeader;
    }

    public void setFollowLeader(boolean followLeader) {
        this.followLeader = followLeader;
    }

    public boolean isForceBehavior() {
        return forceBehavior;
    }

    public void setForceBehavior(boolean forceBehavior) {
        this.forceBehavior = forceBehavior;
    }


    public int getWanderDistance() {
        return wanderDistance;
    }

    public void setWanderDistance(int wanderDistance) {
        this.wanderDistance = wanderDistance;
    }


    public ENGAGEMENT_LEVEL getEngagementLevel() {
        if (engagementLevel == null) {
            engagementLevel = ENGAGEMENT_LEVEL.UNSUSPECTING;
        }
        return engagementLevel;
    }

    public void setEngagementLevel(ENGAGEMENT_LEVEL engagementLevel) {
        this.engagementLevel = engagementLevel;
    }

    public void addEnemyKnownCoordinates(Coordinates... coordinates) {
        getKnownEnemyCoordinates().addAll(Arrays.asList(coordinates));
        // knownEnemyCoordinatesMap
    }

    private List<Coordinates> getKnownEnemyCoordinates() {
        return null ;
    }

    public Coordinates getOriginCoordinates() {
        if (originCoordinates == null) {
            originCoordinates = leader.getCoordinates();
        }
        return originCoordinates;
    }

    public boolean isClockwisePatrol() {
        return clockwisePatrol;
    }

    public boolean isBackAndForth() {
        return backAndForth;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    public void setBlock(LevelBlock block) {
        this.block = block;
    }

    public void setMembers(DequeImpl<Unit> members) {
        this.members = members;
        members.forEach(member -> member.getAI().setGroupAI(this));
    }

    public LevelBlock getBlock() {
        return block;
    }

    public void setBehavior(UnitAI.AI_BEHAVIOR_MODE behavior) {
        this.behavior = behavior;
    }

    public UnitAI.AI_BEHAVIOR_MODE getBehavior() {
        return behavior;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }
}
