package kr.pwner.fakegram.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="tb_follow")
public class Follow {
    @Id
    @GeneratedValue
    private String id;

    @Column(nullable = false)
    private String sourceId;

    @Column(nullable = false)
    private String destinationId;
}
