package se.meltastudio.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.VehicleModel;
import se.meltastudio.cms.model.carservice.VehicleService;
import se.meltastudio.cms.repository.VehicleModelRepository;
import se.meltastudio.cms.repository.VehicleRepository;
import se.meltastudio.cms.repository.VehicleServiceRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service")
public class VehicleServiceController {

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleServiceRepository vehicleServiceRepository;
    @GetMapping("/{brand}/{model}/{year}")
    public ResponseEntity<?> getServiceIntervals(
            @PathVariable String brand,
            @PathVariable String model,
            @PathVariable int year) {

        Optional<VehicleModel> vehicleModelOpt = vehicleModelRepository.findByBrandAndModelAndYear(brand, model, year);

        if (vehicleModelOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Modell ej hittad");
        }

        VehicleModel vehicleModel = vehicleModelOpt.get();
        List<VehicleService> services = vehicleServiceRepository.findByVehicleModel(vehicleModel);

        // Omvandla listan så att varje objekt endast returnerar service-namnet
        List<Map<String, Object>> response = services.stream()
                .map(service -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", service.getId());
                    map.put("serviceName", service.getServiceName()); // Anropar den nya metoden i entity-klassen
                    map.put("startIntervalKm", service.getStartIntervalKm());
                    map.put("startIntervalTimeMonths", service.getStartIntervalTimeMonths());
                    map.put("intervalKm", service.getIntervalKm());
                    map.put("intervalTimeMonths", service.getIntervalTimeMonths());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }




    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Map<String, Object>>> getServiceForVehicle(@PathVariable Long vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Vehicle vehicle = vehicleOpt.get();
        VehicleModel model = vehicle.getVehicleModel();
        int currentMileage = vehicle.getMileage();
        Integer lastKnownService = vehicle.getLastKnownService();
        LocalDate lastServiceDate = vehicle.getLastKnownServiceDate();
        LocalDate today = LocalDate.now();

        int referenceMileage = (lastKnownService != null) ? lastKnownService : currentMileage;
        int maxLookahead = currentMileage + 50000;

        List<VehicleService> allServices = vehicleServiceRepository.findByVehicleModel(model);
        List<Map<String, Object>> upcomingServices = new ArrayList<>();

        for (VehicleService service : allServices) {
            int startInterval = service.getStartIntervalKm();
            int intervalKm = service.getIntervalKm();
            int intervalTimeMonths = service.getIntervalTimeMonths();
            int nextServiceKm = startInterval;

            // Logga ut initiala värden
            System.out.println("Service: " + service.getServiceType().getServiceName() +
                    ", startInterval: " + startInterval +
                    ", intervalKm: " + intervalKm);

            // Om ingen servicehistorik finns, börja räkna från start_interval
            if (lastKnownService == null && startInterval <= currentMileage) {
                while (nextServiceKm <= currentMileage) {
                    nextServiceKm += intervalKm;
                }
            } else if (lastKnownService != null) {
                while (nextServiceKm <= referenceMileage) {
                    nextServiceKm += intervalKm;
                }
            }

            System.out.println("Beräknat nextServiceKm: " + nextServiceKm + ", maxLookahead: " + maxLookahead);


            // Begränsa framtida servicepunkter
            if (nextServiceKm > maxLookahead) {
                continue;
            }

            // Kontrollera om tiden har överskridits
            boolean timeOverdue = false;
            if (lastServiceDate != null) {
                LocalDate expectedDate = lastServiceDate.plusMonths(intervalTimeMonths);
                timeOverdue = today.isAfter(expectedDate);
            }

            // Bestäm status (överskriden om endera tid eller mil har passerats)
            String status = (nextServiceKm <= currentMileage || timeOverdue) ? "Överskriden" : "Kommande";

            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put("serviceName", service.getVariation() != null ? service.getVariation().getVariation() : service.getServiceType().getServiceName());
            serviceMap.put("intervalKm", intervalKm);
            serviceMap.put("intervalTimeMonths", intervalTimeMonths);
            serviceMap.put("nextServiceKm", nextServiceKm);
            serviceMap.put("status", status);
            serviceMap.put("lastServiceDate", lastServiceDate);

            upcomingServices.add(serviceMap);
        }

        return ResponseEntity.ok(upcomingServices);
    }




   /* @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Map<String, Object>>> getServiceForVehicle(@PathVariable Long vehicleId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Vehicle vehicle = vehicleOpt.get();
        VehicleModel model = vehicle.getVehicleModel();
        int currentMileage = vehicle.getMileage();
        Integer lastKnownService = vehicle.getLastKnownService();
        int referenceMileage = (lastKnownService != null) ? lastKnownService : currentMileage;
        int maxLookahead = currentMileage + 50000;

        List<VehicleService> allServices = vehicleServiceRepository.findByVehicleModel(model);
        List<Map<String, Object>> overdueServices = new ArrayList<>();
        List<Map<String, Object>> upcomingServices = new ArrayList<>();

        System.out.println("=== DEBUG ===");
        System.out.println("Vehicle ID: " + vehicleId);
        System.out.println("Current Mileage: " + currentMileage);
        System.out.println("Last Known Service: " + (lastKnownService != null ? lastKnownService : "null"));
        System.out.println("Reference Mileage: " + referenceMileage);
        System.out.println("Max Lookahead: " + maxLookahead);

        for (VehicleService service : allServices) {
            int startInterval = service.getStartIntervalKm();
            int interval = service.getIntervalKm();
            int nextServiceKm = startInterval;
            int lastServiceKm = startInterval;

            System.out.println("Checking service: " + service.getServiceType().getServiceName());
            System.out.println("Start Interval: " + startInterval + ", Interval KM: " + interval);

            // Iterera genom serviceintervallen och hitta senaste överskridna service
            while (nextServiceKm <= referenceMileage) {
                lastServiceKm = nextServiceKm;
                nextServiceKm += interval;
            }

            System.out.println("Last completed service at: " + lastServiceKm);
            System.out.println("Next planned service at: " + nextServiceKm);

            // Om senaste servicen är överskriden, lägg till den
            if (lastServiceKm < currentMileage) {
                Map<String, Object> overdueService = new HashMap<>();
                overdueService.put("serviceName", service.getVariation() != null ? service.getVariation().getVariation() : service.getServiceType().getServiceName());
                overdueService.put("intervalKm", service.getIntervalKm());
                overdueService.put("intervalTid", service.getIntervalTid());
                overdueService.put("nextServiceKm", lastServiceKm);
                overdueService.put("status", "Överskriden");

                System.out.println("Added overdue service: " + service.getServiceType().getServiceName() + " at " + lastServiceKm);
                overdueServices.add(overdueService);
            }

            // Lägg till nästa planerade service om den är inom maxgränsen
            if (nextServiceKm > currentMileage && nextServiceKm <= maxLookahead) {
                Map<String, Object> upcomingService = new HashMap<>();
                upcomingService.put("serviceName", service.getVariation() != null ? service.getVariation().getVariation() : service.getServiceType().getServiceName());
                upcomingService.put("intervalKm", service.getIntervalKm());
                upcomingService.put("intervalTid", service.getIntervalTid());
                upcomingService.put("nextServiceKm", nextServiceKm);
                upcomingService.put("status", "Kommande");

                System.out.println("Added upcoming service: " + service.getServiceType().getServiceName() + " at " + nextServiceKm);
                upcomingServices.add(upcomingService);
            }
        }

        // Sortera överskridna servicar från äldst till nyast
        overdueServices.sort(Comparator.comparing(s -> (int) s.get("nextServiceKm")));

        // Sortera kommande servicar från närmast till längst bort
        upcomingServices.sort(Comparator.comparing(s -> (int) s.get("nextServiceKm")));

        List<Map<String, Object>> sortedServices = new ArrayList<>();
        sortedServices.addAll(overdueServices);
        sortedServices.addAll(upcomingServices);

        System.out.println("=== END DEBUG ===");

        return ResponseEntity.ok(sortedServices);
    }*/



    /**
     * Hämtar korrekt servicenamn, antingen från variation eller serviceType.
     */
    private String getServiceName(VehicleService service) {
        return service.getVariation() != null ? service.getVariation().getVariation() : service.getServiceType().getServiceName();
    }




}
