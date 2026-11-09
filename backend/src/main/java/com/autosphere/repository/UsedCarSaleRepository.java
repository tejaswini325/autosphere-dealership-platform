package com.autosphere.repository;
import com.autosphere.model.UsedCarSale;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface UsedCarSaleRepository extends JpaRepository<UsedCarSale,Long>{List<UsedCarSale> findByModelIgnoreCaseAndYearBetweenAndKilometresBetween(String model,int minYear,int maxYear,long minKm,long maxKm);}
