package gdx.general.view;

import gdx.dto.DtoManager;
import libgdx.gui.generic.GroupX;

public abstract class ADtoView<T extends DtoManager.Dto> extends GroupX {

    protected T dto;
    protected boolean dirty;

    public ADtoView<T> setDto(T dto) {
        if (dto.equals(this.dto)){
            return null;
        }
        this.dto = dto;
        dirty = true;
        return this;
    }

    @Override
    public void act(float delta) {
        if (dirty)
        {
            update();
            dirty = false;
        }
        super.act(delta);
    }

    protected abstract void update();
}
