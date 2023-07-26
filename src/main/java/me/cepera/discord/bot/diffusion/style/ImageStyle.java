package me.cepera.discord.bot.diffusion.style;

import me.cepera.discord.bot.diffusion.local.lang.Translatable;

public interface ImageStyle extends Translatable{

    int getId();

    String getKey();

    String getStyleParamValue();

    boolean isUndefinedStyle();

    @Override
    default String getLangKey() {
        return "style."+getKey();
    }

}
