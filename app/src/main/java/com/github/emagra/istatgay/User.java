package com.github.emagra.istatgay;

public class User {

    private String sex;
    private boolean status;
    private long firstCommit,
            commitTime;

    public User() {
    }

    public User(String sex, boolean status, long firstCommit, long commitTime) {
        this.sex = sex;
        this.status = status;
        this.firstCommit = firstCommit;
        this.commitTime = commitTime;
    }

    public User(String sex, boolean status) {
        this.sex = sex;
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(long commitTime) {
        this.commitTime = commitTime;
    }

    public long getFirstCommit() {
        return firstCommit;
    }

    public void setFirstCommit(long firstCommit) {
        this.firstCommit = firstCommit;
    }
}
