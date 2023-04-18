package me.cepera.discord.bot.diffusion.style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.cepera.discord.bot.diffusion.enums.DefaultDiffusionImageStyle;

public class ImageStyleRegistry {

    private List<ImageStyle> styles = new ArrayList<>();
    private Map<Integer, ImageStyle> byId = new HashMap<>();

    public ImageStyleRegistry() {
        registerDefaultStyles();
    }

    private void registerDefaultStyles() {
        Arrays.asList(DefaultDiffusionImageStyle.values()).forEach(this::registerImageStyle);
    }

    public void registerImageStyle(ImageStyle style) {
        byId.put(style.getId(), style);
        styles.add(style);
    }

    public List<ImageStyle> getStyles() {
        return Collections.unmodifiableList(styles);
    }

    public Optional<ImageStyle> getStyleByName(String styleName) {
        return styles.stream().filter(style->isStyleName(style, styleName)).findAny();
    }

    private boolean isStyleName(ImageStyle style, String name) {
        if(style.getKey().equalsIgnoreCase(name) || style.getKey().replace('_', ' ').equalsIgnoreCase(name)) {
            return true;
        }
        return false;
    }

    public Optional<ImageStyle> getStyleById(Integer id) {
        return Optional.ofNullable(byId.get(id));
    }

}
