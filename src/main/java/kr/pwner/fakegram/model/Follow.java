package kr.pwner.fakegram.model;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long idx;

    @Column(nullable = false)
    private Long sourceIdx;

    @Column(nullable = false)
    private Long targetIdx;

    public Follow() {}
    @Builder
    public Follow(Long sourceIdx, Long targetIdx) {
        this.sourceIdx = sourceIdx;
        this.targetIdx = targetIdx;
    }
}
