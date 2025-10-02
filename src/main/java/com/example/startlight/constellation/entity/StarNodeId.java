package com.example.startlight.constellation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StarNodeId implements Serializable {
    private Long con_id;
    private Long node_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarNodeId that = (StarNodeId) o;
        return Objects.equals(con_id, that.con_id) &&
                Objects.equals(node_id, that.node_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(con_id, node_id);
    }
}
