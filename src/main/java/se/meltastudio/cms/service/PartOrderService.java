package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.dto.PartOrderDTO;
import se.meltastudio.cms.repository.*;

@Service
public class PartOrderService {

    private final PartOrderRepository partOrderRepository;

    public PartOrderService(PartOrderRepository partOrderRepository) {

        this.partOrderRepository = partOrderRepository;

    }


}
