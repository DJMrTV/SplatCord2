package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3;

import java.time.Instant;

public class Schedule3 {
    public String startTime;
    public String endTime;
    public MatchSetting regularMatchSetting;
    public MatchSetting[] bankaraMatchSettings;

    public long getStartTime(){
        return Instant.parse(startTime).toEpochMilli()/1000;
    }
    public long getEndTime(){

        return Instant.parse(endTime).toEpochMilli()/1000;
    }

    @Override
    public String toString() {
        return "Schedule3{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", regularMatchSetting=" + regularMatchSetting +
                '}';
    }
}
