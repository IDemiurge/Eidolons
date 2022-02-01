package eidolons.netherflame.generic.match;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Generic logic for rolling 2 rows to match into slots
 * Usages planned:
 * 1) Hero Draft
 * 2) Inventory prisms-slots
 * 3) Loot
 * 4) Essence Imbue
 */
public abstract class MatchLogic<T, C, B> {

    public static class MatchResult<T, C, B> {
        T top;
        C center;
        B bottom;

        public MatchResult(T top, C center, B bottom) {
            this.top = top;
            this.center = center;
            this.bottom = bottom;
        }
    }

    Map<C, Pair<B, T>> map=new LinkedHashMap<>();
    LinkedList<T> top;
    C[] center;
    int slots = center.length;
    LinkedList<B> bottom;

    public MatchLogic(Collection<T> top, Collection<B> bottom, C... center) {
        this.top = new LinkedList<>(top);
        this.center = center;
        this.bottom = new LinkedList<>(bottom);
    }

    public void rollTop(boolean left) {
        if (left) {
            T first = top.removeFirst(); //shifts?
            top.add(first);
        } else {
            T last = top.removeLast();
            top.add(0, last);
        }
        updateMap();
        update();
    }

    private void updateMap() {
        map.clear();
        int i =0;
        for (C c : center) {
            map.put(c, new ImmutablePair<>(bottom.get(i), top.get(i++)));
        }
    }
    public void reset(){

    }
    public void random(Boolean topBotAll){

    }
    // what about the idea to swap into HERO's own pool?
    public void swap(boolean topOrBot, int i, int j){
        if (topOrBot) {
            T removed = top.remove(i);
            top.add(j, removed);
        } else {
            B removed = bottom.remove(i);
            bottom.add(j, removed);
        }
    }

    public Set<MatchResult<T, C, B>> getResults(){
        Set<MatchResult<T, C, B>> set= new LinkedHashSet<>();
        for (C c : map.keySet()) {
            T t= map.get(c).getRight();
            B b= map.get(c).getLeft();
            set.add(new MatchResult<>(t, c, b));
        }
        return set;
    }

    protected abstract void update();
    protected abstract void fail();

}
