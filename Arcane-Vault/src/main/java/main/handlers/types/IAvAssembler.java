package main.handlers.types;

public interface IAvAssembler {
    void applyType();

    void construct(); //from other types

    void levelUp();

    void preview();

    enum TEMPLATE_TYPE{
        BASE, GROUP, SUBGROUP, TYPE, UPGRADE, NAMED,
    }
}
