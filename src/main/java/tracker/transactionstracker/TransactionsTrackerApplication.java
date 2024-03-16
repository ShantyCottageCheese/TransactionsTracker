package tracker.transactionstracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@SpringBootApplication
@Slf4j
public class TransactionsTrackerApplication {

    public static void main(String[] args) {
        int mb = 1024 * 1024;
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long xmx = memoryBean.getHeapMemoryUsage().getMax() / mb;
        long xms = memoryBean.getHeapMemoryUsage().getInit() / mb;

        log.info("Initial Memory (xms) : {} mb", xms);
        log.info("Max Memory (xmx) : {} mb", xmx);

        SpringApplication.run(TransactionsTrackerApplication.class, args);

    }

}
