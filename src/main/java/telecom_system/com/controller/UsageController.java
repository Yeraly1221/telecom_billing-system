package telecom_system.com.controller;


import org.springframework.web.bind.annotation.*;
import telecom_system.com.request.ProcessUsageRequest;
import telecom_system.com.usecase.ProcessUsageUseCase;


@RestController
@RequestMapping("/api/usage")

public class UsageController {
    private final ProcessUsageUseCase processUsageUseCase;

    public UsageController(ProcessUsageUseCase processUsageUseCase) {
        this.processUsageUseCase = processUsageUseCase;
    }

    @PutMapping()
    public void processUsage(@RequestBody ProcessUsageRequest request) {
        processUsageUseCase.processUsage(
                request.getCustomerId(),
                request.getTrafficType(),
                request.getAmount(),
                request.getPaymentType()
        );
    }
}
