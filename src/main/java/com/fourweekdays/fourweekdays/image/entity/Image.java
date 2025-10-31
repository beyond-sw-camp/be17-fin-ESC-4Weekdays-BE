package com.fourweekdays.fourweekdays.image.entity;

import com.fourweekdays.fourweekdays.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
