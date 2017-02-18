package main.swing.generic.misc;

import main.content.values.parameters.PARAMETER;

import javax.swing.*;

public class PointComp extends JSpinner {

    private PARAMETER param;
    private SpinnerNumberModel model;

    public PointComp(Number value, Comparable minimum, Comparable maximum,
                     Number step) {
        super(new SpinnerNumberModel(value, minimum, maximum, step));

    }

    public PointComp(Number value) {
        super(new SpinnerNumberModel(value, Integer.MIN_VALUE,
                Integer.MAX_VALUE, 1));
    }

    public PointComp(Integer intParam, boolean attributes) {
        this(intParam);
    }


    public PARAMETER getParam() {
        return param;
    }

    public void setParam(PARAMETER param) {
        this.param = param;
    }

    @Override
    public SpinnerNumberModel getModel() {
        return (SpinnerNumberModel) super.getModel();
    }

    public Comparable getMinimum() {
        return getModel().getMinimum();
    }

    public void setMinimum(Comparable minimum) {
        getModel().setMinimum(minimum);
    }

    public Comparable getMaximum() {
        return getModel().getMaximum();
    }

    public void setMaximum(Comparable maximum) {
        getModel().setMaximum(maximum);
    }

    public Number getStepSize() {
        return getModel().getStepSize();
    }

    public void setStepSize(Number stepSize) {
        getModel().setStepSize(stepSize);
    }

}
