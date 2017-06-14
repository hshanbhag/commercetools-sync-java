package com.commercetools.sync.commons.helpers;

import com.commercetools.sync.categories.helpers.CategorySyncStatisticsBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseSyncStatisticsTest {
    private static final long ONE_HOUR_FIFTEEN_MINUTES_AND_TWENTY_SECONDS_IN_MILLIS = 75 * 60 * 1000 + 20 * 1000L;
    private BaseSyncStatisticsBuilder baseSyncStatisticsBuilder;

    @Before
    public void setup() {
        baseSyncStatisticsBuilder = new CategorySyncStatisticsBuilder();
    }

    @Test
    public void getUpdated_WithNoUpdated_ShouldReturnZero() {
        assertThat(baseSyncStatisticsBuilder.build().getUpdated()).isEqualTo(0);
    }

    @Test
    public void incrementUpdated_WithNoSpecifiedTimes_ShouldIncrementUpdatedValue() {
        baseSyncStatisticsBuilder.incrementUpdated();
        assertThat(baseSyncStatisticsBuilder.build().getUpdated()).isEqualTo(1);
    }

    @Test
    public void incrementUpdated_WithSpecifiedTimes_ShouldIncrementUpdatedValue() {
        baseSyncStatisticsBuilder.incrementUpdated(5);
        assertThat(baseSyncStatisticsBuilder.build().getUpdated()).isEqualTo(5);
    }

    @Test
    public void getCreated_WithNoCreated_ShouldReturnZero() {
        assertThat(baseSyncStatisticsBuilder.build().getCreated()).isEqualTo(0);
    }

    @Test
    public void incrementCreated_WithNoSpecifiedTimes_ShouldIncrementCreatedValue() {
        baseSyncStatisticsBuilder.incrementCreated();
        assertThat(baseSyncStatisticsBuilder.build().getCreated()).isEqualTo(1);
    }

    @Test
    public void incrementCreated_WithSpecifiedTimes_ShouldIncrementCreatedValue() {
        baseSyncStatisticsBuilder.incrementCreated(2);
        assertThat(baseSyncStatisticsBuilder.build().getCreated()).isEqualTo(2);
    }

    @Test
    public void getUpToDate_WithNoUpToDate_ShouldReturnZero() {
        assertThat(baseSyncStatisticsBuilder.build().getUpToDate()).isEqualTo(0);
    }

    @Test
    public void incrementUpToDate_WithNoSpecifiedTimes_ShouldIncrementUpToDateValue() {
        baseSyncStatisticsBuilder.incrementUpToDate();
        assertThat(baseSyncStatisticsBuilder.build().getUpToDate()).isEqualTo(1);
    }

    @Test
    public void incrementUpToDate_WithSpecifiedTimes_ShouldIncrementUpToDateValue() {
        baseSyncStatisticsBuilder.incrementUpToDate(5);
        assertThat(baseSyncStatisticsBuilder.build().getUpToDate()).isEqualTo(5);
    }

    @Test
    public void getProcessed_WithNoProcessed_ShouldReturnZero() {
        assertThat(baseSyncStatisticsBuilder.build().getProcessed()).isEqualTo(0);
    }

    @Test
    public void getProcessed_WithOtherStatsIncremented_ShouldReturnSumOfOtherValues() {
        baseSyncStatisticsBuilder.incrementCreated();
        baseSyncStatisticsBuilder.incrementUpdated();
        baseSyncStatisticsBuilder.incrementUpToDate();
        baseSyncStatisticsBuilder.incrementFailed();
        assertThat(baseSyncStatisticsBuilder.build().getProcessed()).isEqualTo(4);
    }

    @Test
    public void getFailed_WithNoFailed_ShouldReturnZero() {
        assertThat(baseSyncStatisticsBuilder.build().getFailed()).isEqualTo(0);
    }

    @Test
    public void incrementFailed_WithNoSpecifiedTimes_ShouldIncrementFailedValue() {
        baseSyncStatisticsBuilder.incrementFailed();
        assertThat(baseSyncStatisticsBuilder.build().getFailed()).isEqualTo(1);
    }

    @Test
    public void incrementFailed_WithSpecifiedTimes_ShouldIncrementFailedValue() {
        baseSyncStatisticsBuilder.incrementFailed(3);
        assertThat(baseSyncStatisticsBuilder.build().getFailed()).isEqualTo(3);
    }

    @Test
    public void getProcesingTimeInMillis_WithNoProcessingTime_ShouldReturnZero() {
        assertThat(baseSyncStatisticsBuilder.build().getProcessingTimeInMillis()).isEqualTo(0L);
    }

    @Test
    public void setProcesingTimeInMillis_ShouldSetProcessingTimeValue() {
        baseSyncStatisticsBuilder.setProcessingTimeInMillis(ONE_HOUR_FIFTEEN_MINUTES_AND_TWENTY_SECONDS_IN_MILLIS);
        assertThat(baseSyncStatisticsBuilder.build().getProcessingTimeInMillis())
            .isEqualTo(ONE_HOUR_FIFTEEN_MINUTES_AND_TWENTY_SECONDS_IN_MILLIS);
    }

    @Test
    public void getFormattedProcessingTime_ShouldReturnFormattedString() {
        baseSyncStatisticsBuilder.setProcessingTimeInMillis(ONE_HOUR_FIFTEEN_MINUTES_AND_TWENTY_SECONDS_IN_MILLIS);
        assertThat(baseSyncStatisticsBuilder.build().getFormattedProcessingTime("d'd, 'H'h, 'm'm, 's's, 'S'ms'"))
            .isEqualTo("0d, 1h, 15m, 20s, 000ms");
    }
}
