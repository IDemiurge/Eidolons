package libgdx.gui.panels.dc.topleft.atb;

import libgdx.anims.actions.ActionMaster;
import libgdx.bf.generic.ImageContainer;
import libgdx.bf.grid.cell.QueueView;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;

import java.util.List;

public class AtbViewManager {
    AtbPanel atbPanel;

    public AtbViewManager(AtbPanel atbPanel) {
        this.atbPanel = atbPanel;
        bindEvents();
    }

    public void updateTimedEvents(){
        /**
         * need index to know where to draw, and that's it
         *
         */
//        timedEvents.for

    }
    protected void bindEvents() {
 GuiEventManager.bind(GuiEventType.PREVIEW_ATB_READINESS, obj -> {
        List<Integer> list = (List<Integer>) obj.get();
        previewAtbReadiness(list);
    });
            GuiEventManager.bind(GuiEventType.ATB_POS_PREVIEW, obj -> {
        if (obj.get() == null) {
            removePreviewAtbPos();
            previewAtbReadiness(null);
        } else {
            previewAtbPos((int) obj.get() );
        }
    });
}

    protected void previewAtbReadiness(List<Integer> list) {
        int i = 0;
        for (int j =atbPanel. queue.length - 1; j > 0; j--) {
            QueueViewContainer sub = atbPanel.queue[j];
            if (sub == null)
                break;
            if (list != null)
                if (list.size() <= i) {
                    break;
                }
            QueueView actor = sub.getActor();
            String text = (list == null ? sub.initiative : list.get(i++)) + "";
            if (actor == null) {
                continue;
            }
            actor.setInitiativeLabelText(text);
        }
    }
    void sort() {
        for (int i = 0; i < atbPanel.queue.length - 1; i++) {
            final int ip1 = i + 1;
            QueueViewContainer cur = atbPanel.queue[i];

            if (cur != null) {
                QueueViewContainer next = atbPanel.queue[ip1];
                if (next == null) {
                    atbPanel.queue[i] = null;
                    atbPanel.queue[ip1] = cur;
                } else {
                    boolean result =
                            isSortByTimeTillTurn() ? cur.queuePriority > next.queuePriority || (cur.id > next.id && cur.queuePriority == next.queuePriority)
                                    : cur.initiative > next.initiative || (cur.id > next.id && cur.initiative == next.initiative);
                    if (result) {
                        atbPanel.queue[ip1] = cur;
                        atbPanel.queue[i] = next;
                        for (int y = i; y > 0; y--) {
                            if (atbPanel.queue[y - 1] == null) {
                                break;
                            }
                            if (atbPanel.queue[y].initiative >= atbPanel.queue[y - 1].initiative) {
                                break;
                            }
                            QueueViewContainer buff = atbPanel.queue[y - 1];
                            atbPanel.queue[y - 1] = atbPanel.queue[y];
                            atbPanel.queue[y] = buff;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < atbPanel.queue.length; i++) {
            QueueViewContainer cur = atbPanel.queue[i];
            if (cur != null && !cur.mobilityState) {
                for (int y = i; y > 0; y--) {
                    if (atbPanel.queue[y - 1] == null) {
                        break;
                    }
                    if (!atbPanel.queue[y - 1].mobilityState) {
                        break;
                    }
                    QueueViewContainer buff = atbPanel.queue[y - 1];
                    atbPanel.queue[y - 1] = atbPanel.queue[y];
                    atbPanel.queue[y] = buff;
                }
            }
        }
    }

    protected void previewAtbPos(int i) {
        if (i > atbPanel.maxSize) {
            //TODO
        }
        if (atbPanel.previewActor == null) {
            atbPanel.addActor(atbPanel.previewActor = new ImageContainer(atbPanel.manager.getPreviewPath()));
            atbPanel.previewActor.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.ATB_POS);
        }
        atbPanel.previewActor.setVisible(true);
        atbPanel.previewActor.setScale(1.25f);
        atbPanel.previewActor.clearActions();
        atbPanel.previewActor.setY(-30);
        atbPanel.previewActor.setX(atbPanel.container.getX() + (i + 1) * (AtbPanel.imageSize + atbPanel.DST_BETWEEN_VIEWS) - atbPanel.DST_BETWEEN_VIEWS / 2);
        ActionMaster.addScaleActionCentered(atbPanel.previewActor, 0, 1, 8);
//        ActorMaster.addMoveToAction(previewActor, previewActor.getX()+previewActor.getWidth()/2,
//         previewActor.getY(), 5);
    }

    protected float relToPixPos(int pos) {
        return pos * AtbPanel.imageSize + pos * atbPanel.DST_BETWEEN_VIEWS;
    }

    protected QueueViewContainer getIfExists(QueueView view) {
        for (int i = 0; i < atbPanel.queue.length; i++) {
            if (atbPanel.queue[i] != null && atbPanel.queue[i].getActor() == view) {
                return atbPanel.queue[i];
            }
        }
        return null;
    }
        protected QueueViewContainer getIfExists(int id) {
        for (int i = 0; i < atbPanel.queue.length; i++) {
            if (atbPanel.queue[i] != null && atbPanel.queue[i].id == id) {
                return atbPanel.queue[i];
            }
        }
        return null;
    }

    protected String getPreviewPath() {
        return StrPathBuilder.build(PathFinder.getComponentsPath()
                , "dc", "atb pos preview.png"
        ) + PathUtils.getPathSeparator();
    }


    protected boolean isSortByTimeTillTurn() {
        return true;
    }


    protected void removePreviewAtbPos() {
        if (    atbPanel.previewActor != null) {
            atbPanel.previewActor.setVisible(false);
        }
    }


//    public static class AtbTimedEvent{
//float timeLeft;
//boolean endOfRound;
//BattleFieldObject after;
//Runnable action;
// will this apply for Channeling?
//    }
}
