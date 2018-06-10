package eidolons.game.module.dungeoncrawl.ai;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;

public class Patrol {
    Coordinates destination;
    Coordinates returnCoordinates;
    MapBlock block;
    boolean backAndForth;
    int waitPeriod;
    int maxWaitPeriod;
    int minWaitPeriod;
    int turnsWaited = 0;
    boolean alert;
    boolean search;
    private GroupAI group;
    private DIRECTION direction;
    private boolean clockwise = RandomWizard.random();
    private Integer distance;

    public Patrol(GroupAI group) {

    }

    public Coordinates getDestination() {
        return destination;
    }

    public void setDestination(Coordinates destination) {
        turnsWaited = 0;
        this.destination = destination;
    }

    public void turnWaited() {
        turnsWaited++;
    }

    public Unit getLeadingUnit() {

        return getGroup().getLeader();

    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public MapBlock getBlock() {
        return block;
    }

    public void setBlock(MapBlock block) {
        this.block = block;
    }

    public boolean isBackAndForth() {
        return backAndForth;
    }

    public void setBackAndForth(boolean backAndForth) {
        this.backAndForth = backAndForth;
    }

    public int getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(int waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    public int getMaxWaitPeriod() {
        return maxWaitPeriod;
    }

    public void setMaxWaitPeriod(int maxWaitPeriod) {
        this.maxWaitPeriod = maxWaitPeriod;
    }

    public int getMinWaitPeriod() {
        return minWaitPeriod;
    }

    public void setMinWaitPeriod(int minWaitPeriod) {
        this.minWaitPeriod = minWaitPeriod;
    }

    public Coordinates getReturnCoordinates() {
        return returnCoordinates;
    }

    public void setReturnCoordinates(Coordinates returnCoordinates) {
        this.returnCoordinates = returnCoordinates;
    }

    public boolean isClockwise() {
        return clockwise;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public int getTurnsWaited() {
        return turnsWaited;
    }

    public void setTurnsWaited(int turnsWaited) {
        this.turnsWaited = turnsWaited;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public GroupAI getGroup() {
        return group;
    }

    public void setGroup(GroupAI group) {
        this.group = group;
    }

}
