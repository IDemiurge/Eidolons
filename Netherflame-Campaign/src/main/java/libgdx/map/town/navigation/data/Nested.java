package libgdx.map.town.navigation.data;

import java.util.Set;

/**
 * Created by JustMe on 11/29/2018.
 */
public interface Nested<T extends NestedLeaf> extends NestedLeaf {
    default void add(T element){
        getNested().add(element);
    }
    Set<T> getNested();

    default T get(){
        return (T) this;
    }

}
