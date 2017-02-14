package main.music.m3u;

import main.logic.AT_PROPS;
import main.music.MusicCore;
import main.music.entity.Track;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class TrackReader<E> {
    Class<E> clazz;

    public TrackReader(Class<E> clazz) {
        this.clazz = clazz;
    }

    public List<E> getTracksFromList(String path) {
        List<E> tracks = new LinkedList<>();
        E track = null;
        E lastTrack = null;
        for (String line : FileManager.readFileLines(path)) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.equals(M3uGenerator.M3U_PREFIX)) {
                continue;
            }
            // #EXTINF:91,Two Steps From Hell - King's Legion
            if (line.contains(M3uGenerator.M3U_TRACK_PREFIX_UTF8)) {
                track = initTrack(line);
            } else if (line.contains(M3uGenerator.M3U_TRACK_PREFIX)) {
                if (track == null) {
                    track = initTrack(line);
                }
            } else {
                if (track != null) {
                    addTrack(tracks, track, line);
                }
                track = null;
            }
        }

        return tracks;
    }

    private void addTrack(List<E> tracks, E track, String line) {
        if (track instanceof Track) {
            Track trackObj = (Track) track;
            trackObj.setPath(line);

        }
        tracks.add(track);
    }

    private E getTrack(String name) {
        if (clazz == Track.class) {
            return (E) MusicCore.getTrack(name);
        }
        return (E) name;
    }

    private E initTrack(String line) {
        E track;
        String name = line.substring(line.indexOf('-') + 1);
        if (line.indexOf('-') == -1) {
            name = line.substring(line.indexOf(',') + 1);
        }
        name = name.trim();
        track = getTrack(name);
        if (track instanceof Track) {
            Track trackObj = (Track) track;
            String artist = StringMaster.getSubString(line, ',', '-', false).trim();
            trackObj.setProperty(AT_PROPS.ARTIST, artist);
            // String duration = line.substring(line.indexOf(':') + 1,
            // line.indexOf(','));
            // track.setParam(G_PARAMS.DURATION, duration);
            return track;
        } else {
            return track;
        }
    }

}
