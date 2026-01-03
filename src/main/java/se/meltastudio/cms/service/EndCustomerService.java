package se.meltastudio.cms.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.EndCustomerDTO;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EndCustomerService {

    private final EndCustomerRepository endCustomerRepository;
    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;
    private final WorkOrderRepository workOrderRepository;
    private final InvoiceRepository invoiceRepository;

    public EndCustomerService(
            CompanyRepository companyRepository,
            EndCustomerRepository endCustomerRepository,
            VehicleRepository vehicleRepository,
            WorkOrderRepository workOrderRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.companyRepository = companyRepository;
        this.endCustomerRepository = endCustomerRepository;
        this.vehicleRepository = vehicleRepository;
        this.workOrderRepository = workOrderRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // ---------------------------------------------------------------------
    // 1. Metoder som returnerar EndCustomerDTO istället för EndCustomer
    // ---------------------------------------------------------------------

    /**
     * Hämtar alla slutkunder för ett visst companyId och returnerar en lista av EndCustomerDTO.
     */
    public List<EndCustomerDTO> getEndCustomersDTOByCompany(Long companyId) {
        // Hämta entiteterna
        List<EndCustomer> entityList = endCustomerRepository.findByCompanyId(companyId);
        // Mappa dem till DTO
        return toDTOList(entityList);
    }

    /**
     * Hämtar en enskild slutkund som DTO, kontrollerar att den hör till rätt företag.
     */
    public EndCustomerDTO getEndCustomerDTOById(Long endCustomerId, Long companyId) {
        EndCustomer endCustomer = endCustomerRepository.findByIdAndCompanyId(endCustomerId, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EndCustomer not found or unauthorized"));

        return toDTO(endCustomer);
    }

    /**
     * Skapar en ny slutkund (utifrån en EndCustomerDTO) och returnerar slutresultatet som DTO.
     */
    public EndCustomerDTO addEndCustomerDTO(Long companyId, EndCustomerDTO dto) {
        // Mappa DTO -> entitet
        EndCustomer entity = toEntity(dto);

        // Koppla till Company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id=" + companyId));
        entity.setCompany(company);

        // Spara entiteten
        EndCustomer saved = endCustomerRepository.save(entity);
        // Returnera den sparade kundens DTO
        return toDTO(saved);
    }

    /**
     * Uppdaterar en slutkund (utifrån en EndCustomerDTO) och returnerar en EndCustomerDTO.
     */
    public EndCustomerDTO updateEndCustomerDTO(Long id, EndCustomerDTO dto) {
        return endCustomerRepository.findById(id)
                .map(entity -> {
                    entity.setName(dto.getName());
                    entity.setEmail(dto.getEmail());
                    entity.setPhone(dto.getPhone());
                    entity.setBillingStreet(dto.getBillingStreet());
                    entity.setBillingCity(dto.getBillingCity());
                    entity.setBillingZip(dto.getBillingZip());
                    entity.setBillingCountry(dto.getBillingCountry());

                    // Om companyId satts i DTO kan du valfritt hantera det
                    if (dto.getCompanyId() != null) {
                        Company c = companyRepository.findById(dto.getCompanyId())
                                .orElseThrow(() -> new RuntimeException("Company not found"));
                        entity.setCompany(c);
                    }

                    // Spara
                    EndCustomer updated = endCustomerRepository.save(entity);
                    return toDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("EndCustomer not found with id=" + id));
    }

    /**
     * Raderar en slutkund.
     */
    public void deleteEndCustomer(Long id) {
        endCustomerRepository.deleteById(id);
    }

    // ---------------------------------------------------------------------
    // 2. Interna metoder som hanterar entiteter men inte exponeras i controllers
    //    (ex. fordon, fakturor, etc. som entiteter).
    //    Vill du också returnera/uppdatera dem via DTO bör du skriva liknande
    //    mappermetoder för Vehicle, Invoice, mm.
    // ---------------------------------------------------------------------

    public Vehicle addVehicleToEndCustomer(Long endCustomerId, Vehicle vehicle) {
        vehicle.setEndCustomer(new EndCustomer(endCustomerId));
        return vehicleRepository.save(vehicle);
    }

    // Exempel: hämta fordon (som entiteter internt)
    public List<Vehicle> getVehiclesByEndCustomer(Long endCustomerId) {
        return vehicleRepository.findByEndCustomerId(endCustomerId);
    }

    // ---------------------------------------------------------------------
    // 3. Mapper: Entity <-> DTO
    // ---------------------------------------------------------------------

    /**
     * Konverterar en EndCustomer‑entitet till dess motsvarande EndCustomerDTO.
     */
    public EndCustomerDTO toDTO(EndCustomer entity) {
        if (entity == null) return null;

        EndCustomerDTO dto = new EndCustomerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setBillingStreet(entity.getBillingStreet());
        dto.setBillingCity(entity.getBillingCity());
        dto.setBillingZip(entity.getBillingZip());
        dto.setBillingCountry(entity.getBillingCountry());
        dto.setIsActive(entity.getIsActive());
        dto.setArchivedDate(entity.getArchivedDate());

        if (entity.getCompany() != null) {
            dto.setCompanyId(entity.getCompany().getId());
        }

        // Lägg in en lista av fordonens ID
        if (entity.getVehicles() != null) {
            List<Long> vIds = new ArrayList<>();
            entity.getVehicles().forEach(v -> vIds.add(v.getId()));
            dto.setVehicleIds(vIds);
        }

        return dto;
    }

    /**
     * Konverterar en EndCustomerDTO till en EndCustomer‑entitet.
     * Observera att det inte hämtar fordon om du inte explicit vill.
     */
    public EndCustomer toEntity(EndCustomerDTO dto) {
        if (dto == null) return null;

        EndCustomer entity = new EndCustomer();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setBillingStreet(dto.getBillingStreet());
        entity.setBillingCity(dto.getBillingCity());
        entity.setBillingZip(dto.getBillingZip());
        entity.setBillingCountry(dto.getBillingCountry());
        // Company kan sättas av anropare eller i addEndCustomerDTO

        // Om du vill kopiera fordon får du antingen:
        //   - hämta dem från DB och setVehicles()
        //   - eller helt låta dem vara. Ofta uppdaterar man fordon i en separat endpoint

        return entity;
    }

    /**
     * Konverterar en lista av entiteter till en lista av DTO:er.
     */
    public List<EndCustomerDTO> toDTOList(List<EndCustomer> entities) {
        List<EndCustomerDTO> dtos = new ArrayList<>();
        for (EndCustomer e : entities) {
            dtos.add(toDTO(e));
        }
        return dtos;
    }

    public List<Invoice> getInvoicesByEndCustomer(Long id) {
        return invoiceRepository.findByEndCustomerId(id);
    }

    public List<WorkOrder> getWorkOrdersByEndCustomer(Long id) {
        return workOrderRepository.findByEndCustomerId(id);
    }

    public List<EndCustomer> getEndCustomersByCompany(Long companyId) {
        return endCustomerRepository.findByCompanyId(companyId);
    }

    public EndCustomer getEndCustomerById(Long id, Long companyId) {
        List<EndCustomer> companyEndCustomers = endCustomerRepository.findByCompanyId(companyId);
        for(int i = 0 ; i < companyEndCustomers.size(); i++) {
            if(id.equals(companyEndCustomers.get(i).getId()))
                return companyEndCustomers.get(i);
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // 4. Archive/Unarchive functionality (Soft Delete)
    // ---------------------------------------------------------------------

    /**
     * Archive an end customer (soft delete).
     * Sets isActive = false and archivedDate = now.
     */
    public EndCustomerDTO archiveEndCustomer(Long id, Long companyId) {
        EndCustomer endCustomer = endCustomerRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EndCustomer not found or unauthorized"));

        endCustomer.setIsActive(false);
        endCustomer.setArchivedDate(LocalDateTime.now());

        EndCustomer saved = endCustomerRepository.save(endCustomer);
        return toDTO(saved);
    }

    /**
     * Unarchive an end customer (restore from soft delete).
     * Sets isActive = true and archivedDate = null.
     */
    public EndCustomerDTO unarchiveEndCustomer(Long id, Long companyId) {
        EndCustomer endCustomer = endCustomerRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EndCustomer not found or unauthorized"));

        endCustomer.setIsActive(true);
        endCustomer.setArchivedDate(null);

        EndCustomer saved = endCustomerRepository.save(endCustomer);
        return toDTO(saved);
    }

    /**
     * Get only active (non-archived) end customers for a company.
     */
    public List<EndCustomer> getActiveEndCustomers(Long companyId) {
        return endCustomerRepository.findByCompanyIdAndIsActive(companyId, true);
    }

    /**
     * Get all end customers including archived ones (for admin views).
     */
    public List<EndCustomer> getAllEndCustomersIncludingArchived(Long companyId) {
        return endCustomerRepository.findAllByCompanyIdIncludingArchived(companyId);
    }
}
