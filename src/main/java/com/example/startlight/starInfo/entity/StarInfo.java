package com.example.startlight.starInfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name="star_info")
@AllArgsConstructor
@NoArgsConstructor
public class StarInfo {
    @Id
    private Long info_id;

    private Long memory_id;

    private Long node_id;

    private Long con_id;
}
