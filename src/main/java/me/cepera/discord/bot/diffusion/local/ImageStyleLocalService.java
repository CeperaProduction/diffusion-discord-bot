package me.cepera.discord.bot.diffusion.local;

import javax.inject.Inject;

import discord4j.common.util.Snowflake;
import me.cepera.discord.bot.diffusion.enums.DefaultDiffusionImageStyle;
import me.cepera.discord.bot.diffusion.repository.UserStyleRepository;
import me.cepera.discord.bot.diffusion.style.ImageStyle;
import me.cepera.discord.bot.diffusion.style.ImageStyleRegistry;
import reactor.core.publisher.Mono;

public class ImageStyleLocalService {

    private final ImageStyleRegistry imageStyleRegistry;

    private final UserStyleRepository userStyleRepository;

    @Inject
    public ImageStyleLocalService(ImageStyleRegistry imageStyleRegistry, UserStyleRepository userStyleRepository) {
        this.imageStyleRegistry = imageStyleRegistry;
        this.userStyleRepository = userStyleRepository;
    }

    public Mono<ImageStyle> getImageStyleForUser(Snowflake userId){
        return userStyleRepository.getUserImageStyleId(userId.asLong())
                .flatMap(styleId->Mono.justOrEmpty(imageStyleRegistry.getStyleById(styleId)))
                .switchIfEmpty(Mono.fromSupplier(this::defaultImageStyle));
    }

    public Mono<Void> setImageStyleForUser(Snowflake userId, ImageStyle style){
        return userStyleRepository.setUserImageStyleId(userId.asLong(), style.getId());
    }

    private ImageStyle defaultImageStyle() {
        return DefaultDiffusionImageStyle.NO_STYLE;
    }

}
