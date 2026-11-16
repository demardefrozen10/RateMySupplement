package ratemysupps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ratemysupps.entity.Brand;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrandName(String brandName);

    List<Brand> findByBrandNameContainingIgnoreCase(String name);

    List<Brand> findByIsVerifiedFalse();
    
    @Query("SELECT COUNT(r) FROM review r WHERE r.supplement.brand.id = :brandId")
    int countReviewsByBrandId(Long brandId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM review r WHERE r.supplement.brand.id = :brandId")
    double averageRatingByBrandId(Long brandId);


}
