package me.cepera.discord.bot.diffusion.discord;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.core.spec.MessageCreateFields;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Color;
import io.netty.util.internal.ThrowableUtil;
import me.cepera.discord.bot.diffusion.enums.CanvasExpandingDirection;
import me.cepera.discord.bot.diffusion.enums.DefaultDiffusionImageStyle;
import me.cepera.discord.bot.diffusion.enums.ProcessStatus;
import me.cepera.discord.bot.diffusion.image.ImageTransformUtils;
import me.cepera.discord.bot.diffusion.local.DiffusionLocalService;
import me.cepera.discord.bot.diffusion.local.ImageStyleLocalService;
import me.cepera.discord.bot.diffusion.local.lang.LanguageLocalService;
import me.cepera.discord.bot.diffusion.model.DiffusionPaintingState;
import me.cepera.discord.bot.diffusion.style.ImageStyle;
import me.cepera.discord.bot.diffusion.style.ImageStyleRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DiffusionDiscordBot extends BasicDiscordBot{

    private static final Logger LOGGER = LogManager.getLogger(DiffusionDiscordBot.class);

    public static final String COMMAND_PAINT = "paint";
    public static final String COMMAND_STYLE = "style";

    public static final String COMMAND_OPTION_DESCRIPTION = "description";
    public static final String COMMAND_OPTION_STYLE = "style";
    public static final String COMMAND_OPTION_IMAGE = "image";
    public static final String COMMAND_OPTION_DIRECTION = "direction";

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
                   .addOption(ApplicationCommandOptionData.builder()
                           .name(COMMAND_OPTION_IMAGE)
                           .nameLocalizationsOrNull(localization("command.paint.option.image"))
                           .description(localization(null, "command.paint.option.image.description"))
                           .descriptionLocalizationsOrNull(localization("command.paint.option.image.description"))
                           .type(11)
                           .required(false)
                           .build())
                   .addOption(ApplicationCommandOptionData.builder()
                           .name(COMMAND_OPTION_DIRECTION)
                           .nameLocalizationsOrNull(localization("command.paint.option.direction"))
                           .description(localization(null, "command.paint.option.direction.description"))
                           .descriptionLocalizationsOrNull(localization("command.paint.option.direction.description"))
                           .type(3)
                           .required(false)
                           .addAllChoices(Arrays.stream(CanvasExpandingDirection.values())
                                   .map(expand->ApplicationCommandOptionChoiceData.builder()
                                           .name(localization(null, expand))
                                           .nameLocalizationsOrNull(localization(expand))
                                           .value(expand.getKey())
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

            Attachment attachment = event.getOption(COMMAND_OPTION_IMAGE)
                .flatMap(option->option.getValue())
                .map(value->value.asAttachment())
                .orElse(null);

            CanvasExpandingDirection expandingDirection = event.getOption(COMMAND_OPTION_DIRECTION)
                    .flatMap(option->option.getValue())
                    .map(value->CanvasExpandingDirection.fromName(value.asString()))
                    .orElse(CanvasExpandingDirection.ALL);

            return Mono.justOrEmpty(optStyle)
                    .switchIfEmpty(imageStyleLocalService.getImageStyleForUser(event.getInteraction().getUser().getId()))
                    .flatMap(style->handlePaintCommand(event, description, style, attachment, expandingDirection));
        }
        case COMMAND_STYLE:
        {
            ImageStyle style = event.getOption(COMMAND_OPTION_STYLE)
                    .flatMap(option->option.getValue())
                    .flatMap(value->imageStyleRegistry.getStyleByName(value.asString()))
                    .orElse(DefaultDiffusionImageStyle.NO_STYLE);

            return handleStyleCommand(event, style);
        }
        default: return super.handleChatInputInteractionEvent(event);
        }
    }

    private boolean isImage(String contentType) {
        return contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg");
    }

    private Mono<Void> handlePaintCommand(ApplicationCommandInteractionEvent event, String description, ImageStyle style,
            Attachment attachment, CanvasExpandingDirection expanding){
        String actionIdentity = createActionIdentity(event, "paint");
        AtomicBoolean started = new AtomicBoolean();

        if(attachment != null) {
            String contentType = attachment.getContentType().orElse("");
            LOGGER.info("Received attachment with type {} for action {}", contentType, actionIdentity);
            if(!isImage(contentType)) {
                return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .content(wrongAttachmentResponseText(event))
                        .ephemeral(true)
                        .build());
            }
        }

        Duration pollingInterval = Duration.ofSeconds(3);
        int maxPollings = 150;

        return Mono.defer(()->event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content(generationQueuedResponseText(event, description, style))
                        .build()))
                .then(getAttachmentContent(attachment)
                        .map(sourceImageBytes->ImageTransformUtils.transformImageToSquare(sourceImageBytes, 640, 1024, expanding))
                        .switchIfEmpty(Mono.fromSupplier(()->new byte[0])))
                .flatMap(sourceImageBytes->diffusionService.runGeneration(description, style, 1024, 1024, sourceImageBytes))
                .flatMap(initialState->Mono.just(initialState)
                        .doOnNext(state->LOGGER.info("Painting for action {} started. Initial state: {}", actionIdentity, state))
                        .filter(state->state.getStatus().isTerminalStatus())
                        .switchIfEmpty(Mono.delay(pollingInterval).then(Mono.defer(()->diffusionService.checkGeneration(initialState.getUuid())
                                    .doOnNext(state->LOGGER.info("Continue painting for action {}. Current process status: {}", actionIdentity, state.getStatus()))
                                    .flatMap(state->Mono.fromSupplier(state::getStatus)
                                            .flatMap(status->Mono.just(status)
                                                    .filter(s->s != ProcessStatus.INITIAL && !started.getAndSet(true))
                                                    .flatMap(s->event.editReply(generationStartedResponseText(event, description, style)))
                                                    .then(Mono.just(status)))
                                            .filter(ProcessStatus::isTerminalStatus)
                                            .map(status->state)
                                    ))
                                .repeatWhenEmpty(flux->flux.filter(counter->counter < maxPollings).delayElements(pollingInterval))
                                .switchIfEmpty(Mono.error(()->new TimeoutException("The picture is painting for too long time. Stop polling."))))
                            ))
                .doOnNext(result->LOGGER.info("Painting action {} ended. Final painting state: {}", actionIdentity, result))
                .filter(jobState->jobState.getStatus() == ProcessStatus.DONE)
                .switchIfEmpty(Mono.error(()->new IllegalStateException("Picture painting process ended with bad status.")))
                .flatMap(result->sendImages(event, description, style, attachment, expanding, result))
                .then(Mono.<Void>fromRunnable(()->LOGGER.info("Result of painting action {} sended.", actionIdentity)))
                .onErrorResume(e->{
                    LOGGER.error("Got error on action {}: {}", actionIdentity, ThrowableUtil.stackTraceToString(e));
                    return event.editReply(generationFailedResponseText(event, description, style))
                            .then();
                });
    }

    private Mono<Void> sendImages(ApplicationCommandInteractionEvent event, String description, ImageStyle style,
            Attachment sourceImageAttachment, CanvasExpandingDirection expandingDirection, DiffusionPaintingState result) {
        List<EmbedCreateSpec> embededs = new ArrayList<>();
        List<MessageCreateFields.File> files = new ArrayList<MessageCreateFields.File>();
        int counter = 0;
        for(byte[] imageBytes : result.getImages()) {

            String imageName = "painting"+(++counter)+".png";

            EmbedCreateSpec.Builder embededBuilder = EmbedCreateSpec.builder()
                    .title(localization(event.getInteraction().getUserLocale(), "element.painted"))
                    .description(description)
                    .image("attachment://"+imageName)
                    .color(chooseColor(result))
                    .timestamp(Instant.ofEpochMilli(System.currentTimeMillis()))
                    .footer(getAuthorBlock(event), event.getInteraction().getUser().getAvatarUrl());

            if(!style.isUndefinedStyle()) {
                embededBuilder.addField(localization(event.getInteraction().getUserLocale(), "element.style"),
                        localization(event.getInteraction().getUserLocale(), style), true);
            }

            if(sourceImageAttachment != null) {
                embededBuilder.thumbnail(sourceImageAttachment.getProxyUrl());
                embededBuilder.addField(localization(event.getInteraction().getUserLocale(), "element.direction"),
                        localization(event.getInteraction().getUserLocale(), expandingDirection), true);
            }

            MessageCreateFields.File file = MessageCreateFields.File.of(imageName,
                    new ByteArrayInputStream(ImageTransformUtils.transformImageMaxDimension(imageBytes, 1024)));

            embededs.add(embededBuilder.build());
            files.add(file);

        }

        return event.createFollowup(InteractionFollowupCreateSpec.builder()
                    .addAllFiles(files)
                    .addAllEmbeds(embededs)
                    .ephemeral(false)
                    .build())
                .then();
    }

    private String getAuthorBlock(ApplicationCommandInteractionEvent event) {
        return event.getInteraction().getMember()
                .map(member->member.getDisplayName())
                .orElseGet(()->event.getInteraction().getUser().getUsername());
    }

    /*
    private String prepareUserTag(String tag) {
        if(tag.endsWith("#0")) {
            return tag.substring(0, tag.length()-2);
        }
        return tag;
    }*/



    private String createActionIdentity(ApplicationCommandInteractionEvent event, String prefix) {
        return prefix+"#"+UUID.randomUUID().toString().replace("-", "")+"#"+event.getInteraction().getUser().getTag();
    }

    private Mono<Void> handleStyleCommand(ChatInputInteractionEvent event, ImageStyle style){
        String actionIdentity = createActionIdentity(event, "style");
        return imageStyleLocalService.setImageStyleForUser(event.getInteraction().getUser().getId(), style)
                .onErrorResume(e->{
                    LOGGER.error("Got error on action {}: {}", actionIdentity, ThrowableUtil.stackTraceToString(e));
                    return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .content(commandErrorText(event))
                            .ephemeral(true)
                            .build())
                            .then(Mono.empty());
                })
                .flatMap(v->Mono.fromRunnable(()->LOGGER.info("Default style of {} set to {}", event.getInteraction().getUser().getTag(), style)))
                .then(event.reply(styleChangedResponseText(event, style))
                        .withEphemeral(true));
    }

    private String styleChangedResponseText(ApplicationCommandInteractionEvent event, ImageStyle style) {
        return localization(event.getInteraction().getUserLocale(), "message.style.changed",
                "style", localization(event.getInteraction().getUserLocale(), style));
    }

    private String generationQueuedResponseText(ApplicationCommandInteractionEvent event, String description, ImageStyle style) {
        String langKey = style.isUndefinedStyle() ? "message.paint.queued" : "message.paint.queued.styled";
        return localization(event.getInteraction().getUserLocale(), langKey,
                "description", description,
                "style", localization(event.getInteraction().getUserLocale(), style));
    }

    private String generationStartedResponseText(ApplicationCommandInteractionEvent event, String description, ImageStyle style) {
        String langKey = style.isUndefinedStyle() ? "message.paint.started" : "message.paint.started.styled";
        return localization(event.getInteraction().getUserLocale(), langKey,
                "description", description,
                "style", localization(event.getInteraction().getUserLocale(), style));
    }

    private String generationFailedResponseText(ApplicationCommandInteractionEvent event, String description, ImageStyle style) {
        return localization(event.getInteraction().getUserLocale(), "message.paint.error");
    }

    private String wrongAttachmentResponseText(ApplicationCommandInteractionEvent event) {
        return localization(event.getInteraction().getUserLocale(), "message.paint.wrong_attachment");
    }

    private Possible<Color> chooseColor(DiffusionPaintingState result) {
        if(result.isCensored() || result.getImages().isEmpty()) {
            return Possible.absent();
        }
        return Possible.of(Color.of(0x00ffffff & ImageTransformUtils.mediumARGB(result.getImages().get(0))));
    }

    private String commandErrorText(ApplicationCommandInteractionEvent event) {
        return localization(event.getInteraction().getUserLocale(), "message.command.error");
    }

}
