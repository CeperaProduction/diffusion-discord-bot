package me.cepera.discord.bot.diffusion.di;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.cepera.discord.bot.diffusion.repository.ChronicleUserStyleRepository;
import me.cepera.discord.bot.diffusion.repository.UserStyleRepository;

@Module
public class DataModule {

    @Provides
    @Singleton
    UserStyleRepository userStyleRepository() {
        File file = new File("data/user-styles.dat");
        file.getParentFile().mkdirs();
        return new ChronicleUserStyleRepository(file);
    }

}
