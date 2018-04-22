package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PropMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by JustMe on 4/15/2018.
 */
public class HqDataMaster {
    static Map<Unit, HqDataMaster> map = new HashMap<>();
    Unit hero;
    HeroDataModel heroModel;
    Stack<Pair<ParamMap, PropMap>> stack;

    public HqDataMaster(Unit hero) {
        this.hero = hero;
        heroModel = new HeroDataModel(hero);
        stack = new Stack<>();
        map.put(hero, this);
    }

    public static HeroDataModel getHeroModel(Unit hero) {
        return map.get(hero).getHeroModel();
    }

    public static HqHeroDataSource getHeroDataSource(Unit hero) {
        return         new HqHeroDataSource(map.get(hero).getHeroModel());
    }

    public static void saveHero(HeroDataModel model) {
        map.get(model.getHero()).save();
    }
        public void save() {
        hero.cloneMaps(heroModel);

    }
        public void modified() {
        stack.push(new ImmutablePair<>(hero.getParamMap(), hero.getPropMap()));
    }

    public void undo() {
        heroModel = new HeroDataModel (hero.getType(),  stack.pop());
        hero.cloneMaps(heroModel);
    }

    public   void modify(HeroDataModel model,
                         PARAMETER param, int i) {
        modified();
        model.modifyParameter(param, i, true);
        reset();


    }

    private void reset() {
        heroModel.reset();
        HqPanel.getActiveInstance().modelChanged();
    }

    public static void modify(HqHeroDataSource dataSource,
                              PARAMETER param, int i) {
        map.get((dataSource.getEntity().getHero()))
         .modify(dataSource.getEntity(), param, i);
    }

    public HeroDataModel getHeroModel() {
        return heroModel;
    }

    public static void modelChanged(HeroDataModel entity) {
        map.get(entity.getHero()).reset();
    }
}
