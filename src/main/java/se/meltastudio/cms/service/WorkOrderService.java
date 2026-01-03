package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;


import se.meltastudio.cms.dto.*;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {


    private final WorkOrderRepository workOrderRepository;
    private final WorkTaskTemplateRepository workTaskTemplateRepository;
    private final WorkTaskRepository workTaskRepository;


    private final WorkOrderCategoryRepository workOrderCategoryRepository;
    private final VehicleRepository vehicleRepository;

    private final ArticleRepository articleRepository;
    private final PartMappingRepository partMappingRepository;
    private final WorkOrderStatusRepository workOrderStatusRepository;
    private final UserRepository userRepository;
    private final  PartOrderRepository partOrderRepository;
    private final RoleRepository roleRepository;
    private final SupplierRepository supplierRepository;


    public WorkOrderService(WorkOrderRepository workOrderRepository,
                            VehicleRepository vehicleRepository,
                            WorkTaskTemplateRepository workTaskTemplateRepository,
                            WorkTaskRepository workTaskRepository,
                            WorkOrderCategoryRepository workOrderCategoryRepository,
                            ArticleRepository articleRepository,
                            PartMappingRepository partMappingRepository,
                            WorkOrderStatusRepository workOrderStatusRepository,
                            UserRepository userRepository,
                            PartOrderRepository partOrderRepository,
                            RoleRepository roleRepository, SupplierRepository supplierRepository) {
        this.workOrderRepository = workOrderRepository;
        this.vehicleRepository = vehicleRepository;
        this.workTaskTemplateRepository = workTaskTemplateRepository;
        this.workTaskRepository = workTaskRepository;
        this.workOrderCategoryRepository = workOrderCategoryRepository;
        this.articleRepository = articleRepository;
        this.partMappingRepository = partMappingRepository;
        this.workOrderStatusRepository = workOrderStatusRepository;
        this.userRepository = userRepository;
        this.partOrderRepository = partOrderRepository;
        this.roleRepository = roleRepository;
        this.supplierRepository = supplierRepository;
    }
    public List<WorkOrder> getWorkOrdersByVehicle(Long vehicleId) {
        return workOrderRepository.findByVehicleId(vehicleId);
    }

    public Optional<WorkOrder> getWorkOrderById(long id) {
        return workOrderRepository.findById(id);
    }


    public List<WorkTask> generateServiceTasks(Vehicle vehicle, WorkOrderCategory category, WorkOrder workOrder, List<WorkTaskDTO> selectedTasks) {
        System.out.println("📌 [START] generateServiceTasks - Vehicle: " + vehicle.getRegistrationNumber() + ", Category: " + category.getName());
        System.out.println("📌 Valda arbetsmoment från frontend: " + (selectedTasks != null ? selectedTasks.size() : "null"));

        List<WorkTask> tasks = new ArrayList<>();

        List<WorkTaskTemplate> templates = workTaskTemplateRepository.findByCategory(category);

        for (WorkTaskTemplate template : templates) {
            System.out.println("🛠️ [INFO] Skapar WorkTask från mall: " + template.getDescription());

            WorkTask task = new WorkTask();
            task.setWorkOrder(workOrder);
            task.setDescription(template.getDescription());
            Long defaultWorkOrderStatus = 1L;
            task.setWorkOrderStatus(workOrderStatusRepository.getReferenceById(defaultWorkOrderStatus));
            task.setWorkTaskTemplate(template);

            // 🔥 Hämta rätt artikel baserat på bilmodell + arbetsmoment
            Optional<PartMapping> partMapping = partMappingRepository.findByVehicleModelAndWorkTaskTemplate(vehicle.getVehicleModel(), template);

            if (partMapping.isPresent()) {
                Article article = partMapping.get().getArticle();

                WorkTaskArticle workTaskArticle = new WorkTaskArticle();
                workTaskArticle.setWorkTask(task);
                workTaskArticle.setArticle(article);
                workTaskArticle.setQuantity(1); // Standardvärde, kan justeras

                task.setWorkTaskArticles(List.of(workTaskArticle));

                System.out.println("✅ [INFO] Kopplad artikel: " + article.getPartNumber() + " (" + article.getDescription() + ")");
            } else {
                System.out.println("⚠️ [WARNING] Ingen artikel funnen för arbetsmomentet och bilmodellen.");
            }

            System.out.println("sparar task " + task.toString());
            task = workTaskRepository.save(task);
            System.out.println("sparat färdigt task " + task.toString());
            tasks.add(task);
        }

        System.out.println("✅ [SUCCESS] generateServiceTasks skapade " + tasks.size() + " arbetsmoment.");
        return tasks;
    }




    public WorkOrder addWorkOrder(Long vehicleId, WorkOrderDTO workOrderDTO) {
        System.out.println("📌 [START] addWorkOrder - Vehicle ID: " + vehicleId);
        System.out.println("📌 Mottagen WorkOrderDTO: " + workOrderDTO);

        if (workOrderDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("❌ Arbetsorderkategori får inte vara null.");
        }
        WorkOrderCategory category = workOrderCategoryRepository.findById(workOrderDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("❌ Arbetsorderkategorin finns inte."));
        workOrderDTO.setCategoryId(category.getId());

        // Hämta standardstatus om den inte är satt
        if (workOrderDTO.getWorkOrderStatus() == null) {
            Long PENDING = 1L;
            WorkOrderStatus defaultStatus = workOrderStatusRepository.findById(PENDING)
                    .orElseThrow(() -> new RuntimeException("❌ Standardstatus saknas i databasen"));
            WorkOrderStatusDTO wosDTO = new WorkOrderStatusDTO();
            wosDTO.setId(defaultStatus.getId());
            wosDTO.setName(defaultStatus.getName());
            workOrderDTO.setWorkOrderStatus(wosDTO);
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("❌ Vehicle not found"));
        workOrderDTO.setVehicleId(vehicle.getId());
        workOrderDTO.setCreatedDate(LocalDate.now());

        System.out.println("✅ [INFO] Fordon och kategori hämtad: " + vehicle.getRegistrationNumber() + ", kategori: " + category.getName());

        // Konvertera DTO till entity
        WorkOrder workOrder = toWorkOrder(workOrderDTO);

        // ✅ SPARA FÖRST workOrder så att det får ett ID
        workOrder = workOrderRepository.save(workOrder);
        System.out.println("✅ [INFO] WorkOrder sparad med ID: " + workOrder.getId());

        List<WorkTask> tasks = new ArrayList<>();

        if (category.getName().equalsIgnoreCase("Service")) {
            System.out.println("🔄 [INFO] Skapar service-uppgifter för arbetsorder...");
            // Skicka med de valda arbetsmomenten från frontend
            tasks.addAll(generateServiceTasks(vehicle, category, workOrder, workOrderDTO.getWorkTasks()));
        } else {
            System.out.println("🔄 [INFO] Skapar reparationsmoment...");
            if (workOrderDTO.getWorkTasks() != null && !workOrderDTO.getWorkTasks().isEmpty()) {
                for (WorkTaskDTO taskDTO : workOrderDTO.getWorkTasks()) {
                    System.out.println("🛠️ [INFO] Lägger till WorkTask: " + taskDTO.getDescription());

                    WorkTask workTask = new WorkTask();
                    workTask.setWorkOrder(workOrder);

                    if (taskDTO.getTemplateId() != null) {
                        WorkTaskTemplate template = workTaskTemplateRepository.findById(taskDTO.getTemplateId())
                                .orElseThrow(() -> new RuntimeException("❌ Work task template not found"));
                        workTask.setWorkTaskTemplate(template);
                        workTask.setDescription(template.getDescription());
                    } else {
                        workTask.setDescription(taskDTO.getDescription());
                    }

                    workTask = workTaskRepository.save(workTask);
                    tasks.add(workTask);
                    System.out.println("✅ [INFO] WorkTask sparad med ID: " + workTask.getId());
                }
            } else {
                System.out.println("⚠️ [WARNING] Inga arbetsmoment hittades i DTO.");
            }
        }

        workOrder.setWorkTasks(tasks);
        System.out.println("✅ [SUCCESS] WorkOrder skapad med " + tasks.size() + " arbetsmoment.");

        return workOrder;
    }





    public List<WorkOrderCategory> getAllCategories() {
        return workOrderCategoryRepository.findAll();
    }



    public WorkOrder updateWorkOrder(Long id, WorkOrderDTO updatedWorkOrder) {
        return workOrderRepository.findById(id)
                .map(workOrder -> {
                    workOrder.setDescription(updatedWorkOrder.getDescription());
                    Optional<WorkOrderStatus> status = workOrderStatusRepository.findById(updatedWorkOrder.getWorkOrderStatus().getId());
                    if(!status.isPresent())
                        throw new RuntimeException("Workorder not found");

                    workOrder.setStatus(status.get());
                    return workOrderRepository.save(workOrder);
                }).orElseThrow(() -> new RuntimeException("WorkOrder not found"));
    }

    public void deleteWorkOrder(Long id) {
        workOrderRepository.deleteById(id);
    }

    public WorkOrder updateWorkOrderStatus(Long workOrderId, Long statusId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("WorkOrder not found"));

        WorkOrderStatus newStatus = workOrderStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        workOrder.setStatus(newStatus);
        return workOrderRepository.save(workOrder);
    }


    public List<WorkOrder> getWorkOrdersByWorkplace(Long workplaceId, WorkOrderStatus filterStatus, boolean sortByLatest,
                                                    Integer page, Integer pageSize,
                                                    LocalDate startDate, LocalDate endDate,
                                                    Long mechanicId
    ) {
        List<WorkOrder> workOrders;

        if (mechanicId != null) {
            workOrders = workOrderRepository.findByWorkplaceIdAndMechanic(workplaceId, mechanicId);
        } else {
            workOrders = workOrderRepository.findByWorkplaceId(workplaceId);
        }
        // Filtrera efter status om specificerat
        if (filterStatus != null) {
            workOrders = workOrders.stream()
                    .filter(wo -> wo.getStatus() == filterStatus)
                    .collect(Collectors.toList());
        }

        // Filtrera efter datumintervall om specificerat
        if (startDate != null) {
            workOrders = workOrders.stream()
                    .filter(wo -> !wo.getCreatedDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            workOrders = workOrders.stream()
                    .filter(wo -> !wo.getCreatedDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }


        // Sortera efter senaste arbetsorder om specificerat
        if (sortByLatest) {
            workOrders.sort(Comparator.comparing(WorkOrder::getCreatedDate, Comparator.reverseOrder()));
        }

        // Paginera resultaten om page och pageSize är angivna
        if (page != null && pageSize != null && page >= 0 && pageSize > 0) {
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, workOrders.size());
            if (fromIndex < workOrders.size()) {
                workOrders = workOrders.subList(fromIndex, toIndex);
            } else {
                workOrders = Collections.emptyList(); // Returnera tom lista om sidnumret är utanför räckvidd
            }
        }

        return workOrders;
    }

    public List<WorkOrder> getWorkOrdersByMechanic(Long mechanicId, WorkOrderStatus filterStatus, boolean sortByLatest, Integer page, Integer pageSize, LocalDate startDate, LocalDate endDate) {
        List<WorkOrder> workOrders = workOrderRepository.findByMechanicId(mechanicId);

        // Filtrera efter status om specificerat
        if (filterStatus != null) {
            workOrders = workOrders.stream()
                    .filter(wo -> wo.getStatus() == filterStatus)
                    .collect(Collectors.toList());
        }

        // Filtrera efter datumintervall om specificerat
        if (startDate != null) {
            workOrders = workOrders.stream()
                    .filter(wo -> !wo.getCreatedDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }
        if (endDate != null) {
            workOrders = workOrders.stream()
                    .filter(wo -> !wo.getCreatedDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        // Sortera efter senaste arbetsorder om specificerat
        if (sortByLatest) {
            workOrders.sort(Comparator.comparing(WorkOrder::getCreatedDate, Comparator.reverseOrder()));
        }

        // Paginera resultaten om page och pageSize är angivna
        if (page != null && pageSize != null && page >= 0 && pageSize > 0) {
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, workOrders.size());
            if (fromIndex < workOrders.size()) {
                workOrders = workOrders.subList(fromIndex, toIndex);
            } else {
                workOrders = Collections.emptyList(); // Returnera tom lista om sidnumret är utanför räckvidd
            }
        }

        return workOrders;
    }

    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    public List<WorkTask> generateTasksForWorkOrder(Long workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Arbetsorder hittades inte"));

        WorkOrderCategory category = workOrder.getCategory();
        List<WorkTaskTemplate> taskTemplates = workTaskTemplateRepository.findByCategory(category);

        List<WorkTask> tasks = new ArrayList<>();

        for (WorkTaskTemplate template : taskTemplates) {
            WorkTask task = new WorkTask();
            task.setWorkOrder(workOrder);
            task.setDescription(template.getDescription());
            task.setWorkOrderStatus(workOrder.getStatus());
            task.setWorkTaskTemplate(template);

            // Spara först WorkTask för att få ID
            WorkTask savedTask = workTaskRepository.save(task);

            // 🔥 Hämta artiklar baserat på WorkTaskTemplate + VehicleModel
            List<PartMapping> partMappings = partMappingRepository.findByWorkTaskTemplateAndVehicleModel(template, workOrder.getVehicle().getVehicleModel());

            List<WorkTaskArticle> workTaskArticles = new ArrayList<>();
            for (PartMapping mapping : partMappings) {
                WorkTaskArticle workTaskArticle = new WorkTaskArticle();
                workTaskArticle.setWorkTask(savedTask);
                workTaskArticle.setArticle(mapping.getArticle());
                workTaskArticle.setQuantity(mapping.getQuantity());
                workTaskArticles.add(workTaskArticle);
            }

            savedTask.setWorkTaskArticles(workTaskArticles);
            workTaskRepository.save(savedTask); // 🔄 Spara igen med artiklarna kopplade
            tasks.add(savedTask);
        }

        return tasks;
    }






    private WorkTask createTask(WorkOrder workOrder, String description, List<String> partNumbers) {
        Long PENDING = 1L;
        WorkTask task = new WorkTask();
        task.setWorkOrder(workOrder);
        task.setDescription(description);

        // Hämta PENDING-status från databasen istället för att använda en enum
        WorkOrderStatus pendingStatus = workOrderStatusRepository.findById(PENDING)
                .orElseThrow(() -> new RuntimeException("Status 'PENDING' saknas i databasen"));
        task.setWorkOrderStatus(pendingStatus);

        // 🔥 Hämta faktiska artiklar baserat på partNumber
        List<Article> articles = articleRepository.findByPartNumberIn(partNumbers);

        if (articles.size() != partNumbers.size()) {
            throw new RuntimeException("Vissa artiklar kunde inte hittas i databasen.");
        }

        // 🔥 Koppla artiklarna till arbetsmomentet
        List<WorkTaskArticle> workTaskArticles = articles.stream().map(article -> {
            WorkTaskArticle workTaskArticle = new WorkTaskArticle();
            workTaskArticle.setWorkTask(task);
            workTaskArticle.setArticle(article);
            workTaskArticle.setQuantity(1); // Standardvärde, kan justeras
            return workTaskArticle;
        }).toList();

        task.setWorkTaskArticles(workTaskArticles);
        return task;
    }


    public List<WorkOrderStatusDTO> getAllWorkOrderStatuses() {
        List<WorkOrderStatus> wo = workOrderStatusRepository.findAll();
        List<WorkOrderStatusDTO> dtos = new ArrayList<>();
        for(int i = 0; i< wo.size();i++) {
            WorkOrderStatus w = wo.get(i);
            WorkOrderStatusDTO dto = new WorkOrderStatusDTO();
            dto.setId(w.getId());
            dto.setName(w.getName());
            dtos.add(dto);
        }
        return dtos;
    }

    public Optional<WorkOrder> getWorkOrderById(Long id) {
        System.out.println("🔍 WorkOrder utan detaljer -för debug: " + id);
        return workOrderRepository.findById(id);
    }

    public WorkOrderDTO getDetailedWorkOrderById(Long id) {

        Optional<WorkOrder> workOrder = workOrderRepository.findByIdWithDetails(id);

        workOrder.ifPresent(w -> {
            System.out.println("🔍 WorkOrder-för debug: " + w);
            System.out.println("🚗 Vehicle: " + w.getVehicle());
        });

        WorkOrderDTO dto = toDTO(workOrder.get());
        return dto;
    }

    public WorkOrderDTO assignMechanic(Long workOrderId, Long mechanicId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("WorkOrder not found"));

        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));

        workOrder.setMechanic(mechanic);
        return toDTO(workOrderRepository.save(workOrder));
    }

    public List<User> getMechanicsForWorkOrder(Long workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("WorkOrder not found"));

        Long companyId = workOrder.getVehicle().getCompany().getId();

        // ✅ Hämta Role-entiteten från databasen istället för att använda Role.MECHANIC
        Role mechanicRole = roleRepository.findByName("MECHANIC")
                .orElseThrow(() -> new RuntimeException("Role MECHANIC not found"));

        return userRepository.findByCompanyIdAndRole(companyId, mechanicRole);
    }


    public PartOrderDTO orderPart(Long workOrderId, PartOrderRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("WorkOrder not found"));


        Optional<Article> article = articleRepository.findById(request.getArticleId());
        Optional<Supplier> supplier = supplierRepository.findById(request.getSupplierId());
        if(article.isPresent() && article.get().getId().equals(request.getArticleId())) {
            PartOrder partOrder = new PartOrder();
            partOrder.setWorkOrder(workOrder);
            partOrder.setArticle(article.get());
            partOrder.setQuantity(request.getQuantity());
            partOrder.setSupplier(supplier.get());
            partOrder.setOrderDate(LocalDateTime.now());
            return toPartOrderDTO(partOrderRepository.save(partOrder));
        } else {
            return null;
        }



    }

    public PartOrderDTO toPartOrderDTO(PartOrder partOrder) {
        PartOrderDTO dto = new PartOrderDTO();
        dto.setArticleId(partOrder.getArticle().getId());
        dto.setWorkOrderId(partOrder.getWorkOrder().getId());
        dto.setExpectedArrivalDate(partOrder.getExpectedArrivalDate());
        dto.setPurchasePrice(partOrder.getPurchasePrice());
        dto.setSellingPrice(partOrder.getSellingPrice());
        return dto;

    }


    public WorkOrderDTO toDTO(WorkOrder entity) {
        WorkOrderDTO dto = new WorkOrderDTO();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDate(entity.getCreatedDate());

        if (entity.getMechanic() != null) {
            dto.setMechanicId(entity.getMechanic().getId());
        }
        if (entity.getStatus() != null) {
            WorkOrderStatusDTO workOrderStatusDTO = new WorkOrderStatusDTO();
            workOrderStatusDTO.setName(entity.getStatus().getName());
            workOrderStatusDTO.setId(entity.getStatus().getId());
            dto.setWorkOrderStatus(workOrderStatusDTO);
        }
        if (entity.getVehicle() != null) {
            dto.setVehicleId(entity.getVehicle().getId());
            VehicleDTO vehicleDTO = new VehicleDTO();
            vehicleDTO.setModelName(entity.getVehicle().getVehicleModel().getModel());
            vehicleDTO.setId(entity.getVehicle().getId());
            vehicleDTO.setBrand(entity.getVehicle().getVehicleModel().getBrand());
            vehicleDTO.setVehicleModelId(entity.getVehicle().getVehicleModel().getId());
            vehicleDTO.setRegistrationNumber(entity.getVehicle().getRegistrationNumber());
            vehicleDTO.setMileage(entity.getVehicle().getMileage());
            vehicleDTO.setEndCustomerId(entity.getVehicle().getEndCustomer().getId());
            vehicleDTO.setTransmission(entity.getVehicle().getTransmission());
            vehicleDTO.setWorkplaceId(entity.getVehicle().getWorkplace().getId());
            vehicleDTO.setYear(entity.getVehicle().getVehicleModel().getYear());
            vehicleDTO.setLastKnownService(entity.getVehicle().getLastKnownService());
            vehicleDTO.setLastKnownServiceDate(entity.getVehicle().getLastKnownServiceDate());
            vehicleDTO.setCompanyId(entity.getVehicle().getCompany().getId());


            List<WorkOrder> workOrders = entity.getVehicle().getWorkOrders();
            List<Long> wIds = new ArrayList<>();
            for(int i = 0; i<workOrders.size();i++) {
                wIds.add(workOrders.get(i).getId());

            }
            vehicleDTO.setWorkOrderIds(wIds);
            dto.setVehicle(vehicleDTO);

        }
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
        }

        // Mappa partOrders
        if (entity.getPartOrders() != null) {
            List<Long> partOrderIds = new ArrayList<>();
            for (PartOrder po : entity.getPartOrders()) {
                partOrderIds.add(po.getId());
            }
            dto.setPartOrderIds(partOrderIds);
        }

        // Lägg till WorkTask
        List<WorkTaskDTO> taskDTOs = new ArrayList<>();
        if (entity.getWorkTasks() != null) {
            for (WorkTask t : entity.getWorkTasks()) {
                WorkTaskDTO tdto = new WorkTaskDTO();
                tdto.setWorkOrderId(entity.getId());
                tdto.setId(t.getId());
                tdto.setDescription(t.getDescription());
                if (t.getWorkOrderStatus() != null) {
                    tdto.setStatusId(t.getWorkOrderStatus().getId());
                    tdto.setStatusName(t.getWorkOrderStatus().getName());
                }
                // Kopiera requiredParts om du vill
                // ...
                taskDTOs.add(tdto);
            }
        }
        dto.setWorkTasks(taskDTOs);

        if(entity.getMechanic() != null) {
            dto.setMechanicId(entity.getMechanic().getId());
            dto.setMechanicName(entity.getMechanic().getUsername());
        }

        return dto;

    }



    public WorkOrder toWorkOrder(WorkOrderDTO dto) {
        // Hämta existerande eller skapa en ny entitet
        WorkOrder entity = new WorkOrder();
        // Om du ska uppdatera existerande, hämta den från DB via repository

        entity.setDescription(dto.getDescription());
        entity.setCreatedDate(dto.getCreatedDate());

        // Hämtar relaterade entiteter via Repository om ID:et inte är null
        if (dto.getMechanicId() != null) {
            User mechanic = userRepository.findById(dto.getMechanicId())
                    .orElseThrow(() -> new RuntimeException("Mechanic not found"));
            entity.setMechanic(mechanic);
        }
        if (dto.getWorkOrderStatus() != null) {
            WorkOrderStatus status = workOrderStatusRepository.findById(dto.getWorkOrderStatus().getId())
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            entity.setStatus(status);
        }
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            entity.setVehicle(vehicle);
        }
        if (dto.getCategoryId() != null) {
            WorkOrderCategory category = workOrderCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            entity.setCategory(category);
        }

        // PartOrders är oftast nya eller uppdaterade. Exempel:
        if (dto.getPartOrderIds() != null) {
            List<PartOrder> partOrders = new ArrayList<>();
            for (Long partOrderId : dto.getPartOrderIds()) {
                PartOrder partOrder = partOrderRepository.findById(partOrderId)
                        .orElseThrow(() -> new RuntimeException("PartOrder not found"));
                partOrder.setWorkOrder(entity);
                partOrders.add(partOrder);
            }
            entity.setPartOrders(partOrders);
        }

        return entity;
    }

    public List<WorkOrderDTO> findAll() {
        List<WorkOrder> wos = workOrderRepository.findAll();
        List<WorkOrderDTO> workOrderDTOS = new ArrayList<>();
        for(int i = 0; i < wos.size(); i++) {
            WorkOrderDTO workOrderDTO = toDTO(wos.get(i));
            workOrderDTOS.add(workOrderDTO);
        }
        return workOrderDTOS;
    }
}
