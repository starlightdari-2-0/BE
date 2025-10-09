package com.example.startlight.constellation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "star_node")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class StarNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long star_node_id;

    @Column(nullable = false)
    private Long node_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "con_id", insertable = false, updatable = false)
    private Constellation constellation;

    @Column(nullable = false)
    private Integer x_star;

    @Column(nullable = false)
    private Integer y_star;
}
