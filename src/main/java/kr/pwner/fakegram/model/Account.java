package kr.pwner.fakegram.model;

import kr.pwner.fakegram.dto.account.UpdateAccountDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

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
    private Date lastSignIn; // new Date();

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
        this.lastSignIn = new Date();
        this.refreshToken = null;
    }

    public void Update(UpdateAccountDto.Request account) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(account.getPassword());
        this.id = Objects.nonNull(account.getId()) ? account.getId() : this.getId();
        this.password = Objects.nonNull(account.getPassword()) ? encryptedPassword : this.getPassword();
        this.email = Objects.nonNull(account.getEmail()) ? account.getEmail() : this.getEmail();
        this.name = Objects.nonNull(account.getName()) ? account.getName() : this.getName();
        this.updatedAt = new Date();
    }

    public void Delete() {
        this.isActivate = false;
        this.password = null;
    }

    public void SignIn(String refreshToken) {
        this.refreshToken = refreshToken;
        this.lastSignIn = new Date();
    }

    public void SignOut() {
        this.refreshToken = null;
    }

    public Account() {
    }
}