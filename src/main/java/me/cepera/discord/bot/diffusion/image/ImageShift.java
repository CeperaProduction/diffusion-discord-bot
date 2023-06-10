package me.cepera.discord.bot.diffusion.image;

public interface ImageShift {

    public int x(int sourceImageWidth, int targetImageWidth, int sourceImageHeight, int targetImageHeight);

    public int y(int sourceImageWidth, int targetImageWidth, int sourceImageHeight, int targetImageHeight);

}
