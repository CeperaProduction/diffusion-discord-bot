package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.cepera.discord.bot.diffusion.enums.QueueStatus;

public class DiffusionGenerationResult {

    private String id;

    private String hash;

    private DiffusionGenerationParams params;

    private List<String> response = new ArrayList<String>();

    private List<String> error = new ArrayList<String>();

    private QueueStatus status;

    private Date createdAt;

    private Date updatedAt;

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public DiffusionGenerationParams getParams() {
        return params;
    }

    public void setParams(DiffusionGenerationParams params) {
        this.params = params;
    }

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, error, hash, id, params, response, status, updatedAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionGenerationResult other = (DiffusionGenerationResult) obj;
        return Objects.equals(createdAt, other.createdAt) && Objects.equals(error, other.error)
                && Objects.equals(hash, other.hash) && Objects.equals(id, other.id)
                && Objects.equals(params, other.params) && Objects.equals(response, other.response)
                && status == other.status && Objects.equals(updatedAt, other.updatedAt);
    }

    @Override
    public String toString() {
        return "DiffusionGenerationResult [id=" + id + ", hash=" + hash + ", params=" + params + ", response="
                + response + ", error=" + error + ", status=" + status + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + "]";
    }

}
