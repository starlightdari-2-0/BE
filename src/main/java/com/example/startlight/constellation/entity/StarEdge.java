package com.example.startlight.constellation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "star_edge")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class StarEdge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long edge_id;

    @ManyToOne
    @JoinColumn(name = "con_id")
    private Constellation constellation;

    @Column(nullable = false)
    private Long start_node_id;

    @Column(nullable = false)
    private Long end_node_id;
}
