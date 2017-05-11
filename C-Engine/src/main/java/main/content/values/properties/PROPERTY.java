package main.content.values.properties;

import main.content.VALUE;

/**
 * Property types: [by obj type], e.g. UNIT - Race, Class. , ...
 * <portrait>
 * Property values: (for race - human , ...
 *
 * @author JustMe
 */
public interface PROPERTY extends VALUE {

    boolean isContainer();

    boolean isDynamic();

    // get tag
}
