package car.sharing.model.car;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE cars SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = FALSE")
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar",
            name = "car_type", nullable = false)
    private CarType type;

    @Column(nullable = false)
    private Integer inventory;

    @Column(name = "daily_fee", nullable = false)
    private BigDecimal dailyFee;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
