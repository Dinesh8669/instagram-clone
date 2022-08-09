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
    Follow findByFromIdxAndToIdx(Long fromIdx, Long toIdx);

    void deleteByIdx(Long idx);

    @Query(value="SELECT a.id, a.name, a.email " +
            "FROM account a, follow f " +
            "f.toIdx = a.idx AND f.fromIdx = :idx", nativeQuery = true)
    List<Map<String, String>> getFollowerByIdx(@Param("idx") Long idx);

    @Query(value="SELECT ta.id, ta.name, ta.email " +
            "FROM account ta, follow tf " +
            "f.fromIdx = a.idx AND f.toIdx = :idx", nativeQuery = true)
    List<Map<String, String>> getFollowingByIdx(@Param("idx") Long idx);
}