package se.meltastudio.cms.parts.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import se.meltastudio.cms.model.Company;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.model.Vehicle;
import se.meltastudio.cms.model.WorkOrder;
import se.meltastudio.cms.parts.domain.PartLineStatus;
import se.meltastudio.cms.parts.domain.WorkOrderPartLine;
import se.meltastudio.cms.parts.service.PartsService;
import se.meltastudio.cms.parts.service.PartsServiceException;
import se.meltastudio.cms.repository.UserRepository;
import se.meltastudio.cms.repository.WorkOrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PartsController.
 * Tests controller logic, error handling, and response mapping.
 */
@ExtendWith(MockitoExtension.class)
class PartsControllerTest {

    @Mock
    private PartsService partsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private PartsController controller;

    private Company company;
    private User user;
    private WorkOrder workOrder;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("Test Company");

        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setCompany(company);

        workOrder = new WorkOrder();
        workOrder.setId(100L);
        Vehicle vehicle = new Vehicle();
        se.meltastudio.cms.model.Workplace workplace = new se.meltastudio.cms.model.Workplace();
        workplace.setCompany(company);
        vehicle.setWorkplace(workplace);
        workOrder.setVehicle(vehicle);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void searchParts_ValidRequest_ShouldReturnResults() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(workOrderRepository.findById(100L)).thenReturn(Optional.of(workOrder));

        PartOffer offer = new PartOffer();
        offer.setSupplierPartId("PART-001");
        offer.setPartName("Brake Pad");
        offer.setUnitPriceExVat(new BigDecimal("450.00"));

        when(partsService.searchParts(any(), any(), any())).thenReturn(List.of(offer));

        // When
        ResponseEntity<?> response = controller.searchParts(100L, "brake", null, 20, userDetails);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    void searchParts_WorkOrderNotFound_ShouldReturn404() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(workOrderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = controller.searchParts(999L, "brake", null, 20, userDetails);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addPartLine_ValidRequest_ShouldCreatePartLine() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        AddPartLineRequest request = new AddPartLineRequest();
        request.setSupplierCode("MOCK_SUPPLIER");
        request.setSupplierPartId("PART-001");
        request.setQuantity(2);

        WorkOrderPartLine partLine = new WorkOrderPartLine();
        partLine.setId(1L);
        partLine.setSupplierPartId("PART-001");
        partLine.setPartName("Brake Pad");
        partLine.setQuantity(2);
        partLine.setUnitPriceExVat(new BigDecimal("450.00"));
        partLine.setStatus(PartLineStatus.PLANNED);

        when(partsService.addPartLine(eq(100L), any(), any())).thenReturn(partLine);

        // When
        ResponseEntity<?> response = controller.addPartLine(100L, request, userDetails);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PartLineResponse);
    }

    @Test
    void addPartLine_ServiceException_ShouldReturn400() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(partsService.addPartLine(anyLong(), any(), any()))
                .thenThrow(new PartsServiceException("Invalid request"));

        AddPartLineRequest request = new AddPartLineRequest();
        request.setSupplierCode("INVALID");
        request.setSupplierPartId("PART-999");
        request.setQuantity(1);

        // When
        ResponseEntity<?> response = controller.addPartLine(100L, request, userDetails);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getPartLines_ValidRequest_ShouldReturnList() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        WorkOrderPartLine partLine1 = new WorkOrderPartLine();
        partLine1.setId(1L);
        partLine1.setSupplierPartId("PART-001");
        partLine1.setPartName("Brake Pad");

        WorkOrderPartLine partLine2 = new WorkOrderPartLine();
        partLine2.setId(2L);
        partLine2.setSupplierPartId("PART-002");
        partLine2.setPartName("Oil Filter");

        when(partsService.getWorkOrderPartLines(100L, user)).thenReturn(List.of(partLine1, partLine2));

        // When
        ResponseEntity<?> response = controller.getPartLines(100L, userDetails);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);

        @SuppressWarnings("unchecked")
        List<PartLineResponse> partLines = (List<PartLineResponse>) response.getBody();
        assertEquals(2, partLines.size());
    }

    @Test
    void orderPartLine_ValidRequest_ShouldReturnUpdatedPartLine() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        WorkOrderPartLine partLine = new WorkOrderPartLine();
        partLine.setId(1L);
        partLine.setSupplierPartId("PART-001");
        partLine.setStatus(PartLineStatus.ORDERED);
        partLine.setSupplierOrderReference("ORD-12345");

        when(partsService.placeOrder(100L, 1L, user)).thenReturn(partLine);

        // When
        ResponseEntity<?> response = controller.orderPartLine(100L, 1L, userDetails);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof PartLineResponse);

        PartLineResponse responseBody = (PartLineResponse) response.getBody();
        assertEquals("ORDERED", responseBody.getStatus());
        assertEquals("ORD-12345", responseBody.getSupplierOrderReference());
    }

    @Test
    void orderPartLine_ServiceException_ShouldReturn400() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(partsService.placeOrder(anyLong(), anyLong(), any()))
                .thenThrow(new PartsServiceException("Ordering not enabled"));

        // When
        ResponseEntity<?> response = controller.orderPartLine(100L, 1L, userDetails);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cancelPartLine_ValidRequest_ShouldReturn204() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(partsService).cancelPartLine(100L, 1L, user);

        // When
        ResponseEntity<?> response = controller.cancelPartLine(100L, 1L, userDetails);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(partsService).cancelPartLine(100L, 1L, user);
    }

    @Test
    void cancelPartLine_ServiceException_ShouldReturn400() throws Exception {
        // Given
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        doThrow(new PartsServiceException("Part line not found"))
                .when(partsService).cancelPartLine(anyLong(), anyLong(), any());

        // When
        ResponseEntity<?> response = controller.cancelPartLine(100L, 1L, userDetails);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserFromAuth_UserNotFound_ShouldThrowException() throws Exception {
        // Given
        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
                controller.searchParts(100L, "brake", null, 20, userDetails)
        );
    }
}
