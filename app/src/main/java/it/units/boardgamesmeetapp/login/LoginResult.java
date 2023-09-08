package it.units.boardgamesmeetapp.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.units.boardgamesmeetapp.utils.Result;

class LoginResult {
    @Nullable
    private final Integer message;
    @NonNull
    private final Result result;

    LoginResult(@NonNull Result result) {
        this.message = null;
        this.result = result;
    }

    LoginResult(@NonNull Result result, @NonNull Integer error) {
        this.message = error;
        this.result = result;
    }

    @Nullable
    Integer getMessage() {
        return message;
    }

    @NonNull
    public Result getResult() {
        return result;
    }
}