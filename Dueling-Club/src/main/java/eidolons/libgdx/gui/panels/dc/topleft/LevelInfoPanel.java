package eidolons.libgdx.gui.panels.dc.topleft;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.ScenarioGame;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LevelInfoPanel extends TablePanelX {
    protected ValueContainer locationLabel;

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public LevelInfoPanel() {
        super(new ImageContainer((Images.ZARK_BOX_UPSIDE_DOWN)));

        addActor(locationLabel = new ValueContainer("", "") {
            @Override
            protected boolean isVertical() {
                return true;
            }
        });
        locationLabel.setNameStyle(StyleHolder.getAVQLabelStyle(19));
        locationLabel.setValueStyle(StyleHolder.getAVQLabelStyle(17));
        locationLabel.padTop(12);
        locationLabel.padBottom(12);

        GuiEventManager.bind(GuiEventType.GAME_STARTED, p -> {
            CharSequence text = "";
            CharSequence v = "";
            if (Eidolons.getGame() instanceof ScenarioGame) {
                if (EidolonsGame.FOOTAGE) {
                    text = "Castle Evarinath";
                    v =
                            //                                StringMaster.getWellFormattedString(PathUtils.getLastPathSegment
                            //                                        (StringMaster.cropFormat(MainLauncher.getCustomLaunch().
                            //                                                getValue(CustomLaunch.CustomLaunchValue.xml_path)))
                            " Floor [" + 3 + "/" +
                                    4 + "]\n" +
                                    "Citadel";
                } else {
                    ScenarioMetaMaster m = ScenarioGame.getGame().getMetaMaster();
                    text = m.getMetaGame().getScenario().getName();
                    v = m.getMetaDataManager().getMissionName()
                            + "\n   Level [" + (m.getMetaGame().getMissionIndex() + 1) + "/" +
                            m.getMetaGame().getMissionNumber() + "]";
                }

                locationLabel.setNameText(text);
                locationLabel.setValueText(v);
                locationLabel.pack();
                GdxMaster.top(locationLabel);
                locationLabel.setX(17);  //GdxMaster.centerWidth(locationLabel);
            }

        });
    }
    }
