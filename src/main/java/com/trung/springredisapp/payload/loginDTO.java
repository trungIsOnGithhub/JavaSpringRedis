package com.trung.springredisapp.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class LoginData {
    @NonNull
    private String username;
    @NonNull
    private String password;
}
