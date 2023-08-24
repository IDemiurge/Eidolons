package elements.stats.generic;

public interface Stat {
    default String getName(){
        return toString();
    }
}
