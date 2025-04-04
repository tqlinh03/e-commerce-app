package com.linh.ecommerce.size;

import com.linh.ecommerce.inventory.Inventory;
import com.linh.ecommerce.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "sizes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_type_value", // Tên của ràng buộc unique
                columnNames = {"type_name", "value"} // Ràng buộc unique trên cặp cột type_name và value
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Size {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_name", nullable = false)
    private SizeType type;

    @Column(name = "value")
    private String value;

    @ManyToMany(mappedBy = "sizes", fetch = FetchType.LAZY)
    private List<Product> products;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories;
}
