package ua.com.alevel.persistence.entities;

import jakarta.persistence.*;

import java.time.Instant;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant updated;

    private boolean enabled;

    public BaseEntity() {
        this.created = Instant.now();
        this.updated = Instant.now();
        this.enabled = true;
    }

    @PrePersist
    public void onPrePersist() {
        this.created = Instant.now();
        this.updated = Instant.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updated = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
