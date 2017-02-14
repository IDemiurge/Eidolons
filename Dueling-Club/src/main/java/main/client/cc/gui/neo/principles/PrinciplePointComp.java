package main.client.cc.gui.neo.principles;

import main.client.cc.gui.neo.points.HC_PointComp;
import main.client.cc.gui.neo.points.PointSpinnerModel;
import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.rules.rpg.IntegrityRule;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PrinciplePointComp extends HC_PointComp {// do I really need to?

    private PRINCIPLES principle;
    private PrinciplePanel panel;

    public PrinciplePointComp(PrinciplePanel panel, PRINCIPLES principle, DC_HeroObj hero,
                              PrinciplePanel principlePanel, ObjType buffer) {
        super(true, hero, buffer, DC_ContentManager.getIdentityParamForPrinciple(principle),
                PARAMS.IDENTITY_POINTS, VISUALS.PRINCIPLE_VALUE_BOX, true);
        panelSize = new Dimension(textComp.getVisuals().getWidth() + getArrowOffsetX(), textComp
                .getVisuals().getHeight());
        this.principle = principle;
        this.panel = panel;
    }

    protected int getArrowOffsetY() {
        return VISUALS.PRINCIPLE_VALUE_BOX.getHeight() / 2
                - ImageManager.getArrowImage(true, true, getArrowVersion()).getHeight(null);
    }

    protected String getIconOffsetX() {
        return "3";
    }

    protected String getIconOffsetY() {
        return "" + (textComp.getVisuals().getHeight() / 2 - 25);
    }

    @Override
    protected HC_PointElement getTextElement(boolean editable, PARAMETER param, VISUALS V) {
        return new HC_PointElement(false, param, V) {
            @Override
            protected Font getDefaultFont() {
                return PrincipleView.getDefaultFont();
            }

            @Override
            protected String getValue() {
                return "";
            }

            @Override
            protected int getDefaultY() {
                return super.getDefaultY() + 3;
            }

            @Override
            protected String getText() {
                if (principle == null) {
                    return "";
                }
                text = principle.toString();
                return text;
            }

            @Override
            protected boolean isCentering() {
                return true;
            }
        };
    }

    @Override
    protected PointSpinnerModel createModel(DC_HeroObj hero, ObjType buffer, PARAMETER param,
                                            PARAMETER pool) {
        return new PointSpinnerModel(textComp, hero, buffer, param, pool) {
            @Override
            public void up() {
                super.up();
                panel.getView().refresh();
                main.system.auxiliary.LogMaster.log(1, modified + " modified");
            }

            @Override
            protected boolean checkUp() {
                Integer value = buffer.getIntParam(param);
                if (value > IntegrityRule.getMaxIdentityValue()) {
                    return false;
                }
                return super.checkUp();
            }

            @Override
            protected boolean checkDown() {
                Integer value = buffer.getIntParam(param);
                if (value < IntegrityRule.getMinIdentityValue()) {
                    return false;
                }
                return super.checkDown();
            }

            @Override
            public void tryDown() {
                if (!checkDown()) {
                    SoundMaster.playStandardSound(getBlockedSound());
                } else {
                    down();
                }
            }

            @Override
            public void down() {
                super.down();
                panel.getView().refresh();
                main.system.auxiliary.LogMaster.log(1, modified + " modified");
            }

            @Override
            protected int getCost(Integer value, boolean down) {

                if (modified > 0) {
                    if (down) {
                        return getCost(value - 1, !down);
                    }
                }
                if (modified < 0) {
                    if (!down) {
                        return getCost(value + 1, !down);
                    }
                }
                if (value < 0) {
                    if (!down) {
                        return 1;
                    }
                } else if (down) {
                    return -1;
                }
                int multiplier = down ? -1 : 1;
                return multiplier * Math.max(1, Math.abs(value));
            }

            @Override
            protected STD_SOUNDS getBlockedSound() {
                return STD_SOUNDS.DIS__BLOCKED;
            }

            @Override
            protected STD_SOUNDS getUpSound() {
                switch (principle) {
                    case AMBITION:
                        return STD_SOUNDS.DIS__BOON_SMALL;
                    case CHARITY:
                        return STD_SOUNDS.DIS__BLESS;
                    case FREEDOM:
                        return STD_SOUNDS.DIS__BOON_SMALL;
                    case HONOR:
                        return STD_SOUNDS.FIGHT;
                    case LAW:
                        return STD_SOUNDS.CHAIN;
                    case PEACE:
                        return STD_SOUNDS.DIS__BOON_LARGE;
                    case PROGRESS:
                        return STD_SOUNDS.SPELL_UPGRADE_LEARNED;
                    case TRADITION:
                        return STD_SOUNDS.DIS__REWARD;
                    case TREACHERY:
                        return STD_SOUNDS.DIS__KNIFE;
                    case WAR:
                        return STD_SOUNDS.WEAPON;

                }
                return STD_SOUNDS.DIS__BOON_SMALL;
                // return super.getUpSound();
            }

            protected boolean isUndoOnDown() {
                return false;
            }

        };
    }

    @Override
    protected int getArrowOffsetX() {
        return ImageManager.getArrowImage(true, true, getArrowVersion()).getWidth(null);
    }

    @Override
    protected int getArrowVersion() {
        return 2;
    }

    @Override
    protected void updateIcon() {
        if (panel.getPrinciple() != null) {
            if (panel.getPrinciple().equals(principle)) {
                textComp.setVisuals(VISUALS.PRINCIPLE_VALUE_BOX_SELECTED);
            } else {
                textComp.setVisuals(VISUALS.PRINCIPLE_VALUE_BOX);
            }
        }
        // glow on PR. icon?
    }

    @Override
    protected void infoClick() {
        panel.getView().principleSelected(principle);
        SoundMaster.playStandardSound(STD_SOUNDS.MOVE);
    }

    @Override
    protected Image getValueIcon(PARAMETER param) {
        return ImageManager.getPrincipleImage(DC_ContentManager.getPrinciple(param));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    protected boolean isLocked() {
        return false;
    }

    @Override
    protected boolean isTreeNagivationOn() {
        return false;
    }
}
