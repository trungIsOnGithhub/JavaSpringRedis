package com.trung.springredisapp.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Setter
public class LoginDTO {
    @NonNull
    private String username;
    @NonNull
    private String password;

    // public String getUsername() {
    //     return this.username;
    // }
}
