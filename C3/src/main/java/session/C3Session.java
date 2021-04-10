package session;

import data.C3Enums;

public class C3Session {
    private C3Enums.SessionType sessionType;
    private C3Enums.Direction direction;
    private Integer duration;

    public C3Session(C3Enums.SessionType sessionType, C3Enums.Direction direction, Integer duration) {
        this.sessionType = sessionType;
        this.direction = direction;
        this.duration = duration;
    }

    public C3Enums.SessionType getSessionType() {
        return sessionType;
    }

    public C3Enums.Direction getDirection() {
        return direction;
    }

    public Integer getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return sessionType+ " " +  direction +
                "Session\n Duration: " + duration  ;
    }
}
