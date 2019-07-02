package eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.imbue;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.battlecraft.logic.meta.igg.death.ChainHero;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.EidolonImbuer;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.libgdx.gui.panels.ScrollPaneX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.inv.ItemActor;
import eidolons.libgdx.gui.tooltips.SmartClickListener;

import java.util.ArrayList;

public class ImbueItems extends TablePanelX {
    private DC_HeroItemObj selected;
    ArrayList<DC_HeroItemObj> items = new ArrayList<>();
    ScrollPane scrollPane;
    ValueTable<DC_HeroItemObj, ImbueItemActor  > table;

    public ImbueItems() {
        table = new ValueTable<DC_HeroItemObj, ImbueItemActor>(4, 24) {
            @Override
            protected ImbueItemActor createElement(DC_HeroItemObj item) {
                 ImbueItemActor itemActor = new ImbueItemActor(item);

                itemActor.addListener(new SmartClickListener(itemActor) {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        setSelected(item);
                    }

                    @Override
                    protected void entered() {
                        super.entered();
                        //info
                    }
                });
                return itemActor;
            }

            @Override
            protected ImbueItemActor[] initActorArray() {
                return new ImbueItemActor[items.size()];
            }

            @Override
            protected DC_HeroItemObj[] initDataArray() {
                return items.toArray(new DC_HeroItemObj[items.size()]);
            }
        };
        add(scrollPane = new ScrollPaneX(table));

    }

    @Override
    public void updateAct(float delta) {
        clearChildren();
        LordPanel.LordDataSource data = (LordPanel.LordDataSource) getUserObject();

        for (ChainHero chainHero : data.getChain().getHeroes()) {
            items.addAll(EidolonImbuer.getValidItems(chainHero.getUnit()));
        }
        for (DC_HeroItemObj item : items) {
            ImbueItemActor itemActor = new ImbueItemActor(item);

            itemActor.addListener(new SmartClickListener(itemActor) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    setSelected(item);
                }

                @Override
                protected void entered() {
                    super.entered();
                    //info
                }
            });
        }
    }

    public DC_HeroItemObj getSelected() {
        return selected;
    }

    public void setSelected(DC_HeroItemObj selected) {
        this.selected = selected;
    }

    private class ImbueItemActor extends ItemActor {

        public ImbueItemActor(DC_HeroItemObj model) {
            super(model);
        }
    }
}
