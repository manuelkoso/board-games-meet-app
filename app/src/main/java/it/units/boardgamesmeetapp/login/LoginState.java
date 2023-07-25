package it.units.boardgamesmeetapp.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form. This is the UI state 
 */
class LoginState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer passwordError;
    private final boolean isDataValid;

    LoginState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}