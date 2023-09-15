package it.units.boardgamesmeetapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SubmissionResult {
    @Nullable
    private final Integer message;
    @NonNull
    private final Result result;

    public SubmissionResult(@NonNull Result result) {
        this.message = null;
        this.result = result;
    }

    public SubmissionResult(@NonNull Result result, @NonNull Integer error) {
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