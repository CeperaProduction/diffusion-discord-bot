package me.cepera.discord.bot.diffusion.di;

import javax.inject.Singleton;

import dagger.Component;
import me.cepera.discord.bot.diffusion.discord.DiscordBot;

@Singleton
@Component(modules = {DiffusionModule.class, DiscordModule.class, DataModule.class})
public interface BaseComponent {

    DiscordBot getDiscordBot();

}
