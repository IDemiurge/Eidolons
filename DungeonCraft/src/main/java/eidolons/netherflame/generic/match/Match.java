package eidolons.netherflame.generic.match;

public interface Match <T, C, B> {

    void  process(MatchLogic.MatchResult<T, C, B> result);
}
