package me.cepera.discord.bot.diffusion.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.cepera.discord.bot.diffusion.converter.BodyConverter;
import me.cepera.discord.bot.diffusion.converter.GenericJsonBodyConverter;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionEntitiesResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionQueueResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionStatusResponse;
import me.cepera.discord.bot.diffusion.style.ImageStyleRegistry;

@Module
public class DiffusionModule {

    @Provides
    @Singleton
    ImageStyleRegistry imageStyleRegistry() {
        return new ImageStyleRegistry();
    }

    @Provides
    BodyConverter<DiffusionStatusResponse> diffusionStatusResponseBodyConverter(){
        return new GenericJsonBodyConverter<>(DiffusionStatusResponse.class);
    }

    @Provides
    BodyConverter<DiffusionQueueResponse> diffusionQueueResponseBodyConverter(){
        return new GenericJsonBodyConverter<>(DiffusionQueueResponse.class);
    }

    @Provides
    BodyConverter<DiffusionEntitiesResponse> diffusionEntitiesResponseBodyConverter(){
        return new GenericJsonBodyConverter<>(DiffusionEntitiesResponse.class);
    }

    @Provides
    BodyConverter<DiffusionRunResponse> diffusionRunResponseBodyConverter(){
        return new GenericJsonBodyConverter<>(DiffusionRunResponse.class);
    }

}
