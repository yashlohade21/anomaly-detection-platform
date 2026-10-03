package com.anomaly.ingestion.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class BloomFilterService {

    private static final int EXPECTED_INSERTIONS = 10_000_000;
    private static final double FALSE_POSITIVE_PROBABILITY = 0.01;

    private final BloomFilter<CharSequence> bloomFilter;

    public BloomFilterService() {
        this.bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                EXPECTED_INSERTIONS,
                FALSE_POSITIVE_PROBABILITY
        );
        log.info("BloomFilter initialized with expectedInsertions={}, fpp={}",
                EXPECTED_INSERTIONS, FALSE_POSITIVE_PROBABILITY);
    }

    /**
     * Checks if the given event ID is a probable duplicate.
     * If not seen before, the event ID is added to the filter.
     *
     * @param eventId the event ID to check
     * @return true if the event ID is likely a duplicate, false otherwise
     */
    public boolean isDuplicate(String eventId) {
        if (eventId == null) {
            return false;
        }

        boolean mightContain = bloomFilter.mightContain(eventId);
        if (!mightContain) {
            bloomFilter.put(eventId);
        } else {
            log.info("Duplicate event detected: event_id={}", eventId);
        }
        return mightContain;
    }
}
