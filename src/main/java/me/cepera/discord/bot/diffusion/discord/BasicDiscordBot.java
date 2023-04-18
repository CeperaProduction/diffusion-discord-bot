package me.cepera.discord.bot.diffusion.discord;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import me.cepera.discord.bot.diffusion.local.lang.LanguageLocalService;
import me.cepera.discord.bot.diffusion.style.ImageStyle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public abstract class BasicDiscordBot implements DiscordBot{

    private static final Logger LOGGER = LogManager.getLogger(BasicDiscordBot.class);

    private final Scheduler botActionsScheduler = Schedulers.newBoundedElastic(Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE, Integer.MAX_VALUE, "discord-bot-actions");

    protected final LanguageLocalService languageLocalService;

    public BasicDiscordBot(LanguageLocalService languageLocalService) {
        this.languageLocalService = languageLocalService;
    }

    @Override
    public void start(String botApiKey) {

        if(botApiKey == null || botApiKey.isEmpty()) {
            throw new IllegalArgumentException("Discord bot key must be provided.");
        }

        DiscordClient.create(botApiKey)
            .withGateway(client->{

                client.getEventDispatcher().on(ReadyEvent.class)
                    .flatMap(this::handleReadyEvent)
                    .subscribe();

                configureGatewayClient(client);

                registerCommands(client);


                return client.onDisconnect();
            })
            .block();

    }

    protected void registerCommands(GatewayDiscordClient client) {
        client.getRestClient().getApplicationId()
            .flatMapMany(appId->commandsToRegister()
                    .flatMap(command->client.getRestClient().getApplicationService()
                            .createGlobalApplicationCommand(appId, command)))
            .doOnNext(commandData->LOGGER.info("Global command {} registered.", commandData.name()))
            .subscribe();
    }

    protected Flux<ApplicationCommandRequest> commandsToRegister(){
        return Flux.empty();
    }

    protected void configureGatewayClient(GatewayDiscordClient client) {

        client.on(ChatInputInteractionEvent.class)
            .publishOn(botActionsScheduler)
            .flatMap(event->this.handleChatInputInteractionEvent(event)
                    .onErrorResume(e->{
                        LOGGER.error("Error on handling chat interaction", e);
                        return Mono.empty();
                    }))
            .subscribe();

    }

    protected Mono<Void> handleReadyEvent(ReadyEvent event){
        return Mono.fromRunnable(()->LOGGER.info("Discord bot logged in as {}", event.getSelf().getUsername()));
    }

    protected Mono<Void> handleChatInputInteractionEvent(ChatInputInteractionEvent event){
        return Mono.empty();
    }

    @Nullable
    protected Map<String, String> localization(String key){
        return languageLocalService.getLocalizations(key);
    }

    @Nullable
    protected Map<String, String> localization(ImageStyle style){
        return localization("style."+style.getKey());
    }

    protected String localization(String locale, ImageStyle style){
        return localization(locale, "style."+style.getKey());
    }

    protected String localization(String locale, String key, String... replacementPairs) {
        Map<String, String> langToValue = languageLocalService.getLocalizations(key, replacementPairs);
        if(langToValue == null) {
            return key;
        }
        String l = Optional.ofNullable(locale).filter(s->!s.isEmpty()).orElse("en-US");
        return langToValue.getOrDefault(l, langToValue.getOrDefault("en-US", key));
    }

}
