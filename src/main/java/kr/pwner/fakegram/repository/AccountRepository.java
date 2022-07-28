package kr.pwner.fakegram.repository;

import kr.pwner.fakegram.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findById(String id);
    Account findByIdAndIsActivateTrue(String id);
    Account findByIdx(Long idx);
    Account findByIdxAndIsActivateTrue(Long idx);
}
