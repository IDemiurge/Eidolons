package main.content.parameters;

import main.content.VALUE;

public interface PARAMETER extends VALUE {
    boolean isDynamic();

    boolean isAttribute();

    boolean isMastery();

    boolean isMod();

}
