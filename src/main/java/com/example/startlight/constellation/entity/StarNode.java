package com.example.startlight.constellation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "star_node")
@IdClass(StarNodeId.class)
@AllArgsConstructor
@NoArgsConstructor
public class StarNode {
    @Id
    private Long con_id;

    @Id
    private Long node_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "con_id", insertable = false, updatable = false)
    private Constellation constellation;

    @Column(nullable = false)
    private Integer x_star;

    @Column(nullable = false)
    private Integer y_star;
}
