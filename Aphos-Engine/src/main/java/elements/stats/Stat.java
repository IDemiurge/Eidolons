package elements.stats;

public interface Stat {
    default String getName(){
        return toString();
    }
}
