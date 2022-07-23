package kr.pwner.fakegram.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Accessors(chain=true)
@Entity(name="tb_account")
public class Account {
    @Id // PK
    @Column(nullable = false)
    private String uuid; // UUID.randomUUID().toString();

    @Column(nullable = false)
    private Boolean isActivated; // true;

    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false, length = 72)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role; // "USER";

    @Column(nullable = false)
    private Date createdAt; // new Date();

    @Column(nullable = false)
    private Date updatedAt; // new Date();

    @Column(nullable = false)
    private Date lastSignin; // new Date();

    @Column()
    private String refreshTokenUuid; // null;
}

