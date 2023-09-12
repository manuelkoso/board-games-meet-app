package it.units.boardgamesmeetapp.viewmodels.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.units.boardgamesmeetapp.utils.Result;

public class LoginResult {
    @Nullable
    private final Integer message;
    @NonNull
    private final Result result;

    public LoginResult(@NonNull Result result) {
        this.message = null;
        this.result = result;
    }

    public LoginResult(@NonNull Result result, @NonNull Integer error) {
        this.message = error;
        this.result = result;
    }

    @Nullable
    public Integer getMessage() {
        return message;
    }

    @NonNull
    public Result getResult() {
        return result;
    }
}