package kr.pwner.fakegram.model;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Getter
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

    @Builder
    public Account(String id, String password, String name, String email) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;

        this.uuid = UUID.randomUUID().toString();
        this.isActivated = true;
        this.role = "USER";
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.lastSignin = new Date();
        this.refreshTokenUuid = null;
    }

    public void Update(String id, String password, String name, String email){
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public void Delete(){
        this.isActivated = false;
        this.password = null;
    }

    public void SignIn(String refreshTokenUuid){
        this.refreshTokenUuid = refreshTokenUuid;
    }

    public void SignOut(){
        this.refreshTokenUuid = null;
    }

    public Account() {}
}