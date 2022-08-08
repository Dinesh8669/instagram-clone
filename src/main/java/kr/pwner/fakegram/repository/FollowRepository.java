package kr.pwner.fakegram.repository;

import kr.pwner.fakegram.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findBySourceIdxAndTargetIdx(Long sourceIdx, Long targetIdx);

    void deleteByIdx(Long idx);

    @Query(value="SELECT ta.id, ta.name, ta.email " +
            "FROM account ta, follow tf " +
            "WHERE tf.source_idx = ta.idx AND tf.target_idx = :idx", nativeQuery = true)
    List<Map<String, String>> getFollowerByIdx(@Param("idx") Long idx);

    @Query(value="SELECT ta.id, ta.name, ta.email " +
            "FROM account ta, follow tf " +
            "WHERE tf.target_idx = ta.idx AND tf.source_idx = :idx", nativeQuery = true)
    List<Map<String, String>> getFollowingByIdx(@Param("idx") Long idx);
}