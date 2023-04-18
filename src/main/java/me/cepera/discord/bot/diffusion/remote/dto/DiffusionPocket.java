package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

public class DiffusionPocket {

    private String pocketId;

    public String getPocketId() {
        return pocketId;
    }

    public void setPocketId(String pocketId) {
        this.pocketId = pocketId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pocketId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionPocket other = (DiffusionPocket) obj;
        return Objects.equals(pocketId, other.pocketId);
    }

    @Override
    public String toString() {
        return "DiffusionPocket [pocketId=" + pocketId + "]";
    }

}
