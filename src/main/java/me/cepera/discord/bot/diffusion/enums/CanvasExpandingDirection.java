package me.cepera.discord.bot.diffusion.enums;

import java.util.function.BiFunction;

import me.cepera.discord.bot.diffusion.image.ImageShift;
import me.cepera.discord.bot.diffusion.local.lang.Translatable;

import static me.cepera.discord.bot.diffusion.enums.CanvasExpandingDirection.SimpleAxisMotions.*;

public enum CanvasExpandingDirection implements ImageShift, Translatable{

    ALL(AXIS_CENTER, AXIS_CENTER),
    RIGHT(AXIS_MIN, AXIS_CENTER),
    LEFT(AXIS_MAX, AXIS_CENTER),
    DOWN(AXIS_CENTER, AXIS_MIN),
    UP(AXIS_CENTER, AXIS_MAX),
    RIGHT_DOWN(AXIS_MIN, AXIS_MIN),
    RIGHT_UP(AXIS_MIN, AXIS_MAX),
    LEFT_DOWN(AXIS_MAX, AXIS_MIN),
    LEFT_UP(AXIS_MAX, AXIS_MAX);

    static class SimpleAxisMotions {

        static final BiFunction<Integer, Integer, Integer> AXIS_CENTER = (s,t)->t/2 - s/2;
        static final BiFunction<Integer, Integer, Integer> AXIS_MIN = (s,t)->0;
        static final BiFunction<Integer, Integer, Integer> AXIS_MAX = (s,t)->t - s;

    }

    private final BiFunction<Integer, Integer, Integer> xAxisMoveFuntion;
    private final BiFunction<Integer, Integer, Integer> yAxisMoveFuntion;

    private CanvasExpandingDirection(BiFunction<Integer, Integer, Integer> xAxisMoveFuntion,
            BiFunction<Integer, Integer, Integer> yAxisMoveFuntion) {
        this.xAxisMoveFuntion = xAxisMoveFuntion;
        this.yAxisMoveFuntion = yAxisMoveFuntion;
    }

    public static CanvasExpandingDirection fromName(String name) {
        String internalName = name.toUpperCase().replace(' ', '_');
        CanvasExpandingDirection candidate = valueOf(internalName);
        return candidate == null ? ALL : candidate;
    }

    public String getKey() {
        return name().toLowerCase().replace('_', ' ');
    }

    @Override
    public String getLangKey() {
        return "direction."+name().toLowerCase();
    }

    @Override
    public int x(int sourceImageWidth, int targetImageWidth, int sourceImageHeight, int targetImageHeight) {
        return xAxisMoveFuntion.apply(sourceImageWidth, targetImageWidth);
    }

    @Override
    public int y(int sourceImageWidth, int targetImageWidth, int sourceImageHeight, int targetImageHeight) {
        return yAxisMoveFuntion.apply(sourceImageHeight, targetImageHeight);
    }

}
