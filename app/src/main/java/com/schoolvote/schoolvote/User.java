package com.schoolvote.schoolvote;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private long grade_update;
    private long clroom_update;
    private String number_update;
    private boolean isAdmin;
    private long feedback;
    private String joiningVoteTitle;
    private String joiningMeetingTitle;
    private String school;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGrade_update(long grade_update) {
        this.grade_update = grade_update;
    }

    public void setClroom_update(long clroom_update) {
        this.clroom_update = clroom_update;
    }

    public void setNumber_update(String number_update) {
        this.number_update = number_update;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setJoiningVoteTitle(String joiningVoteTitle) { this.joiningVoteTitle = joiningVoteTitle; }

    public void setFeedback(long feedback) { this.feedback = feedback; }

    public void setJoiningMeetingTitle(String joiningMeetingTitle) { this.joiningMeetingTitle = joiningMeetingTitle; }

    public void setSchool(String school) { this.school = school; }

    public String getEmail() { return email; }

    public long getGrade_update() { return grade_update; }

    public long getClroom_update() { return clroom_update; }

    public String getNumber_update() { return number_update; }

    public boolean isAdmin() { return isAdmin; }

    public String getJoiningVoteTitle() { return joiningVoteTitle; }

    public long getFeedback() { return feedback; }

    public String getJoiningMeetingTitle() { return joiningMeetingTitle; }

    public String getSchool() { return school; }
}
