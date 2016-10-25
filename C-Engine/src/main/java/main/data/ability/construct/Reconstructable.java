package main.data.ability.construct;


public interface Reconstructable<E> {

    E getCopy();

    Construct getConstruct();

    void setConstruct(Construct construct);
}
