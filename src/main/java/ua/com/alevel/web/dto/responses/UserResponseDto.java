package ua.com.alevel.web.dto.responses;

import ua.com.alevel.persistence.entities.User;
import ua.com.alevel.persistence.types.Role;

import java.time.Instant;

public class UserResponseDto extends BaseResponseDto{


    private Long id;
    private Instant created;
    private Instant updated;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.created = user.getCreated();
        this.updated = user.getUpdated();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getRole();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
