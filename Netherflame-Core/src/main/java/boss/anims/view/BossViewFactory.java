package boss.anims.view;

import boss.BossHandler;
import boss.anims.generic.BossVisual;
import boss.logic.BossCycle;
import boss.logic.entity.BossUnit;
import eidolons.game.core.Core;
import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.BossModel;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BossViewFactory<T extends BossModel> extends BossHandler<T> {

    private final Map<BossCycle.BOSS_TYPE, BossVisual> map = new LinkedHashMap<>();

    public BossViewFactory(BossManager<T> manager) {
        super(manager);
    }

    public abstract BossVisual create(BossCycle.BOSS_TYPE type, BossUnit unit);

    public void afterInit() {

        Core.onGdxThread(() -> {
            for (BossCycle.BOSS_TYPE boss_type : getModel().getCycle()) {
                try {
                    BossVisual view = create(boss_type, getEntity(boss_type));
                    map.put(boss_type, view);
                    GuiEventManager.trigger(GuiEventType.ADD_BOSS_VIEW, view);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        });
    }

    public BossVisual get(BossCycle.BOSS_TYPE type) {
        return map.get(type);
    }
}
