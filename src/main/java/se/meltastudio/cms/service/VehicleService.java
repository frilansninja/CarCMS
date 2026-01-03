package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.dto.VehicleDTO;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.*;

import java.time.LocalDate;
import java.util.*;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final EndCustomerRepository endCustomerRepository;
    private final WorkOrderRepository workOrderRepository;
    private final WorkplaceRepository workplaceRepository;
    private final CompanyRepository companyRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleServiceRepository vehicleServiceRepository;

    public VehicleService(VehicleRepository vehicleRepository, EndCustomerRepository endCustomerRepository, WorkOrderRepository workOrderRepository, WorkplaceRepository workplaceRepository, CompanyRepository companyRepository, VehicleModelRepository vehicleModelRepository, VehicleServiceRepository vehicleServiceRepository) {
        this.vehicleRepository = vehicleRepository;
        this.endCustomerRepository = endCustomerRepository;
        this.workOrderRepository = workOrderRepository;
        this.workplaceRepository = workplaceRepository;
        this.companyRepository = companyRepository;
        this.vehicleModelRepository = vehicleModelRepository;
        this.vehicleServiceRepository = vehicleServiceRepository;
    }

    public List<Vehicle> getVehiclesByEndCustomer(Long endCustomerId) {
        return vehicleRepository.findByEndCustomerId(endCustomerId);
    }

    public List<Vehicle> getVehiclesByCompany(Long companyId) {
        return vehicleRepository.findByCompanyId(companyId);
    }

    public List<Vehicle> getVehiclesByWorkPlace(Long workplaceId) {
        return vehicleRepository.findByWorkplaceId(workplaceId);
    }

    public Optional<VehicleDTO> getVehicleById(Long id) {
        return vehicleRepository.findById(id).map(this::toDTO);
    }


    public Vehicle addVehicle(Long endCustomerId, Vehicle vehicle) {
        EndCustomer endCustomer = endCustomerRepository.findById(endCustomerId)
                .orElseThrow(() -> new RuntimeException("EndCustomer not found"));
        vehicle.setEndCustomer(endCustomer);
        if (vehicle.getCompany() == null) {
            vehicle.setCompany(endCustomer.getCompany());
        }
        return vehicleRepository.save(vehicle);
    }

    public void saveVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    vehicle.setRegistrationNumber(updatedVehicle.getRegistrationNumber());
                    vehicle.setMileage(updatedVehicle.getMileage());
                    return vehicleRepository.save(vehicle);
                }).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public List<Map<String, Object>> getVehiclesWithStatusByWorkplace(Long workplaceId) {
        List<Vehicle> vehicles = vehicleRepository.findByWorkplaceId(workplaceId);
        return buildVehicleStatusList(vehicles);
    }

    public List<Map<String, Object>> getVehiclesWithStatusByWorkplace(Long workplaceId, WorkOrderStatus filterStatus,
                                                                      boolean sortByLatestWorkOrder, Integer page, Integer pageSize,
                                                                      LocalDate startDate, LocalDate endDate) {
        List<Vehicle> vehicles = vehicleRepository.findByWorkplaceId(workplaceId);
        List<Map<String, Object>> result = buildVehicleStatusList(vehicles, filterStatus, startDate, endDate);

        if (sortByLatestWorkOrder) {
            result.sort(Comparator.comparing((Map<String, Object> v) -> (LocalDate) v.get("latestWorkOrderDate"), Comparator.nullsLast(Comparator.reverseOrder())));
        }

        if (page != null && pageSize != null && page >= 0 && pageSize > 0) {
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, result.size());
            if (fromIndex < result.size()) {
                result = result.subList(fromIndex, toIndex);
            } else {
                result = Collections.emptyList();
            }
        }
        return result;
    }

    private List<Map<String, Object>> buildVehicleStatusList(List<Vehicle> vehicles) {
        return buildVehicleStatusList(vehicles, null, null, null);
    }

    private List<Map<String, Object>> buildVehicleStatusList(List<Vehicle> vehicles, WorkOrderStatus filterStatus,
                                                             LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            WorkOrder latestWorkOrder = workOrderRepository.findTopByVehicleIdOrderByCreatedDateDesc(vehicle.getId());

            if (filterStatus != null && (latestWorkOrder == null || latestWorkOrder.getStatus() != filterStatus)) {
                continue;
            }

            if (latestWorkOrder != null && startDate != null && latestWorkOrder.getCreatedDate().isBefore(startDate)) {
                continue;
            }
            if (latestWorkOrder != null && endDate != null && latestWorkOrder.getCreatedDate().isAfter(endDate)) {
                continue;
            }

            Map<String, Object> vehicleData = new HashMap<>();
            vehicleData.put("vehicleId", vehicle.getId());
            vehicleData.put("registrationNumber", vehicle.getRegistrationNumber());
            vehicleData.put("mileage", vehicle.getMileage());
            vehicleData.put("currentWorkOrderStatus", latestWorkOrder != null ? latestWorkOrder.getStatus() : "No work order");
            vehicleData.put("latestWorkOrderDate", latestWorkOrder != null ? latestWorkOrder.getCreatedDate() : null);

            result.add(vehicleData);
        }
        return result;
    }

    public VehicleDTO toDTO(Vehicle vehicle) {

        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setCompanyId(vehicle.getCompany().getId());
        dto.setCompanyName(vehicle.getCompany().getName());
        dto.setBrand(vehicle.getVehicleModel().getBrand());
        dto.setModelName(vehicle.getVehicleModel().getModel());
        dto.setVehicleModelId(vehicle.getVehicleModel().getId());
        dto.setYear(vehicle.getVehicleModel().getYear());
        dto.setRegistrationNumber(vehicle.getRegistrationNumber());
        dto.setEndCustomerId(vehicle.getEndCustomer().getId());
        dto.setEndCustomerName(vehicle.getEndCustomer().getName());
        if(vehicle.getLastKnownService() != null)
            dto.setLastKnownService(vehicle.getLastKnownService());
        dto.setMileage(vehicle.getMileage());
        dto.setTransmission(vehicle.getTransmission());
        dto.setLastKnownServiceDate(vehicle.getLastKnownServiceDate());

        if(vehicle.getWorkplace() != null) {
            dto.setWorkplaceId(vehicle.getWorkplace().getId());
            dto.setWorkplaceName(vehicle.getWorkplace().getName());
        }

        List<WorkOrder> workOrders = vehicle.getWorkOrders();
        List<Long> workOrderIds = new ArrayList<>();
        boolean hasActive = false;
        for(WorkOrder wo : workOrders) {
            workOrderIds.add(wo.getId());
            // Check if work order is active (not completed or cancelled)
            if(wo.getStatus() != null &&
               !wo.getStatus().getName().equals("COMPLETED") &&
               !wo.getStatus().getName().equals("CANCELLED")) {
                hasActive = true;
            }
        }
        dto.setWorkOrderIds(workOrderIds);
        dto.setHasActiveWorkOrders(hasActive);
        dto.setRegistrationNumber(vehicle.getRegistrationNumber());
        return dto;

    }

    public Vehicle toEntity(VehicleDTO vehicleDto) {

        Vehicle v = new Vehicle();
        Optional<Company> c = companyRepository.findById(vehicleDto.getCompanyId());
        v.setCompany(c.get());
        v.setId(vehicleDto.getId());
        Optional<VehicleModel> vm = vehicleModelRepository.findById(vehicleDto.getVehicleModelId());
        v.setVehicleModel(vm.get());
        Optional<EndCustomer> endCustomer = endCustomerRepository.findById(vehicleDto.getEndCustomerId());
        v.setEndCustomer(endCustomer.get());
        if(vehicleDto.getLastKnownService() != null)
            v.setLastKnownService(vehicleDto.getLastKnownService());

        v.setMileage(vehicleDto.getMileage());
        v.setTransmission(vehicleDto.getTransmission());
        Optional<Workplace> workplace = workplaceRepository.findById(vehicleDto.getWorkplaceId());
        v.setWorkplace(workplace.get());
        v.setLastKnownServiceDate(vehicleDto.getLastKnownServiceDate());
        List<WorkOrder> workOrders = workOrderRepository.findByVehicleId(vehicleDto.getId());
        v.setWorkOrders(workOrders);
        v.setRegistrationNumber(vehicleDto.getRegistrationNumber());

        return v;

    }
}
