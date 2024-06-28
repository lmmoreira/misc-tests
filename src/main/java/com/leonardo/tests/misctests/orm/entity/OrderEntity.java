package com.leonardo.tests.misctests.orm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ORDER")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(name = "OBSERVATION", length = 255, nullable = false)
  private String observation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SELLER")
  private SellerEntity seller;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CLIENT")
  private ClientEntity client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MARKETPLACE")
  private MarketplaceEntity marketplace;

  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS", nullable = false)
  private StatusEntityEnum status;

}
