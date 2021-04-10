package session;

import data.C3Enums;
import framework.C3Handler;
import framework.C3Manager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;
import main.system.util.DialogMaster;
import music.PlaylistHandler;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C3SessionHandler extends C3Handler {
    private C3Session currentSession;

    public C3SessionHandler(C3Manager manager) {
        super(manager);
    }

    public Integer[] getDurationOptions(C3Enums.SessionType sessionType) {
        switch (sessionType) {
            case Preparation -> {
                return new Integer[]{
                        20, 30, 40
                };
            }
            case Perseverance -> {
                return new Integer[]{
                        90, 120, 150
                };
            }
            case Liberation_Short, Night_Short -> {
                return new Integer[]{
                        40, 50, 60
                };
            }
            case Liberation_Long, Night_Long -> {
                return new Integer[]{
                        75, 100, 125
                };
            }
            case Freedom -> {
                return new Integer[]{
                        60, 90, 120, 150
                };
            }
        }
        return null;
    }

    // via separate launch?
    public void initSession() {
        boolean manual = true;
        C3Enums.SessionType sessionType = null;
        if (manual) {
            sessionType = new EnumMaster<C3Enums.SessionType>().selectEnum(C3Enums.SessionType.class);
        } else {
            sessionType = getSessionTypeAuto();
        }

        C3Enums.Direction direction = new EnumMaster<C3Enums.Direction>().selectEnum(C3Enums.Direction.class);

        Integer[] durationOptions = getDurationOptions(sessionType);
        Integer duration = (Integer) DialogMaster.getChosenOption("What's the timing?", durationOptions);
        currentSession = new C3Session(sessionType, direction, duration);
        manager.getTimerHandler().initTimer(currentSession);


        initSessionMusic(sessionType);

    }

    private void initSessionMusic(C3Enums.SessionType sessionType) {
        String musPath = getMusForSession(sessionType);
        List<File> filesFromDirectory = FileManager.getFilesFromDirectory(musPath, false, false, false);
        String playlistPath = null;
        boolean randomMus = false;
        if (randomMus) {
            playlistPath = (String) RandomWizard.getRandomListObject(filesFromDirectory);
        } else {
            Collections.shuffle(filesFromDirectory);
            long maxChoiceSize = 3;
            Object[] m3uOptions = filesFromDirectory.stream().map(file -> file.getName()).limit(maxChoiceSize).collect(
                    Collectors.toList()
            ).toArray();
            playlistPath = (String) DialogMaster.getChosenOption("What's the tune?", m3uOptions);
        }
        if (!PlaylistHandler.play(playlistPath)){
            PlaylistHandler.play(musPath, playlistPath);
        }

    }

    private String getMusForSession(C3Enums.SessionType sessionType) {
        return PlaylistHandler.ROOT_PATH_PICK + "SESSION/" + sessionType+"/";
    }

    private C3Enums.SessionType getSessionTypeAuto() {
        // int day = Calendar.getInstance().getTime().getDay();
        // int hour = Calendar.getInstance().getTime().getHours();
        // boolean altGymDay;
        // boolean weekend;
        // switch (day) {
        //     case 1:
        //     case 2:
        //     case 3:
        //     case 4:
        //     case 5:
        //     case 6:
        //     case 0:
        // }
        return null;
    }

    public C3Session getCurrentSession() {
        return currentSession;
    }


}




















