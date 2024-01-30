package tracker.transactionstracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tracker.transactionstracker.model.TransactionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,String> {
    @Query(value = "SELECT * FROM TRANSACTIONS_TRACKER " +
            "WHERE chain = :chain " +
            "AND date < :date " +
            "AND date >= :dayBefore " +
            "ORDER BY date DESC LIMIT 1", nativeQuery = true)
    Optional<TransactionEntity> findFirstByChainAndDateBeforeWithLimit(
            @Param("chain") String chain,
            @Param("date") Long endDate,
            @Param("dayBefore") Long startDate);
    @Query("SELECT t FROM TRANSACTIONS_TRACKER t WHERE t.date > :fromDate")
    List<TransactionEntity> findAllTransactionsFromLastDays(@Param("fromDate") Long fromDate);
    @Query("SELECT t FROM TRANSACTIONS_TRACKER t WHERE t.twentyFourHourChange IS NULL")
    List<TransactionEntity> findAllWithNullChange();

    List<TransactionEntity> findAllByDateBetween(long localDateTime, long localDateTime1);
}
