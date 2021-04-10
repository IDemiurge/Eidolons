package libgdx.gui.overlay.choice;

import eidolons.game.netherflame.main.death.ChainHero;
import eidolons.game.netherflame.main.soul.SoulforceMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VC_DataSource {

    VC_TYPE type;
    List<VC_Option> options;

    public VC_DataSource(VC_TYPE type) {
        this.type = type;
        options = initOptions(type);
    }

    public VC_DataSource(VC_TYPE type, Object arg) {
        this.type = type;
        options = initCustomOptions(type, arg);
    }

    private List<VC_Option> initCustomOptions(VC_TYPE type, Object arg) {
        List<VC_Option> options = new LinkedList<>();
        switch (type) {
            case hero_choice:
                VC_OPTION chosen = (VC_OPTION) arg;
                Set<ChainHero> heroes = SoulforceMaster.getInstance().getHeroesCanRespawn( );
                for (ChainHero hero : heroes) {
                    options.add(createHeroOption(hero));
                }
                // sortHeroOptions(options);
        }
        return options;
    }

    private VC_Option createHeroOption(ChainHero hero) {
      return   new VC_Option(hero, hero.getUnit().getName(), hero.getUnit().getToolTip(), hero.getUnit().getImagePath());
    }

    private List<VC_Option> initOptions(VC_TYPE type) {
        List<VC_Option> options = new LinkedList<>();
        switch (type) {
            case death:
                options.add(new VC_Option(VC_OPTION.ashen_rebirth));
                options.add(new VC_Option(VC_OPTION.fiery_rebirth));
                options.add(new VC_Option(VC_OPTION.dissolution));
                break;
        }
        return options;
    }

    public enum VC_TYPE {
        // eidolon_arts,
        // victory,
        // soul_steal,
        // precombat,
        death("Death is but another step"),
        hero_choice("An Eidolon must be chosen");

        VC_TYPE(String title) {
            this.title = title;
        }

        private final String title;

        public String getTitle() {
            return title;
        }

    }

    //best 3 or 5 options then
    public enum VC_OPTION {
        cancel,
        //arts
        dissolve,
        revelation,
        fight_rush,
        fight_stand,
        negotiate,

        //vendor - after initial dialogue..
        exchange,
        barter,
        talk,

        // soul_flight,
        fiery_rebirth,
        ashen_rebirth,

        //combat
        coup_de_grace,

        //xp

        //shrine

        burn_item,
        ash_shape,

        drink_blood,
        sacrifice_blood,

        travel_forward,
        travel_back,

        eidolon_vision,

        imbue_soul,
        fracture_soul,

        activate,
        dissolution;
        public String img;

        VC_OPTION() {
            // img = PathFinder.getArtFolder()+"choices/"+ toString()+".png";
            img = "ui\\components\\generic\\vc/" + StringMaster.format(toString()) + ".png";

        }

        VC_OPTION(String img) {
            this.img = img;
        }
    }
}
