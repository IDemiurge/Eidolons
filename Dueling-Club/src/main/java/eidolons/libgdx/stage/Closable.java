package eidolons.libgdx.stage;

/**
 * Created by JustMe on 11/25/2017.
 */
public interface Closable {

    default void close() {
        getStageWithClosable().closeClosable(this);

    }

    StageWithClosable getStageWithClosable();

    default void open() {
        getStageWithClosable().openClosable(this);
    }

}
