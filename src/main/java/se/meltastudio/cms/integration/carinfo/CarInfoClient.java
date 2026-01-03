package se.meltastudio.cms.integration.carinfo;

import se.meltastudio.cms.integration.carinfo.dto.CarInfoVehicleDto;

public interface CarInfoClient {
    CarInfoVehicleDto getVehicleByRegistrationNumber(String registrationNumber);
}
