package com.example.startlight.constellation.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "star_node")
public class StarNode {
    @Id
    private Long node_id;

    @ManyToOne
    @JoinColumn(name = "con_id")
    private Constellation constellation;

    @Column(nullable = false)
    private Integer x_star;

    @Column(nullable = false)
    private Integer y_star;
}
