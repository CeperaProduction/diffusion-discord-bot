package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = SnakeCaseStrategy.class)
public class DiffusionGenerationParams {

    private int width;

    private int height;

    private int numSteps;

    private int numImages;

    private double guidanceScale;

    private String query;

    private String style;

    private String hash;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    public double getGuidanceScale() {
        return guidanceScale;
    }

    public void setGuidanceScale(double guidanceScale) {
        this.guidanceScale = guidanceScale;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guidanceScale, hash, height, numImages, numSteps, query, style, width);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionGenerationParams other = (DiffusionGenerationParams) obj;
        return Double.doubleToLongBits(guidanceScale) == Double.doubleToLongBits(other.guidanceScale)
                && Objects.equals(hash, other.hash) && height == other.height && numImages == other.numImages
                && numSteps == other.numSteps && Objects.equals(query, other.query)
                && Objects.equals(style, other.style) && width == other.width;
    }

    @Override
    public String toString() {
        return "DiffusionGenerationParams [width=" + width + ", height=" + height + ", numSteps=" + numSteps
                + ", numImages=" + numImages + ", guidanceScale=" + guidanceScale + ", query=" + query + ", style="
                + style + ", hash=" + hash + "]";
    }

}
