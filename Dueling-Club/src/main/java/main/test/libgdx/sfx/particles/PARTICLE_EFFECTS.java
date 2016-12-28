package main.test.libgdx.sfx.particles;

/**
 * Created by JustMe on 12/28/2016.
 */
public enum PARTICLE_EFFECTS{
    SMOKE_TEST( "Smoke_Test1.pt"),
    ;
    private  String path;

    PARTICLE_EFFECTS(String path){
        this.path=path;
    }
    public String getPath() {
        return path;
    }

}
