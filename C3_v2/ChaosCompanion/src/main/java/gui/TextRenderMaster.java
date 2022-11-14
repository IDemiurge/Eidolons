package gui;

/**
 * Created by Alexander on 11/13/2022
 */
public class TextRenderMaster {

    public static String getProgressBar(int percent) {
        StringBuilder builder = new StringBuilder();
        percent = Math.round(percent / 10f);
        for (int i = 0; i < percent; i++) {
            builder.append(">");
        }
        for (int i = 0; i < 10 - percent; i++) {
            builder.append("| ");
        }
        builder.append(".V.");
        return builder.toString();
    }
}
