package kr.pwner.fakegram.repository;

import kr.pwner.fakegram.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findBySourceIdxAndTargetIdx(Long sourceIdx, Long targetIdx);
    void deleteByIdx(Long idx);
}
