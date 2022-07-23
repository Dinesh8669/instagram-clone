package kr.pwner.fakegram.repository;

import kr.pwner.fakegram.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {
    Account findById(String id);
    Account findByUuid(String uuid);
    Account findByUuidAndIsActivatedTrue(String uuid);
    Account findByIdAndIsActivatedTrue(String uuid);
}
