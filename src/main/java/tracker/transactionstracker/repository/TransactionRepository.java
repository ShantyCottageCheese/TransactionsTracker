package tracker.transactionstracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tracker.transactionstracker.model.TransactionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,String> {
    Optional<TransactionEntity> findFirstByChainOrderByDateDesc(String chain);

    @Query("SELECT t FROM TRANSACTIONS_TRACKER t WHERE t.twentyFourHourChange IS NULL")
    List<TransactionEntity> findAllWithNullChange();
}