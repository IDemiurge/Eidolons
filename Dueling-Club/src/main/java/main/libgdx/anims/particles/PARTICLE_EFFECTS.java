package main.libgdx.anims.particles;

/**
 * Created by JustMe on 12/28/2016.
 */
public enum PARTICLE_EFFECTS{
    SMOKE_TEST( "Smoke_Test1.pt"),
    DARK_SOULS("dark souls"),
    DARK_SOULS2("dark souls2"),
    DARK_SOULS3("dark souls3"),
    SKULL("skull"),
    SKULL2("skull2"),
    SKULL3("skull3"),

    //TODO sub-emitters

    ;
    public   String path;

    PARTICLE_EFFECTS(String path){
        this.path=path;
    }


}
