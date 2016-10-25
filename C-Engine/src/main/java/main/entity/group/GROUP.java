package main.entity.group;

import main.entity.Entity;

import java.util.List;

public interface GROUP<T extends Entity> {

    List<T> getObjects();

    List<Integer> getObjectIds();

}
