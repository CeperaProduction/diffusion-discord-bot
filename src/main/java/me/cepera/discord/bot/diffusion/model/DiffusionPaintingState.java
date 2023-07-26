package me.cepera.discord.bot.diffusion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.cepera.discord.bot.diffusion.enums.ProcessStatus;

public class DiffusionPaintingState {

    private String uuid;

    private ProcessStatus status;

    private String errorDescription;

    private List<byte[]> images = new ArrayList<>();

    private boolean censored;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public List<byte[]> getImages() {
        return images;
    }

    public void setImages(List<byte[]> images) {
        this.images = images;
    }

    public boolean isCensored() {
        return censored;
    }

    public void setCensored(boolean censored) {
        this.censored = censored;
    }

    @Override
    public int hashCode() {
        return Objects.hash(censored, errorDescription, images, status, uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionPaintingState other = (DiffusionPaintingState) obj;
        return censored == other.censored && Objects.equals(errorDescription, other.errorDescription)
                && Objects.equals(images, other.images) && status == other.status && Objects.equals(uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "DiffusionPaintingState [uuid=" + uuid + ", status=" + status + ", errorDescription=" + errorDescription
                + ", imagesCount=" + images.size() + ", censored=" + censored + "]";
    }

}
