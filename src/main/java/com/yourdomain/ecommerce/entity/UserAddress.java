package com.yourdomain.ecommerce.entity;

import com.yourdomain.ecommerce.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_addresses")
public class UserAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    @Column(name = "phone", nullable = false, length = 32)
    private String phone;

    @Column(name = "province", nullable = false, length = 128)
    private String province;

    @Column(name = "district", nullable = false, length = 128)
    private String district;

    @Column(name = "ward", nullable = false, length = 128)
    private String ward;

    @Column(name = "address_line", nullable = false, length = 512)
    private String addressLine;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean defaultAddress = false;
}
