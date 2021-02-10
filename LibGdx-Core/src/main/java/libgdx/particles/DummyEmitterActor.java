package libgdx.particles;

public class DummyEmitterActor extends EmitterActor {
    public DummyEmitterActor(String finalPath) {
        super(finalPath);
    }

    @Override
    public void init() {
        effect = EmitterPools.getDummyFx(path);
    }
}
