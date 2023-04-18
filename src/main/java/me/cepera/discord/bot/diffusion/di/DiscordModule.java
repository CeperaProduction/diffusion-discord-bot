package me.cepera.discord.bot.diffusion.di;

import dagger.Binds;
import dagger.Module;
import me.cepera.discord.bot.diffusion.discord.DiffusionDiscordBot;
import me.cepera.discord.bot.diffusion.discord.DiscordBot;

@Module
public interface DiscordModule {

    @Binds
    DiscordBot discordBot(DiffusionDiscordBot diffusionDiscordBot);

}
