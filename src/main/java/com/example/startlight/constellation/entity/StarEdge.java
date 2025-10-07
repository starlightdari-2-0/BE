package com.example.startlight.constellation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "star_edge")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StarEdge {
    @Id
    private Long edge_id;

    @ManyToOne
    @JoinColumn(name = "con_id")
    private Constellation constellation;

    @Column(nullable = false)
    private Long start_node_id;

    @Column(nullable = false)
    private Long end_node_id;
}
