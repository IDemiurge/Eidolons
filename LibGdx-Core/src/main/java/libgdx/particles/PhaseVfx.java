package libgdx.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.particles.spell.SpellVfx;
import libgdx.particles.spell.SpellVfxPool;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 12/1/2018.
 */
public class PhaseVfx extends SpellVfx {

    SpellVfx active;
    Map<ANIM_PART, SpellVfx> map = new LinkedHashMap<>();
    static Map<ANIM_PART, String> memoryMap = new LinkedHashMap<>();
    float timeToNext;
    private int n = 0;
    private boolean done;
    private boolean onetime;
    //    VFX[] VFX_MISSILE = new VFX[]{
    //    VFX[] VFX_IMPACT = new VFX[]{
    //    List<String> impactVfx = Arrays.asList(VFX_IMPACT).stream().map(vfx -> vfx.toString()).collect(Collectors.toList());
    //    List<String> missileVfx = Arrays.asList(VFX_MISSILE).stream().map(vfx -> vfx.toString()).collect(Collectors.toList());

    public PhaseVfx(String path_, ANIM_PART... parts) {
        super(path_);
        for (ANIM_PART part : parts) {
            if (isRandom()) {
                String fileName;
                //                if (part== ANIM_PART.MISSILE) {
                //                    fileName =    RandomWizard.getRandomListObject(missileVfx);
                //                } else
                //                if (part== ANIM_PART.IMPACT) {
                //                    fileName =    RandomWizard.getRandomListObject(impactVfx);
                //                } else

               String path = path_ + "/preset/" + part.getPartPath();
                main.system.auxiliary.log.LogMaster.log(1,"random phase vfx: "+path );
                File file = FileManager.getRandomFile(path);
                if (file == null) {
                    main.system.auxiliary.log.LogMaster.log(1, ">> no vfx in " + path);
                    path_ = "nether";
                } else {

                    if (!isUseMemory())
                    fileName = FileManager.getRandomFile(path).getName();
                    else fileName= memoryMap.get(part);
                    SpellVfx vfx = SpellVfxPool.getEmitterActor(path + "/" + fileName);
                    map.put(part, vfx);
                    memoryMap.put(part,  fileName);
                    continue;
                }
                SpellVfx vfx = SpellVfxPool.getEmitterActor(
                        PathFinder.getVfxAtlasPath() + "spell/" + part.getPartPath() + "/" + path_);
                map.put(part, vfx);
            } else {
                SpellVfx vfx = SpellVfxPool.getEmitterActor( path_);
                map.put(part, vfx);
            }

                //                AnimConstructor.getStandardData()



        }
        for (ANIM_PART anim_part : map.keySet()) {
            SpellVfx e = map.get(anim_part);
            e.getEffect().setAlpha(0.23f);
            e.setSpeed(0.63f);
            e.getEffect().getEmitters().forEach(e1-> e1.setAdditive(false));
        }
    }

    private boolean isUseMemory() {
        return true;
    }

    public static boolean isRandom() {
        return false;
    }

    public void next() {
        if (n >= map.size()) {
            done = true;
            return;
        }
        n++;
        if (active != null) {
            active.hide();
        }
        ANIM_PART part = (ANIM_PART) map.keySet().toArray()[n];
        if (part.equals(VisualEnums.ANIM_PART.IMPACT)) {
            onetime = true;
        }
        active = (SpellVfx) map.values().toArray()[n];
        addActor(active);
        active.reset();
        active.start();
        //        if (onetime) {
        //            active.hide();
        //        }

    }

    public void setTimeToNext(float timeToNext) {
        this.timeToNext = timeToNext;
    }

    public void resetPhases() {
        n = -1;
        done = false;
        onetime = false;
        next();
    }

    public void setOnetime(boolean onetime) {
        this.onetime = onetime;
    }

    @Override
    public void reset() {
        active.reset();
    }

    @Override
    public void act(float delta) {
        if (timeToNext > 0) {
            timeToNext -= delta;
            if (timeToNext <= 0) {
                next();
            }
        }
        if (active != null)
        {
            active.getEffect().setAlpha(0.23f);
            active.getEffect().getEmitters().forEach(e-> e.setAdditive(false));
            active.setSpeed(0.63f);
            active.act(delta);
//        main.system.auxiliary.log.LogMaster.log(1," "+active.getEffect().alpha );
        }
    }

    @Override
    public void start() {
        resetPhases();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (active == null)
            return;
        active.setPosition(getX(), getY());
        active.draw(batch, parentAlpha);
    }

    @Override
    public void setFlipX(boolean flipX) {
        active.setFlipX(flipX);
    }

    @Override
    public void setFlipY(boolean flipY) {
        active.setFlipY(flipY);
    }

    @Override
    public void hide() {
        active.hide();
    }

    @Override
    public void setAttached(boolean attached) {
        active.setAttached(attached);
    }

    @Override
    public void setSpeed(float speed) {
        active.setSpeed(speed);
    }
}
