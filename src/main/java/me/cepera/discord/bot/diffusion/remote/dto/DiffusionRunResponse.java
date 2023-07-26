package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

public class DiffusionRunResponse {

    private String uuid;

    private String status;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionRunResponse other = (DiffusionRunResponse) obj;
        return Objects.equals(status, other.status) && Objects.equals(uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "DiffusionRunResponse [uuid=" + uuid + ", status=" + status + "]";
    }

}
