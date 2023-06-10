package me.cepera.discord.bot.diffusion.local;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.cepera.discord.bot.diffusion.converter.BodyConverter;
import me.cepera.discord.bot.diffusion.enums.QueueStatus;
import me.cepera.discord.bot.diffusion.remote.DiffusionRemoteService;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionEntitiesResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionGenerationResult;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionPocket;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionQueue;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionQueueResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionStatusResponse;
import me.cepera.discord.bot.diffusion.style.ImageStyle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DiffusionLocalService {

    private static final Logger LOGGER = LogManager.getLogger(DiffusionLocalService.class);

    private final DiffusionRemoteService remoteService;

    private final BodyConverter<DiffusionQueueResponse> queueResponseConverter;
    private final BodyConverter<DiffusionRunResponse> runResponseConverter;
    private final BodyConverter<DiffusionStatusResponse> statusResponseConverter;
    private final BodyConverter<DiffusionEntitiesResponse> entitiesResponseConverter;

    @Inject
    public DiffusionLocalService(DiffusionRemoteService remoteService,
            BodyConverter<DiffusionQueueResponse> queueResponseConverter,
            BodyConverter<DiffusionRunResponse> runResponseConverter,
            BodyConverter<DiffusionStatusResponse> statusResponseConverter,
            BodyConverter<DiffusionEntitiesResponse> entitiesResponseConverter) {
        this.remoteService = remoteService;
        this.queueResponseConverter = queueResponseConverter;
        this.runResponseConverter = runResponseConverter;
        this.statusResponseConverter = statusResponseConverter;
        this.entitiesResponseConverter = entitiesResponseConverter;
    }

    public Mono<DiffusionQueue> checkQueue(){
        return remoteService.checkQueue()
                .flatMap(queueResponseConverter::read)
                .map(this::fetchResult);
    }

    public Mono<QueueStatus> getStatus(String pocketId){
        return remoteService.getStatus(pocketId)
                .flatMap(statusResponseConverter::read)
                .map(this::fetchResult)
                .map(this::queueStatusFromString);
    }

    public Mono<DiffusionPocket> runGeneration(String query, ImageStyle style, double preset, @Nullable byte[] imageBytes){
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("query", query);
        params.put("preset", presetToString(preset));
        params.put("style", style.getStyleQuery());
        return remoteService.run("generate", params, imageBytes)
                .flatMap(runResponseConverter::read)
                .map(this::fetchResult);
    }

    private String presetToString(double preset) {
        if(preset == (long) preset) {
            return String.format("%d",(long)preset);
        }else {
            return String.format("%s",preset);
        }
    }

    public Flux<DiffusionGenerationResult> getGenerationResults(String pocketId){
        return remoteService.getEntities(pocketId)
                .flatMap(entitiesResponseConverter::read)
                .flatMapIterable(DiffusionEntitiesResponse::getResult);
    }

    private <T> T fetchResult(DiffusionResponse<T> response) {
        if(!response.isSuccess()) {
            LOGGER.error("Diffusion service responsed with error flag. Response: {}", response);
            throw new IllegalStateException("Diffusion service responsed with error flag");
        }
        return response.getResult();
    }

    private QueueStatus queueStatusFromString(String stringValue) {
        QueueStatus status = QueueStatus.valueOf(stringValue.toUpperCase());
        if(status == null) {
            LOGGER.warn("Received unknown queue status {}. Figure it out as ERROR.", stringValue);
            return QueueStatus.ERROR;
        }
        return status;
    }

}
