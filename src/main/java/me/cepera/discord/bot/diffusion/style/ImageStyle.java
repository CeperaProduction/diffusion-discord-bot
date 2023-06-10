package me.cepera.discord.bot.diffusion.style;

import me.cepera.discord.bot.diffusion.local.lang.Translatable;

public interface ImageStyle extends Translatable{

    int getId();

    String getKey();

    int getValue();

    String getStyleQuery();

    default boolean isUndefinedStyle() {
        return getStyleQuery().isEmpty();
    }

    @Override
    default String getLangKey() {
        return "style."+getKey();
    }

}
