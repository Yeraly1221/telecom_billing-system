package telecom_system.com.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import telecom_system.com.entity.Tariff;
import telecom_system.com.exception.BusinessException;
import telecom_system.com.repository.TariffRepository;

@Service
@AllArgsConstructor
public class TariffService {
    private final TariffRepository tariffRepository;

    public Tariff getTariffById(long tarifId){
        return tariffRepository.findById(tarifId)
                .orElseThrow(() -> new BusinessException("Tariff with this id does not exist"));
    }


}
