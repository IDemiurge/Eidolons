package main.content.parameters;

public class MultiParameter extends Param {

    private PARAMETER[] parameters;
    private String separator;
    private String suffix;
    private String prefix;

    public MultiParameter(String separator, PARAMETER... parameters) {
        super(parameters[0]);
        this.separator = separator;
        this.parameters = parameters;
    }

    public PARAMETER[] getParameters() {
        return parameters;
    }

    public String getSeparator() {
        return separator;
    }

}
