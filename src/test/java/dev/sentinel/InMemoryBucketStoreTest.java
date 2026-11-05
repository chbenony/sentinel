package dev.sentinel;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryBucketStoreTest {

    private long now = 0;

    @Test
    void sameKeyReturnsSameBucket() {
        LongSupplier clock = () -> now;
        InMemoryBucketStore bucketStore = new InMemoryBucketStore(clock);
        LimitKey keyA = new LimitKey("app_1", "res_1", "princ_1");
        Policy policyA = new Policy(1, 2.0);

        LimitKey keyB = new LimitKey("app_1", "res_1", "princ_1");
        Policy policyB = new Policy(1, 2.0);

        TokenBucket bucketA = bucketStore.findOrCreate(keyA, policyA);
        TokenBucket bucketB = bucketStore.findOrCreate(keyB, policyB);

        assertSame(bucketA, bucketB);
    }

    @Test
    void differentPrincipalsGetDifferentBuckets() {
        LongSupplier clock = () -> now;
        InMemoryBucketStore bucketStore = new InMemoryBucketStore(clock);
        LimitKey keyA = new LimitKey("app_1", "res_1", "princ_1");
        Policy policyA = new Policy(1, 2.0);

        LimitKey keyB = new LimitKey("app_1", "res_1", "princ_2");
        Policy policyB = new Policy(1, 2.0);

        TokenBucket bucketA = bucketStore.findOrCreate(keyA, policyA);
        TokenBucket bucketB = bucketStore.findOrCreate(keyB, policyB);

        assertNotSame(bucketA, bucketB);
    }

    @RepeatedTest(20)
    @Timeout(10)
    void sameKeyReturnsSameBucketUnderConcurrency() throws Exception {
        LongSupplier clock = () -> now;

        InMemoryBucketStore bucketStore = new InMemoryBucketStore(clock);

        LimitKey key = new LimitKey("app_1", "res_1", "princ_1");
        Policy policy = new Policy(1, 2.0);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<TokenBucket>> futures = new ArrayList<>();

        try (ExecutorService executors = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < 8; i++) {
                futures.add(executors.submit(() -> {
                    start.await();
                    return bucketStore.findOrCreate(key, policy);
                }));
            }
            start.countDown();
        }

        TokenBucket first = futures.getFirst().get();
        for (Future<TokenBucket> future : futures) {
            assertSame(first, future.get());
        }
    }

}
