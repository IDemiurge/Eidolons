package eidolons.client.cc.gui.neo.choice;

import eidolons.entity.obj.unit.Unit;
import main.entity.Entity;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PresetEntityChoiceView extends ChoiceView<Entity> {
    private Entity[] entities;
    private String info;

    public PresetEntityChoiceView(ChoiceSequence choiceSequence, Unit hero, String info,
                                  List<Entity> entities) {
        this(choiceSequence, hero, info, entities.toArray(new Entity[entities.size()]));
    }

    public PresetEntityChoiceView(ChoiceSequence choiceSequence, Unit hero, String info,
                                  Entity... entities) {
        super(choiceSequence, hero);
        this.info = info;
        this.entities = entities;
    }

	/*
     * * middle hero dungeons info panels?
	 */

    @Override
    public void activate() {
        init();
    }

    @Override
    protected void applyChoice() {
        // TODO Auto-generated method stub

    }

    protected void addInfoPanels() {

    }

    @Override
    protected void initData() {
        if (entities != null) {
            data = new ListMaster<Entity>().getList(entities);
        } else {
            data = new ArrayList<>();
        }
    }

    @Override
    public String getInfo() {
        return info;
    }

}
