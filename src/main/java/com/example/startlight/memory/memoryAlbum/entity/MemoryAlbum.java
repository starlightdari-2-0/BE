package com.example.startlight.memory.memoryAlbum.entity;

import com.example.startlight.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "MemoryAlbum")
public class MemoryAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long letter_id;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "memory_album_images", joinColumns = @JoinColumn(name = "letter_id"))
    private List<String> images = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    private Boolean opened = false;

    public void setOpened() {
        this.opened = true;
    }
}
