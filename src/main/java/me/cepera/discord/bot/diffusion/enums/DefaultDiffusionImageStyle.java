package me.cepera.discord.bot.diffusion.enums;

import me.cepera.discord.bot.diffusion.style.ImageStyle;

public enum DefaultDiffusionImageStyle implements ImageStyle{
    NO_STYLE(1, "DEFAULT"),
    ANIME(2, "ANIME"),
    DETAILED(3, "UHD"),
    CYBERPUNK(4, "CYBERPUNK"),
    KANDINSKY(5, "KANDINSKY"),
    AIVAZOVSKY(6, "AIVAZOVSKY"),
    MALEVICH(7, "MALEVICH"),
    PIKASSO(8, "PIKASSO"),
    GONCHAROVA(9, "GONCHAROVA"),
    CLASSICISM(10, "CLASSICISM"),
    RENAISSANCE(11, "RENEISSANCE"),
    OIL_PAINTING(12, "OILPAINTING"),
    PENCIL_DRAWING(13, "PENCILDRAWING"),
    DIGITAL_PAINTING(14, "DIGITALPAINTING"),
    MEDIEVAL(15, "MEDIEVAL"),
    SOVIET_CARTOONS(16, "SOVIETCARTOONS"),
    RENDER_3D(17, "RENDER"),
    CARTOONS(18, "CARTOON"),
    PHOTO_STUDIO(19, "STUDIOPHOTO"),
    PORTRAIT_PHOTO(20, "PORTRAITPHOTO"),
    KHOKHLOMA(23, "KHOKHLOMA"),
    NEW_YEAR(24, "CRISTMAS");

    private final int id;
    private final String styleParamValue;

    private DefaultDiffusionImageStyle(int id, String styleParamValue) {
        this.id = id;
        this.styleParamValue = styleParamValue;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isUndefinedStyle() {
        return this == NO_STYLE;
    }

    @Override
    public String getStyleParamValue() {
        return styleParamValue;
    }

    @Override
    public String getKey() {
        return name().toLowerCase();
    }

}
