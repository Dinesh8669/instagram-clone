package kr.pwner.fakegram.model;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name="tb_upload")
public class Upload {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long idx;

    @Column(nullable=false)
    private Long accountIdx;

    @Column(nullable = false)
    private String fileFullName;

    public Upload(){}

    @Builder
    public Upload(Long accountIdx, String fileFullName){
        this.accountIdx = accountIdx;
        this.fileFullName = fileFullName;
    }
}