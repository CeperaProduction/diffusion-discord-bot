package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

public class DiffusionPaintingParams {

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public int hashCode() {
        return Objects.hash(query);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionPaintingParams other = (DiffusionPaintingParams) obj;
        return Objects.equals(query, other.query);
    }

    @Override
    public String toString() {
        return "DiffusionPaintingParams [query=" + query + "]";
    }

}
