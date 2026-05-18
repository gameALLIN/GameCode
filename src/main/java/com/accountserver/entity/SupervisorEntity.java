package com.accountserver.entity;

import com.webcore.data.persist.AbstractEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "supervisor_list")
public class SupervisorEntity extends AbstractEntity {
    @Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "device")
    private String device;

    @Column(name = "remote_ip")
    private String remoteIp;

    public SupervisorEntity() {
    }

    public SupervisorEntity(String device, String remoteIp) {
        this();
        this.device = device;
        this.remoteIp = remoteIp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public String getRemoteIp() {
        return remoteIp;
    }
}
