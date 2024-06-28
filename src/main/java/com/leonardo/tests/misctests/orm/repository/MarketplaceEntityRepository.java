package com.leonardo.tests.misctests.orm.repository;

import com.leonardo.tests.misctests.orm.entity.MarketplaceEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceEntityRepository extends CrudRepository<MarketplaceEntity, Long> {
    Optional<MarketplaceEntity> findById(Long id);
}
