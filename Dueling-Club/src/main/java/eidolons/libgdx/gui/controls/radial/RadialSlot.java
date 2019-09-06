package eidolons.libgdx.gui.controls.radial;

/**
 * Created by JustMe on 11/13/2018.
 */
public class RadialSlot {
// extends ActionSlot {
//    protected List<RadialSlot> childNodes = new ArrayList<>();
//    protected RadialSlot parent;
//    protected Supplier<Tooltip> tooltipSupplier;
//    protected Tooltip tooltip;
//    protected Runnable lazyChildInitializer;
//    protected Label infoLabel;
//    protected boolean altUnderlay;
//    protected float underlayOffsetX;
//    protected float underlayOffsetY;
//
//    public RadialSlot(TextureRegion texture, Runnable action) {
//        super(null);
//        setUnderlay(
//         valid ?
//          getUnderlayDefault().getTextureRegion() :
//          getUnderlayDisabled().getTextureRegion());
//
//
//        addListener(new BattleClickListener() {
//
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                if (!valid)
//                    return;
//                setUnderlay(getUnderlayDisabled().getTextureRegion());
//                super.clicked(event, x, y);
//            }
//
//            @Override
//            public boolean mouseMoved(InputEvent event, float x, float y) {
//                if (!valid)
//                    return super.mouseMoved(event, x, y);
//                if (hover)
//                    return false;
//                hover = true;
//                setZIndex(Integer.MAX_VALUE);
//                setUnderlay(RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW.getTextureRegion());
//                //                ActorMaster.addScaleAction(RadialValueContainer.this, 1.2f, 1.2f, 0.7f);
//                return super.mouseMoved(event, x, y);
//            }
//
//            @Override
//            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                if (!valid) return;
//                if (!hover) {
//                    hover = true;
//                    setZIndex(Integer.MAX_VALUE);
//                    setUnderlay(getUnderlayGlow().getTextureRegion());
//                }
//                super.enter(event, x, y, pointer, fromActor);
//            }
//
//            @Override
//            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                hover = false;
//                setUnderlay(getUnderlayDefault().getTextureRegion());
//                super.exit(event, x, y, pointer, toActor);
//            }
//        });
//    }

//    public RadialValueContainer(TextureRegion texture, String value, Runnable action) {
//        super(texture, value, action);
//    }
//
//    public RadialValueContainer(TextureRegion textureRegion, Runnable runnable, boolean valid, DC_ActiveObj activeObj, DC_Obj target) {
//        this(textureRegion, runnable);
//        this.valid = valid;
//        try {
//            infoTextSupplier = RadialManager.getInfoTextSupplier(valid, activeObj, target);
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }
//
//    }
//
//    @Override
//    protected String getOverlay(DataModel model) {
//        return null;
//    }
//
//    @Override
//    protected String getEmptyImage() {
//        return null;
//    }
//
//    private void setUnderlay(TextureRegion underlay) {
//        if (!checkUnderlayRequired())
//            return;
//        setUnderlay_(underlay);
//    }
//
//    public void setUnderlay_(TextureRegion underlay) {
//
//        if (underlay == null)
//            return;
//        setUnderlayOffsetX(
//         (getWidth() - underlay.getRegionWidth()) / 3 * 2 + 3);
//        setUnderlayOffsetY((getHeight() - underlay.getRegionHeight()) / 3 * 2 + 7);
//        if (getRadial() != null)
//            if (getRadial().getActions().size > 0)
//                return;
//
//        //        main.system.auxiliary.log.LogMaster.log(1," underlay set " + underlay.getTexture().getTextureData());
//        this.underlay = underlay;
//    }
//
//    @Override
//    public void bindAction(Runnable action) {
//        if (action != null) {
//            clickAction = action::run;
//        }
//    }
//
//    protected boolean checkUnderlayRequired() {
//        if (getParent() == null)
//            return false;
//        return true;
//    }
//
//    public void setInfoTextSupplier(Supplier<String> infoTextSupplier) {
//        this.infoTextSupplier = infoTextSupplier;
//    }
//
////    @Override
////    protected void drawLightUnderlay(Batch batch) {
////        return;
////    }
//
//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//        if (parentAlpha == ShaderMaster.SUPER_DRAW) {
//            super.draw(batch, 1);
//            return;
//        }
//        ShaderMaster.drawWithCustomShader(this, batch,
//         shader, true);
//
//        if (underlayOffsetX == 0 || underlayOffsetY == 0)
//            setUnderlay(underlay);
//
//    }
//
//    public List<RadialSlot> getChildNodes() {
//        if (!ListMaster.isNotEmpty(childNodes))
//            if (lazyChildInitializer != null)
//                lazyChildInitializer.run();
//        return childNodes;
//    }
//
//    public void setChildNodes(List<RadialValueContainer> childNodes) {
//        this.childNodes = childNodes;
//    }
//
//    @Override
//    public RadialValueContainer getParent() {
//        return parent;
//    }
//
//    public void setParent(RadialValueContainer parent) {
//        this.parent = parent;
//    }
//
//    public void setChildVisible(boolean visible) {
//        childNodes.forEach(el -> el.setVisible(visible));
//    }
//
//    @Override
//    public void setVisible(boolean visible) {
//        setUnderlay(
//         valid ?
//          getUnderlayDefault().getTextureRegion() :
//          getUnderlayDisabled().getTextureRegion());
//        setUnderlayOffsetX(0);
//        setUnderlayOffsetY(0);
//        if (visible) {
//            if (infoTextSupplier != null) {
//                if (infoLabel == null) {
//                    infoLabel = new Label(infoTextSupplier.getVar(), StyleHolder.getSizedLabelStyle(FONT.RU, 18));
//                    addActor(infoLabel);
//                } else {
//                    infoLabel.setText(infoTextSupplier.getVar());
//                }
//                infoLabel.setColor(valid ? new Color(1, 1, 1, 1) : new Color(1, 0.2f, 0.3f, 1));
//
//                infoLabel.setPosition((64 - infoLabel.getWidth()) / 2,
//                 (getHeight() + infoLabel.getHeight()) / 2);
//            }
//
//            if (tooltip == null)
//                if (getTooltipSupplier() != null) {
//                    try {
//                        tooltip = tooltipSupplier.getVar();
//                    } catch (Exception e) {
//                        main.system.ExceptionMaster.printStackTrace(e);
//                    }
//                    if (tooltip != null)
//                        addListener(tooltip.getController());
//
//                }
//        }
//    }
//
//    protected RADIAL_UNDERLAYS getUnderlayDefault() {
//        if (isAltUnderlay())
//            return RADIAL_UNDERLAYS.BLACK_BEVEL2;
//        return RADIAL_UNDERLAYS.BLACK_BEVEL;
//    }
//
//    protected RADIAL_UNDERLAYS getUnderlayGlow() {
//        if (isAltUnderlay())
//            return RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW2;
//        return RADIAL_UNDERLAYS.BLACK_BEVEL_GLOW;
//    }
//
//    protected RADIAL_UNDERLAYS getUnderlayDisabled() {
//        if (isAltUnderlay())
//            return RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED2;
//        return RADIAL_UNDERLAYS.BLACK_BEVEL_DISABLED;
//    }
//
//
//    public Supplier<Tooltip> getTooltipSupplier() {
//        return tooltipSupplier;
//    }
//
//    public void setUnderlayOffsetX(float underlayOffsetX) {
//        this.underlayOffsetX = underlayOffsetX;
//    }
//
//    public void setUnderlayOffsetY(float underlayOffsetY) {
//        this.underlayOffsetY = underlayOffsetY;
//    }

}
