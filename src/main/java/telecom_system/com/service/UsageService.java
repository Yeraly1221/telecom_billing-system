package telecom_system.com.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.*;
import telecom_system.com.entity.GlobalRate;
import telecom_system.com.entity.Subscription;
import telecom_system.com.entity.UsageRecord;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.GlobalRateRepository;
import telecom_system.com.repository.UsageRecordRepository;

import java.math.BigDecimal;


@Service
@AllArgsConstructor
public class UsageService {
    private final GlobalRateRepository globalRateRepository;
    private final UsageRecordRepository usageRecordRepository;

    @Transactional
    public UsageRecord createUsageRecord(TrafficType trafficType, Integer amount, Subscription subscription){
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setUsageStatus(UsageStatus.NEW);
        usageRecord.setTrafficType(trafficType);
        usageRecord.setAmount(amount);
        usageRecord.setSubscription(subscription);
        usageRecordRepository.save(usageRecord);
        return usageRecord;
    }

    public BigDecimal getGlobalRateByType(TrafficType trafficType){
        GlobalRate globalRate = globalRateRepository.findByTrafficType(trafficType)
                .orElseThrow(() ->new BusinessException("Global rate does not have this trafficType"));

        return globalRate.getPrice();
    }

    public void saveUsageRecord(UsageRecord usageRecord){
        usageRecordRepository.save(usageRecord);
    }

    public void markProcessed(UsageRecord usageRecord) {
        usageRecord.setUsageStatus(UsageStatus.PROCESSED);
    }

    public void markFailed(UsageRecord usageRecord) {
        usageRecord.setUsageStatus(UsageStatus.FAILED);
    }



}