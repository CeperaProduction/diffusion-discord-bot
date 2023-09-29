package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DiffusionRunParams {

    private String type;

    private String style;

    private Integer width;

    private Integer height;

    private String negativePromptDecoder;

    private DiffusionPaintingParams inPaintingParams;

    private DiffusionPaintingParams generateParams;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public DiffusionPaintingParams getInPaintingParams() {
        return inPaintingParams;
    }

    public void setInPaintingParams(DiffusionPaintingParams inPaintingParams) {
        this.inPaintingParams = inPaintingParams;
    }

    public DiffusionPaintingParams getGenerateParams() {
        return generateParams;
    }

    public void setGenerateParams(DiffusionPaintingParams generateParams) {
        this.generateParams = generateParams;
    }

    public String getNegativePromptDecoder() {
        return negativePromptDecoder;
    }

    public void setNegativePromptDecoder(String negativePromptDecoder) {
        this.negativePromptDecoder = negativePromptDecoder;
    }

    @Override
    public String toString() {
        return "DiffusionRunParams [type=" + type + ", style=" + style + ", width=" + width + ", height=" + height
                + ", negativePromptDecoder=" + negativePromptDecoder + ", inPaintingParams=" + inPaintingParams
                + ", generateParams=" + generateParams + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(generateParams, height, inPaintingParams, negativePromptDecoder, style, type, width);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionRunParams other = (DiffusionRunParams) obj;
        return Objects.equals(generateParams, other.generateParams) && Objects.equals(height, other.height)
                && Objects.equals(inPaintingParams, other.inPaintingParams)
                && Objects.equals(negativePromptDecoder, other.negativePromptDecoder)
                && Objects.equals(style, other.style) && Objects.equals(type, other.type)
                && Objects.equals(width, other.width);
    }

}
