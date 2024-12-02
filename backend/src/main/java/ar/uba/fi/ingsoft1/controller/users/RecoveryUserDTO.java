package ar.uba.fi.ingsoft1.controller.users;

import ar.uba.fi.ingsoft1.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public record RecoveryUserDTO(
    @Getter @Setter String email,
    @Setter @Getter String momName,
    @Setter @Getter String petName,
    @Setter @Getter String color
) { }

