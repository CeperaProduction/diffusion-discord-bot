package me.cepera.discord.bot.diffusion.enums;

import me.cepera.discord.bot.diffusion.style.ImageStyle;

public enum DefaultDiffusionImageStyle implements ImageStyle{
    NO_STYLE(1, 1, ""),
    ANIME(2, 2, "in anime style"),
    DETAILED(3, 3, "4k, ultra HD, detailed photo"),
    CYBERPUNK(4, 4, "in cyberpunk style, futuristic cyberpunk"),
    KANDINSKY(5, 5, "painted by Vasily Kandinsky, abstractionis"),
    AIVAZOVSKY(6, 6, "painted by Aivazovsky"),
    MALEVICH(7, 7, "Malevich, suprematism, avant-garde art, 20th century, geometric shapes , colorful, Russian avant-garde"),
    PIKASSO(8, 8, "Cubist painting by Pablo Picasso, 1934, colourful"),
    GONCHAROVA(9, 9, "painted by Goncharova, Russian avant-garde, futurism, cubism, suprematism"),
    CLASSICISM(10, 10, "classicism painting, 17th century, trending on artstation, baroque painting"),
    RENAISSANCE(11, 11, "painting, renaissance old master royal collection, artstation"),
    OIL_PAINTING(12, 12, "like oil painting"),
    PENCIL_DRAWING(13, 13, "pencil art, pencil drawing, highly detailed"),
    DIGITAL_PAINTING(14, 14, "high quality, highly detailed, concept art, digital painting, by greg rutkowski trending on artstation"),
    MEDIEVAL(15, 15, "medieval painting, 15th century, trending on artstation"),
    SOVIET_CARTOONS(16, 16, "picture from soviet cartoons"),
    RENDER_3D(17, 17, "Unreal Engine rendering, 3d render, photorealistic, digital concept art, octane render, 4k HD"),
    CARTOONS(18, 18, "as cartoon, picture from cartoon"),
    PHOTO_STUDIO(19, 19, "glamorous, emotional ,shot in the photo studio, professional studio lighting, backlit, rim lighting, 8k"),
    PORTRAIT_PHOTO(20, 20, "50mm portrait photography, hard rim lighting photography"),
    MOSAIC(21, 21, "as tile mosaic"),
    ICON(22, 22, "in the style of a wooden christian medieval icon in the church"),
    KHOKHLOMA(23, 23, "in Russian style, Khokhloma, 16th century, marble, decorative, realistic"),
    NEW_YEAR(24, 24, "Новый год - christmas, winter, x-mas, decorations, new year eve, snowflakes, 4k");

    private final int id;
    private final int value;
    private final String styleQuery;

    private DefaultDiffusionImageStyle(int id, int value, String styleQuery) {
        this.id = id;
        this.value = value;
        this.styleQuery = styleQuery;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getStyleQuery() {
        return styleQuery;
    }

    @Override
    public String getKey() {
        return name().toLowerCase();
    }

}
