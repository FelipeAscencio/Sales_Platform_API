package ar.uba.fi.ingsoft1.controller.users;

import ar.uba.fi.ingsoft1.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public record UserDTO(
    @Getter @Setter String firstName,
    @Getter @Setter String lastName,
    @Getter @Setter int age,
    @Getter @Setter String photo,
    @Getter @Setter String email,
    @Getter @Setter String gender,
    @Getter @Setter String address,
    @Setter @Getter String momName,
    @Setter @Getter String petName,
    @Setter @Getter String color,
    @Setter @Getter Boolean isAdmin
) {
    public UserDTO(User user) {
        this(user.getFirstName(), user.getLastName(), user.getAge(), user.getPhoto(), user.getEmail(), user.getGender(), user.getAddress(), user.getMomName(), user.getFirstPetName(), user.getFavoriteColor(), user.getAdmin());
    }
}

