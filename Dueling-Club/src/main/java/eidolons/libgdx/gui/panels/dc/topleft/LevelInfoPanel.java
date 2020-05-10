package eidolons.libgdx.gui.panels.dc.topleft;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.ScenarioGame;
import eidolons.game.netherflame.igg.IGG_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

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
                try {
                    if (CoreEngine.isIDE()) {
                        text = "Castle Evarinath";
                        v =
//                                StringMaster.getWellFormattedString(PathUtils.getLastPathSegment
//                                        (StringMaster.cropFormat(MainLauncher.getCustomLaunch().
//                                                getValue(CustomLaunch.CustomLaunchValue.xml_path)))
                                        " Floor [" + 2 + "/" +
                                        4 + "]\n" +
                                                "Crypts" ;
                    } else {
                        ScenarioMetaMaster m = ScenarioGame.getGame().getMetaMaster();
                        text = m.getMetaGame().getScenario().getName();
                        v = m.getMetaDataManager().getMissionName()
                                + "\n   Level [" + (m.getMetaGame().getMissionIndex() + 1) + "/" +
                                m.getMetaGame().getMissionNumber() + "]";
                    }

                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            } else {

                if (DC_Game.game instanceof IGG_Game) {
//               TODO igg demo fix
//                IGG_MetaMaster m = m = ScenarioGame.getGame().getMetaMaster();
//                    text = m.getMetaGame().getScenario().getName();
//                    v = m.getMetaDataManager().getMissionName()
//                            + ", Level [" + (m.getMetaGame().getMissionIndex() + 1) + "/" +
//                            m.getMetaGame().getMissionNumber() + "]";
                }
            }

            locationLabel.setNameText(text);
            locationLabel.setValueText(v);
            locationLabel.pack();
            GdxMaster.top(locationLabel);
            locationLabel.setX(17);  //GdxMaster.centerWidth(locationLabel);
        });
    }
}
