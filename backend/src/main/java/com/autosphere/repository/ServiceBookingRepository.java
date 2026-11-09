package com.autosphere.repository;
import com.autosphere.model.ServiceBooking;import org.springframework.data.jpa.repository.JpaRepository;import java.time.LocalDate;
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking,Long>{long countByWorkshopAndBookingDateAndSlotTimeAndStatusNot(String workshop,LocalDate date,String slot,String status);}
