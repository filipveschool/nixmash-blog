package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_data")
public class UserData implements Serializable {

    private static final long serialVersionUID = -706243242873257798L;

    @Id
    @Column(name = "user_id", nullable = false)
    protected long userId;

    @Basic
    @Column(name = "login_attempts", nullable = false)
    public int loginAttempts;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastlogin_datetime", nullable = true)
    public Date lastloginDatetime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_datetime")
    public Date createdDatetime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved_datetime", nullable = true)
    public Date approvedDatetime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "invited_datetime")
    public Date invitedDatetime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "accepted_datetime")
    public Date acceptedDatetime;

    @Basic
    @Column(name = "invited_by_id", nullable = false)
    public long invitedById;

    @Basic
    @Column(name = "ip", length = 25)
    public String ip;

    public UserData(){

    }

    @Override
    public String toString() {
        return "UserData{" +
                "userId=" + userId +
                ", loginAttempts=" + loginAttempts +
                ", lastloginDatetime=" + lastloginDatetime +
                ", createdDatetime=" + createdDatetime +
                ", approvedDatetime=" + approvedDatetime +
                ", invitedDatetime=" + invitedDatetime +
                ", acceptedDatetime=" + acceptedDatetime +
                ", invitedById=" + invitedById +
                ", ip='" + ip + '\'' +
                '}';
    }
}
