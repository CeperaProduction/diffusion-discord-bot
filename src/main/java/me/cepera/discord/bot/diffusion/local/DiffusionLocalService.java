package me.cepera.discord.bot.diffusion.local;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.cepera.discord.bot.diffusion.converter.BodyConverter;
import me.cepera.discord.bot.diffusion.enums.ProcessStatus;
import me.cepera.discord.bot.diffusion.model.DiffusionPaintingState;
import me.cepera.discord.bot.diffusion.remote.DiffusionRemoteService;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionPaintingParams;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunParams;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionStatusResponse;
import me.cepera.discord.bot.diffusion.style.ImageStyle;
import reactor.core.publisher.Mono;

public class DiffusionLocalService {

    private static final Logger LOGGER = LogManager.getLogger(DiffusionLocalService.class);

    private final DiffusionRemoteService remoteService;

    private final BodyConverter<DiffusionRunResponse> runResponseConverter;
    private final BodyConverter<DiffusionStatusResponse> statusResponseConverter;

    private final BodyConverter<DiffusionRunParams> runParamsConverter;

    @Inject
    public DiffusionLocalService(DiffusionRemoteService remoteService,
            BodyConverter<DiffusionRunParams> runParamsConverter,
            BodyConverter<DiffusionRunResponse> runResponseConverter,
            BodyConverter<DiffusionStatusResponse> statusResponseConverter) {
        this.remoteService = remoteService;
        this.runParamsConverter = runParamsConverter;
        this.runResponseConverter = runResponseConverter;
        this.statusResponseConverter = statusResponseConverter;
    }

    public Mono<DiffusionPaintingState> checkGeneration(String jobUuid){
        return remoteService.getStatus(jobUuid)
                .flatMap(statusResponseConverter::read)
                .map(statusDto->{
                    DiffusionPaintingState state = new DiffusionPaintingState();
                    state.setStatus(processStatusFromString(statusDto.getStatus()));
                    state.setCensored(statusDto.isCensored());
                    state.setErrorDescription(statusDto.getErrorDescription());
                    state.setUuid(statusDto.getUuid());
                    List<byte[]> images = new ArrayList<>();
                    if(statusDto.getImages() != null) {
                        statusDto.getImages().forEach(imageBase64->images.add(base64ToBytes(imageBase64)));
                    }
                    state.setImages(images);
                    return state;
                });
    }

    public Mono<DiffusionPaintingState> runGeneration(String query, ImageStyle style, int width, int height, @Nullable byte[] imageBytes){
        DiffusionRunParams params = new DiffusionRunParams();
        DiffusionPaintingParams paintingParams = new DiffusionPaintingParams();
        paintingParams.setQuery(query);
        if(imageBytes == null || imageBytes.length == 0) {
            params.setType("GENERATE");
            params.setGenerateParams(paintingParams);
        }else {
            params.setType("INPAINTING");
            params.setInPaintingParams(paintingParams);
        }
        params.setStyle(query);
        params.setHeight(height);
        params.setWidth(width);
        params.setStyle(style.getStyleParamValue());
        return runParamsConverter.write(params)
                .flatMap(paramsBytes->remoteService.run(1, paramsBytes, imageBytes))
                .flatMap(runResponseConverter::read)
                .map(runResultDto->{
                    DiffusionPaintingState state = new DiffusionPaintingState();
                    state.setStatus(processStatusFromString(runResultDto.getStatus()));
                    state.setUuid(runResultDto.getUuid());
                    return state;
                });
    }

    private byte[] base64ToBytes(String base64) {
        return Base64.getDecoder().decode(new String(base64).getBytes(StandardCharsets.UTF_8));
    }

    private ProcessStatus processStatusFromString(String stringValue) {
        ProcessStatus status = ProcessStatus.valueOf(stringValue.toUpperCase());
        if(status == null) {
            LOGGER.warn("Received unknown process status {}. Figure it out as ERROR.", stringValue);
            return ProcessStatus.ERROR;
        }
        return status;
    }

}
