package de.erdbeerbaerlp.splatcord2.storage.json.splatoon3;

public class Schedules3 {
    public SchDat data;

    @Override
    public String toString() {
        return "Schedules3{" +
                "data=" + data +
                '}';
    }

    public static class SchDat{
        public SchedulesRoot regularSchedules = new SchedulesRoot();
        public SchedulesRoot bankaraSchedules = new SchedulesRoot();
        public SchedulesRoot xSchedules = new SchedulesRoot();
        public SchedulesRoot leagueSchedules = new SchedulesRoot();

        @Override
        public String toString() {
            return "SchDat{" +
                    "regularSchedules=" + regularSchedules +
                    '}';
        }
    }
}
