package me.cepera.discord.bot.diffusion.remote;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import reactor.core.publisher.Mono;

public class DiffusionRemoteService implements RemoteService{

    private static final Logger LOGGER = LogManager.getLogger(DiffusionRemoteService.class);

    private static final String URI_ROOT = "https://api.fusionbrain.ai";

    private static final String API_V1_TEXT2IMAGE_RUN = URI_ROOT + "/web/api/v1/text2image/run?model_id={modelId}";
    private static final String API_V1_TEXT2IMAGE_STATUS = URI_ROOT + "/web/api/v1/text2image/status/{processId}";

    private final Map<String, String> extraFormHeaders;

    @Inject
    public DiffusionRemoteService() {
        extraFormHeaders = ImmutableMap.<String,String>builder()
                .put("Host", "api.fusionbrain.ai")
                .build();
    }

    public Mono<byte[]> run(int modelId, byte[] paramsBytes, @Nullable byte[] imageBytes){
        Map<String, FileData> files = new LinkedHashMap<String, FileData>();
        files.put("params", new FileData("blob", "application/json", paramsBytes));
        if(imageBytes != null && imageBytes.length != 0) {
            files.put("file", new FileData("uploadedFile", "image/png", imageBytes));
        }
        return postForm(URI.create(API_V1_TEXT2IMAGE_RUN.replace("{modelId}", Integer.toString(modelId))),
                    extraFormHeaders,  Collections.emptyMap(), files)
                .doOnError(e->LOGGER.error("Error while running new painting procedure", e));
    }

    public Mono<byte[]> getStatus(String processId){
        return get(URI.create(API_V1_TEXT2IMAGE_STATUS.replace("{processId}", processId)))
                .doOnError(e->LOGGER.error("Error while fetching painting procedure status", e));
    }

}
