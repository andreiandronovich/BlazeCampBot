package by.andronovich.bot.BlazeCampBot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "schedule")
public class Schedule {

    @Id
    private String day;
    @Column(length = 1024)
    private String fullScheduleDescription;

    // Todo приватное поле с фото


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFullScheduleDescription() {
        return fullScheduleDescription;
    }

    public void setFullScheduleDescription(String fullScheduleDescription) {
        this.fullScheduleDescription = fullScheduleDescription;
    }
}
