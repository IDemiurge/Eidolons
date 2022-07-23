package gdx.dto;

import logic.entity.Entity;
import logic.entity.Hero;

import java.util.List;

public class FrontLineDto implements DtoManager.Dto {
    List<Entity> heroes;
    //objects separately!
    private Hero active;

    public FrontLineDto(List<Entity> heroes, Hero active) {
        this.heroes = heroes;
        this.active = active;
    }
    //separate for fortifications!

    public List<Entity> getHeroes() {
        return heroes;
    }


    public Hero getActiveHero() {
        return active;
    }
}
