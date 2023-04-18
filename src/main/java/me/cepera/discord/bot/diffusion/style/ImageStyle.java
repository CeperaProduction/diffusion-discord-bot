package me.cepera.discord.bot.diffusion.style;

public interface ImageStyle {

    int getId();

    String getKey();

    int getValue();

    String getStyleQuery();

    default boolean isUndefinedStyle() {
        return getStyleQuery().isEmpty();
    }

}
