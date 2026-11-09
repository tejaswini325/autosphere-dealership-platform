package com.autosphere.repository;
import com.autosphere.model.DealerContent;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface DealerContentRepository extends JpaRepository<DealerContent,Long>{List<DealerContent> findByTypeAndActiveTrue(String type);}
