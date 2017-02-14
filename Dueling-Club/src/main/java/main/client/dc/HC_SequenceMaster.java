package main.client.dc;

import main.client.cc.CharacterCreator;
import main.client.cc.HC_Master;
import main.client.cc.gui.neo.choice.*;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher.VIEWS;
import main.content.CONTENT_CONSTS.RACE;
import main.content.*;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.elements.conditions.*;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.DC_Game.GAME_MODES;
import main.game.DC_Game.GAME_TYPE;
import main.game.logic.arcade.ArenaArcadeMaster;
import main.game.logic.generic.BattleOptions.DIFFICULTY;
import main.game.logic.generic.PartyManager;
import main.system.entity.ConditionMaster;
import main.system.SortMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class HC_SequenceMaster implements SequenceManager {

    private static final Object SELECTION_CANCELLED = "!";
    private static final String CHOOSE_MEMBER = null;
    private ChoiceSequence sequence;
    private SELECTION_TYPES selection;

    public static Condition getWorkspaceFilterCondition() {
        return ConditionMaster.getWorkspaceCondition(true, HC_Master.getFilterWorkspaceGroup());

    }

    public static SequenceManager getGenericSequenceManager() {
        return new SequenceManager() {

            public void doneSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, true);
            }

            public void cancelSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, false);
            }
        };
    }

    public static SequenceManager getHC_SequenceManager() {
        return new SequenceManager() {

            public void doneSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, true);
            }

            public void cancelSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, false);
                Launcher.resetView(VIEWS.HC);
            }
        };
    }

    public void chooseNewMember(PartyObj party) {
        Conditions filterConditions = new Conditions();
        DC_HeroObj leader = party.getLeader();
        OrConditions orConditions = new OrConditions();

        // StringContainersComparison principlesCondition = new
        // StringContainersComparison(
        // "{SOURCE_" + G_PROPS.PRINCIPLES + "}", "{MATCH_"
        // + G_PROPS.PRINCIPLES + "}");
        // orConditions.add(principlesCondition);
        // PartyManager
        // Conditions principlesConditions =
        // PartyManager.getPrincipleConditions(party);
        // orConditions.add(principlesConditions);
        // now gradual! TODO

        // StringComparison deityCondition = new StringComparison("{SOURCE_"
        // + G_PROPS.DEITY + "}", "{MATCH_" + G_PROPS.DEITY + "}", true);
        //
        // orConditions.add(new Conditions(new NotCondition(new PropCondition(
        // G_PROPS.DEITY, "" + STD_DEITY_TYPE_NAMES.Faithless, true)),
        // deityCondition));
        // filterConditions.add(orConditions);

        NotCondition noDuplicatesCondition = new NotCondition(new StringContainersComparison(true,
                StringMaster.getValueRef(KEYS.PARTY, PROPS.MEMBERS), StringMaster.getValueRef(
                KEYS.MATCH, G_PROPS.NAME), false));
        filterConditions.add(noDuplicatesCondition);

        if (CharacterCreator.isArcadeMode()) {
            NumericCondition lvlCondition = new NumericCondition(false, StringMaster.getValueRef(
                    KEYS.PARTY, PARAMS.LEVEL)
                    + "+1", StringMaster.getValueRef(KEYS.MATCH, PARAMS.HERO_LEVEL));
            filterConditions.add(lvlCondition);
        }
        filterConditions.setRef(leader.getRef());
        launchEntitySelection(OBJ_TYPES.CHARS, MainManager.getPresetGroup(), filterConditions,
                leader, InfoMaster.CHOOSE_MEMBER);

        selection = SELECTION_TYPES.NEW_MEMBER_SELECTION;
    }

    public void newEntitySelection(OBJ_TYPES party, String group, DC_HeroObj entity, String info) {
        launchEntitySelection(party, group, null, entity, info);
    }

    public void launchEntitySelection(final OBJ_TYPES t, final String group,
                                      final Condition filteringConditions, DC_HeroObj entity, final String info) {
        Comparator<? super Entity> sorter = HC_Master.getEntitySorter();
        if (sorter == null) {
            getDefaultSorter(t, group);
        }

        launchEntitySelection(t, group, filteringConditions, entity, info, sorter);
    }

    private Comparator<? super Entity> getDefaultSorter(final OBJ_TYPES t, final String group) {
        Comparator<? super Entity> sorter = null;
        if (t == OBJ_TYPES.CHARS) {
            if (group.equalsIgnoreCase(StringMaster.BACKGROUND)) {
                sorter = SortMaster.getSublistSorter(RACE.class);
            } else {
                sorter = SortMaster.getIdSorter();
            }
        }
        return sorter;
    }

    public void launchEntitySelection(final OBJ_TYPES t, final String group,
                                      Condition filteringCondition, DC_HeroObj entity, final String info,
                                      final Comparator<? super ObjType> sorter) {
        final Conditions filteringConditions = new Conditions(filteringCondition
                // , getWorkspaceFilterCondition()
        );
        setSequence(new ChoiceSequence());
        getSequence().addView(new EntityChoiceView(getSequence(), entity) {
            @Override
            public String getInfo() {
                return info;
            }

            @Override
            protected Comparator<? super ObjType> getSorter() {

                return sorter;
            }

            @Override
            protected Condition getFilterConditions() {
                return filteringConditions;
            }

            protected OBJ_TYPE getTYPE() {
                return t;
            }

            protected String getGroup() {
                return group;
            }

            @Override
            protected PROPERTY getPROP() {
                return null;
            }

            @Override
            protected VALUE getFilterValue() {
                return G_PROPS.GROUP;
            }
        });
        getSequence().setManager(this);
        sequence.start();

    }

    public void launchEntitySelection(Collection<? extends Entity> list, DC_HeroObj hero,
                                      String info) {
        sequence = new ChoiceSequence();
        sequence.addView(new PresetEntityChoiceView(sequence, hero, info, list
                .toArray(new Entity[list.size()])));
        getSequence().setManager(this);
        sequence.start();
    }

    @Override
    public void doneSelection() {
        // TODO "return" after wait()?
        WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, sequence.getValue());
        switch (selection) {
            case NEW_MEMBER_SELECTION:
                PartyManager.addMember(sequence.getValue());
                break;
        }
        Launcher.resetView(VIEWS.HC);
    }

    @Override
    public void cancelSelection() {
        WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, SELECTION_CANCELLED);
        if (selection != null) {
            switch (selection) {
                case NEW_MEMBER_SELECTION:
                    break;
            }
        }

        Launcher.resetView(VIEWS.HC);
    }

    public ChoiceSequence getSequence() {
        return sequence;
    }

    public void setSequence(ChoiceSequence sequence) {
        this.sequence = sequence;
    }

    public boolean prebattleChoiceSequence(DC_HeroObj hero) {

        final ChoiceSequence cs = new ChoiceSequence();
        List<Entity> list = ListMaster.getEntityList(PartyManager.getParty().getMembers());
        final int size = PartyManager.getParty().getMembers().size();

        ChoiceView positionChoiceView = null;
        if (ArenaArcadeMaster.isTestMode()) {
            hero.getGame().getArenaArcadeMaster().prebattle(cs);
        }

        if (PartyManager.getParty().checkTactics()
            // || ArenaArcadeMaster.isTestMode()
                ) {
            positionChoiceView = new PositionChoiceView(cs, hero);
        } else if (list.size() > 1) {
            positionChoiceView = new PresetEntityChoiceView(cs, hero, InfoMaster.MIDDLE_HERO, list) {

                // ComponentVisuals getGenericVisuals() { public VISUALS
                // getVisuals() {
                // Object dungeon = cs.getResults().peek();
                // if (dungeon instanceof ObjType){
                // ObjType objType = (ObjType) dungeon;
                // return new CompVisuals(objType.getProperty(
                // PROPS.MAP_BACKGROUND));
                // }
                // return super.getVisuals();
                // }

                protected int getColumnsCount() {
                    return size;
                }

                protected int getPageSize() {
                    return size;
                }

                protected void ok() {
                    PartyManager.getParty().setMiddleHero((DC_HeroObj) getSelectedItem());
                    super.ok();
                }

            };
        }
        cs.addView(new EnumChoiceView<DIFFICULTY>(cs, hero, DIFFICULTY.class) {
            @Override
            protected VISUALS getBackgroundVisuals() {
                return null;
            }

            @Override
            protected void applyChoice() {
                // TODO will it be initialized at the time?
                DC_Game.game.getArenaManager().setDEFAULT_DIFFICULTY(getSelectedItem().toString());
            }
        });
        if (positionChoiceView != null) {
            cs.addView(positionChoiceView);
        }
        if (!ArenaArcadeMaster.isTestMode()) {
            if (hero.getGame().getGameType() != GAME_TYPE.SCENARIO) {
                if (hero.getGame().getGameMode() == GAME_MODES.ARENA_ARCADE) {
                    hero.getGame().getArenaArcadeMaster().prebattle(cs);
                } else {
                    cs.addView(new DungeonChoiceView(cs, PartyManager.getParty()));
                }
            }
        }

        SequenceManager manager = HC_SequenceMaster.getHC_SequenceManager();
        cs.setManager(manager);
        cs.start();
        boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION);
        if (!result) {
            manager.cancelSelection();
        }
        return result;

    }

    public enum SELECTION_TYPES {
        NEW_MEMBER_SELECTION
    }

}
