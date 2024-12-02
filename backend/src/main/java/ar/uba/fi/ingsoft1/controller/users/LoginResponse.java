package ar.uba.fi.ingsoft1.controller.users;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public record LoginResponse (
    //user data and token
    @NonNull @Setter @Getter String token,
    @NonNull @Setter @Getter UserDTO user
) {
    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}
