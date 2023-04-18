package me.cepera.discord.bot.diffusion.discord;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.core.spec.MessageCreateFields;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import io.netty.util.internal.ThrowableUtil;
import me.cepera.discord.bot.diffusion.enums.DefaultDiffusionImageStyle;
import me.cepera.discord.bot.diffusion.enums.QueueStatus;
import me.cepera.discord.bot.diffusion.local.DiffusionLocalService;
import me.cepera.discord.bot.diffusion.local.ImageStyleLocalService;
import me.cepera.discord.bot.diffusion.local.lang.LanguageLocalService;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionGenerationResult;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionQueue;
import me.cepera.discord.bot.diffusion.style.ImageStyle;
import me.cepera.discord.bot.diffusion.style.ImageStyleRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DiffusionDiscordBot extends BasicDiscordBot{

    private static final Logger LOGGER = LogManager.getLogger(DiffusionDiscordBot.class);

    public static final String COMMAND_PAINT = "paint";
    public static final String COMMAND_STYLE = "style";
    public static final String COMMAND_QUEUE = "queue";

    public static final String COMMAND_OPTION_DESCRIPTION = "description";
    public static final String COMMAND_OPTION_STYLE = "style";

    private final ImageStyleRegistry imageStyleRegistry;

    private final DiffusionLocalService diffusionService;

    private final ImageStyleLocalService imageStyleLocalService;

    @Inject
    public DiffusionDiscordBot(ImageStyleRegistry imageStyleRegistry, DiffusionLocalService diffusionService,
            ImageStyleLocalService imageStyleLocalService, LanguageLocalService languageLocalService) {
        super(languageLocalService);
        this.imageStyleRegistry = imageStyleRegistry;
        this.diffusionService = diffusionService;
        this.imageStyleLocalService = imageStyleLocalService;
    }

    @Override
    protected Flux<ApplicationCommandRequest> commandsToRegister() {
        return Flux.create(sink->{
           sink.next(ApplicationCommandRequest.builder()
                   .name(COMMAND_PAINT)
                   .nameLocalizationsOrNull(localization("command.paint"))
                   .description(localization(null, "command.paint.description"))
                   .descriptionLocalizationsOrNull(localization("command.paint.description"))
                   .addOption(ApplicationCommandOptionData.builder()
                           .name(COMMAND_OPTION_DESCRIPTION)
                           .nameLocalizationsOrNull(localization("command.paint.option.description"))
                           .description(localization(null, "command.paint.option.description.description"))
                           .descriptionLocalizationsOrNull(localization("command.paint.option.description.description"))
                           .required(true)
                           .type(3)
                           .build())
                   .addOption(ApplicationCommandOptionData.builder()
                           .name(COMMAND_OPTION_STYLE)
                           .nameLocalizationsOrNull(localization("command.paint.option.style"))
                           .description(localization(null, "command.paint.option.style.description"))
                           .descriptionLocalizationsOrNull(localization("command.paint.option.style.description"))
                           .required(false)
                           .type(3)
                           .addAllChoices(imageStyleRegistry.getStyles().stream()
                                   .map(style->ApplicationCommandOptionChoiceData.builder()
                                           .name(localization(null, style))
                                           .nameLocalizationsOrNull(localization(style))
                                           .value(style.getKey().replace('_', ' '))
                                           .build())
                                   .collect(Collectors.toList()))
                           .build())
                   .build());

           sink.next(ApplicationCommandRequest.builder()
                   .name(COMMAND_STYLE)
                   .nameLocalizationsOrNull(localization("command.style"))
                   .description(localization(null, "command.style.description"))
                   .descriptionLocalizationsOrNull(localization("command.style.description"))
                   .addOption(ApplicationCommandOptionData.builder()
                           .name(COMMAND_OPTION_STYLE)
                           .nameLocalizationsOrNull(localization("command.style.option.style"))
                           .description(localization(null, "command.style.option.style.description"))
                           .descriptionLocalizationsOrNull(localization("command.style.option.style.description"))
                           .required(true)
                           .type(3)
                           .addAllChoices(imageStyleRegistry.getStyles().stream()
                                   .map(style->ApplicationCommandOptionChoiceData.builder()
                                           .name(localization(null, style))
                                           .nameLocalizationsOrNull(localization(style))
                                           .value(style.getKey().replace('_', ' '))
                                           .build())
                                   .collect(Collectors.toList()))
                           .build())
                   .build());

           sink.next(ApplicationCommandRequest.builder()
                   .name(COMMAND_QUEUE)
                   .nameLocalizationsOrNull(localization("command.queue"))
                   .description(localization(null, "command.queue.description"))
                   .descriptionLocalizationsOrNull(localization("command.queue.description"))
                   .build());

           sink.complete();
        });
    }

    @Override
    protected Mono<Void> handleChatInputInteractionEvent(ChatInputInteractionEvent event) {
        String command = event.getCommandName();
        if(command.startsWith("/")) {
            command = command.substring(1);
        }
        switch(command) {
        case COMMAND_PAINT:
        {

            String description = event.getOption(COMMAND_OPTION_DESCRIPTION)
                    .flatMap(option->option.getValue())
                    .map(value->value.asString())
                    .orElse(null);

            Optional<ImageStyle> optStyle = event.getOption(COMMAND_OPTION_STYLE)
                    .flatMap(option->option.getValue())
                    .flatMap(value->imageStyleRegistry.getStyleByName(value.asString()));

            return Mono.justOrEmpty(optStyle)
                    .switchIfEmpty(imageStyleLocalService.getImageStyleForUser(event.getInteraction().getUser().getId()))
                    .flatMap(style->handlePaintCommand(event, description, style));
        }
        case COMMAND_STYLE:
        {
            ImageStyle style = event.getOption(COMMAND_OPTION_STYLE)
                    .flatMap(option->option.getValue())
                    .flatMap(value->imageStyleRegistry.getStyleByName(value.asString()))
                    .orElse(DefaultDiffusionImageStyle.NO_STYLE);

            return handleStyleCommand(event, style);
        }
        case COMMAND_QUEUE:
            return handleQueueCommand(event);
        default: return super.handleChatInputInteractionEvent(event);
        }
    }

    private Mono<Void> handlePaintCommand(ApplicationCommandInteractionEvent event, String description, ImageStyle style){
        String actionIdentity = createActionIdentity(event);
        return Mono.defer(()->event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content(generationStartedResponseTest(event, description, style))
                        .build()))
                .then(diffusionService.checkQueue())
                .doOnNext(queue->LOGGER.info("Start painting for action {}. Current queue state is {}", actionIdentity, queue))
                .then(diffusionService.runGeneration(description, style, 1))
                .flatMap(pocket->Mono.defer(()->diffusionService.getStatus(pocket.getPocketId())
                        .doOnNext(queue->LOGGER.info("Continue painting for action {}. Current queue state is {}", actionIdentity, queue))
                        .filter(QueueStatus::isTerminalStatus)
                        .repeatWhenEmpty(flux->flux.filter(counter->counter < 120L).delayElements(Duration.ofSeconds(1))))
                        .switchIfEmpty(Mono.error(()->new TimeoutException("The picture is painting for longer than 120 seconds. Stop polling.")))
                        .filter(status->status == QueueStatus.SUCCESS)
                        .switchIfEmpty(Mono.error(()->new IllegalStateException("Picture painting process ended with bad status.")))
                        .then(Mono.from(diffusionService.getGenerationResults(pocket.getPocketId()))))
                .doOnNext(result->LOGGER.info("Painting action {} ended. Hash: {} Results count: {}", actionIdentity, result.getHash(), result.getResponse().size()))
                .flatMap(result->sendImages(event, description, style, result))
                .then(Mono.<Void>fromRunnable(()->LOGGER.info("Result of painting action {} sended.", actionIdentity)))
                .onErrorResume(e->{
                    LOGGER.error("Got error on action {}: {}", actionIdentity, ThrowableUtil.stackTraceToString(e));
                    return event.editReply(generationFailedResponseTest(event, description, style))
                            .then();
                });
    }

    private Mono<Void> handleQueueCommand(ApplicationCommandInteractionEvent event){
        return diffusionService.checkQueue()
                .flatMap(queue->event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .content(queueOutput(event, queue))
                        .ephemeral(true)
                        .build()));
    }

    private Mono<Void> sendImages(ApplicationCommandInteractionEvent event, String description, ImageStyle style, DiffusionGenerationResult result) {
        List<EmbedCreateSpec> embededs = new ArrayList<>();
        List<MessageCreateFields.File> files = new ArrayList<MessageCreateFields.File>();
        int counter = 0;
        for(String base64ImageContent : result.getResponse()) {

            String imageName = "painting"+(++counter)+".png";

            EmbedCreateSpec embeded = EmbedCreateSpec.builder()
                    .title(localization(event.getInteraction().getUserLocale(), "element.painted"))
                    .description(description)
                    .addField(localization(event.getInteraction().getUserLocale(), "element.style"),
                            localization(event.getInteraction().getUserLocale(), style), true)
                    .color(chooseColor(result))
                    .timestamp(Instant.ofEpochMilli(result.getUpdatedAt().getTime()))
                    .image("attachment://"+imageName)
                    .footer(event.getInteraction().getUser().getTag(), event.getInteraction().getUser().getAvatarUrl())
                    .build();

            MessageCreateFields.File file = MessageCreateFields.File.of(imageName,
                    new ByteArrayInputStream(base64ToBytes(base64ImageContent)));

            embededs.add(embeded);
            files.add(file);

        }

        return event.createFollowup(InteractionFollowupCreateSpec.builder()
                    .addAllFiles(files)
                    .addAllEmbeds(embededs)
                    .ephemeral(false)
                    .build())
                .then();
    }

    private byte[] base64ToBytes(String base64) {
        return Base64.getDecoder().decode(new String(base64).getBytes(StandardCharsets.UTF_8));
    }

    private String createActionIdentity(ApplicationCommandInteractionEvent event) {
        return UUID.randomUUID().toString().replace("-", "")+"#"+event.getInteraction().getUser().getTag();
    }

    private Mono<Void> handleStyleCommand(ChatInputInteractionEvent event, ImageStyle style){
        return imageStyleLocalService.setImageStyleForUser(event.getInteraction().getUser().getId(), style)
                .then(Mono.fromRunnable(()->LOGGER.info("Default style of {} set to {}", event.getInteraction().getUser().getTag(), style)))
                .then(event.reply(styleChangedResponseText(event, style))
                        .withEphemeral(true));
    }

    private String styleChangedResponseText(ApplicationCommandInteractionEvent event, ImageStyle style) {
        return localization(event.getInteraction().getUserLocale(), "message.style.changed",
                "style", localization(event.getInteraction().getUserLocale(), style));
    }

    private String generationStartedResponseTest(ApplicationCommandInteractionEvent event, String description, ImageStyle style) {
        return localization(event.getInteraction().getUserLocale(), "message.paint.started",
                "description", description,
                "style", localization(event.getInteraction().getUserLocale(), style));
    }

    private String generationFailedResponseTest(ApplicationCommandInteractionEvent event, String description, ImageStyle style) {
        return localization(event.getInteraction().getUserLocale(), "message.paint.error");
    }

    private String queueOutput(ApplicationCommandInteractionEvent event, DiffusionQueue queue) {
        return localization(event.getInteraction().getUserLocale(), "message.queue.status",
                "current", Integer.toString(queue.getCount()),
                "day", Integer.toString(queue.getDayCount()));
    }

    private Color chooseColor(DiffusionGenerationResult result) {
        return Color.of(0x00ffffff & result.getHash().hashCode());
    }

}
