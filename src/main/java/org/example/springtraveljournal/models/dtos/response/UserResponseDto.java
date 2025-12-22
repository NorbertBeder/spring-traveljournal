package org.example.springtraveljournal.models.dtos.response;

public class UserResponseDto {

    private Long id;
    private String surname;
    private String name;
    private String email;

    public void setId(Long id) {
        this.id = id;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
