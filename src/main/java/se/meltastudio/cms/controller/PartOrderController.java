package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.PartOrderRequest;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.dto.PartOrderDTO;
import se.meltastudio.cms.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/part-orders")
public class PartOrderController {

    private final PartOrderRepository partOrderRepository;
    private final WorkOrderRepository workOrderRepository;

    private final SupplierRepository supplierRepository;
    private final WorkOrderStatusRepository workOrderStatusRepository;
    private  final ArticleRepository articleRepository;

    public PartOrderController(PartOrderRepository partOrderRepository,
                               WorkOrderRepository workOrderRepository,
                               SupplierRepository supplierRepository,
                               WorkOrderStatusRepository workOrderStatusRepository, ArticleRepository articleRepository) {
        this.partOrderRepository = partOrderRepository;
        this.workOrderRepository = workOrderRepository;
        this.supplierRepository = supplierRepository;
        this.articleRepository = articleRepository;
        this.workOrderStatusRepository = workOrderStatusRepository;
    }

    @PostMapping
    public ResponseEntity<PartOrder> createPartOrder(@RequestBody PartOrderDTO dto) {
        WorkOrder workOrder = workOrderRepository.findById(dto.getWorkOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work order not found"));

        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));


        PartOrder partOrder = new PartOrder();
        partOrder.setWorkOrder(workOrder);
        partOrder.setArticle(article);
        partOrder.setQuantity(dto.getQuantity());

        partOrder.setOrderDate(LocalDateTime.now());
        partOrder.setExpectedArrivalDate(dto.getExpectedArrivalDate());
        partOrder.setPurchasePrice(dto.getPurchasePrice()); // Sätt inköpspris per del
        partOrder.setSellingPrice(dto.getSellingPrice());   // Sätt försäljningspris per del

        PartOrder savedOrder = partOrderRepository.save(partOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }


    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<PartOrder>> getPartOrdersByWorkOrder(@PathVariable Long workOrderId) {
        List<PartOrder> partOrders = partOrderRepository.findByWorkOrderId(workOrderId);
        return ResponseEntity.ok(partOrders);
    }

    @PutMapping("/{partOrderId}/receive")
    public ResponseEntity<PartOrder> markPartAsReceived(@PathVariable Long partOrderId) {
        PartOrder partOrder = partOrderRepository.findById(partOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part order not found"));

        partOrder.setReceived(true);
        partOrderRepository.save(partOrder);

        // Kontrollera om alla delar är mottagna och uppdatera arbetsorder-status
        WorkOrder workOrder = partOrder.getWorkOrder();
        boolean allPartsReceived = workOrder.getPartOrders().stream().allMatch(PartOrder::isReceived);

        if (allPartsReceived) {
            Long IN_PROGRESS = 5L; // Identifieraren för "IN_PROGRESS" i databasen
            Optional<WorkOrderStatus> status = workOrderStatusRepository.findById(IN_PROGRESS);

            if (status.isPresent()) {
                workOrder.setStatus(status.get());
                workOrderRepository.save(workOrder);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Work order status not found");
            }
        }

        return ResponseEntity.ok(partOrder);
    }


    @PostMapping("/order")
    public ResponseEntity<PartOrder> orderPart(@RequestBody PartOrderRequest request) {
        System.out.println("📦 Inkommande beställning: " + request.toString());

        if (request.getArticleId() == null || request.getSupplierId() == null) {
            return ResponseEntity.badRequest().build(); // 400 om något saknas
        }

        System.out.println("🔍 Hämtar supplier med ID: " + request.getSupplierId());
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Leverantör hittades inte!"));

        System.out.println("🔍 Hämtar artikel med ID: " + request.getArticleId());
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("Artikel hittades inte!"));

        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new IllegalArgumentException("WorkOrder hittades inte!"));

        PartOrder partOrder = new PartOrder();
        partOrder.setArticle(article);
        partOrder.setSupplier(supplier);
        partOrder.setQuantity(request.getQuantity());
        partOrder.setOrderDate(LocalDateTime.now());
        partOrder.setWorkOrder(workOrder);
        partOrder.setPurchasePrice(article.getPurchasePrice());
        partOrder.setSellingPrice(article.getSellingPrice());

        PartOrder savedOrder = partOrderRepository.save(partOrder);

        return ResponseEntity.ok(savedOrder);
    }


}
