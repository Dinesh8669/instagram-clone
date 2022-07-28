package kr.pwner.fakegram.model;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity(name = "tb_account")
public class Account {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long idx; // UUID.randomUUID().toString();

    @Column(nullable = false)
    private Boolean isActivate; // true;

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
    private String refreshToken; // null;

    @Builder
    public Account(String id, String password, String name, String email) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;

        this.isActivate = true;
        this.role = "USER";
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.lastSignin = new Date();
        this.refreshToken = null;
    }

    public void Update(String id, String password, String name, String email) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public void Delete() {
        this.isActivate = false;
        this.password = null;
    }

    public void SaveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void SignOut() {
        this.refreshToken = null;
    }

    public Account() {
    }
}