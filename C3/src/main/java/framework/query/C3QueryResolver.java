package framework.query;

import framework.C3Handler;
import framework.C3Manager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;


public class C3QueryResolver  extends C3Handler {

    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
    public static final String YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query";
    private static final int MINS_DELAY_AFTER_QUERY = 8;

    public C3QueryResolver(C3Manager manager) {
        super(manager);
    }

    public boolean resolve(C3_Query query) {
        if (query == null) {
            return false;
        }
        String searchTerm = query.getText().replace(" ", "+");

        String searchUri=(query.getCategory().youTube? YOUTUBE_SEARCH_URL : GOOGLE_SEARCH_URL) +
                searchTerm  ;
        URI uri = URI.create(searchUri).normalize();
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        manager.getQLogger().started(query);
        startResponseTimer(query);

        return true;
    }

    private void startResponseTimer(C3_Query query) {
        long delay=MINS_DELAY_AFTER_QUERY*60000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                manager.notifyTimerElapsed(query);
            }
        }, delay);
    }

    public void promptQueryInput(C3_Query query) {
        String input = JOptionPane.showInputDialog("Wazzup with " + query.getText() + "?");
        manager.getQLogger().done(query, input);

        // openLogBook();

    }


}
