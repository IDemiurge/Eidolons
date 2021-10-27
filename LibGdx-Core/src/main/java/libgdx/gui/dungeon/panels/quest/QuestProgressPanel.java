package libgdx.gui.dungeon.panels.quest;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.exploration.story.quest.advanced.Quest;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.gui.LabelX;
import libgdx.gui.dungeon.panels.TablePanelX;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestProgressPanel extends TablePanelX {
    public static final int WIDTH = 315;

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (EidolonsGame.DUEL) {
//            return;
//        }
        super.draw(batch, parentAlpha);
    }

    public QuestProgressPanel() {
        super(GdxMaster.adjustWidth(WIDTH), GdxMaster.adjustHeight(800));
        top();
        left();
        add(new LabelX("Quests: ", StyleHolder.getHqLabelStyle(20))).center().row();

        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED,
                p -> {
                    if (p.get() instanceof Quest) {
                        for (Actor actor : getChildren()) {
                            if (actor instanceof QuestElement) {
                                if (((QuestElement) actor).quest==p.get()) {
                                    ActionMasterGdx.addFadeOutAction(actor, 1, true);
                                }
                            }
                        }
                    }
                });

        GuiEventManager.bind(GuiEventType.QUEST_UPDATE,
                p -> {
                    for (Actor actor : getChildren()) {
                        if (actor instanceof QuestElement) {
                            ((QuestElement) actor).setUpdateRequired(true);
                        }
                    }
                });

        GuiEventManager.bind(GuiEventType.QUEST_ENDED,
                p -> {
                    setUpdateRequired(true);

                });
        GuiEventManager.bind(GuiEventType.QUEST_STARTED,
                p -> {
                    Quest quest = (Quest) p.get();
                    for (Actor actor : getChildren()) {
                        if (actor instanceof QuestElement) {
                            if (((QuestElement) actor).quest == quest) {
                                return;
                            }
                        }
                    }
                    QuestElement element = new QuestElement(quest);
                    add(element);
                    row();
                    element.fadeIn();
                });
        GuiEventManager.bind(GuiEventType.GAME_STARTED,
                p -> {
                    update();
                });
    }

    @Override
    public void update() {
        GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
    }
}
