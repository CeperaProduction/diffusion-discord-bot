package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

public abstract class DiffusionResponse<T> {

    private T result;

    private boolean success;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, success);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionResponse<?> other = (DiffusionResponse<?>) obj;
        return Objects.equals(result, other.result) && success == other.success;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+" [result=" + result + ", success=" + success + "]";
    }

}
