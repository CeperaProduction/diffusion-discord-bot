package me.cepera.discord.bot.diffusion.remote;

import java.net.URI;
import java.util.HashMap;
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

    private static final String URI_ROOT = "https://fusionbrain.ai";

    private static final String API_V1_TEXT2IMAGE_CHECK_QUEUE = URI_ROOT + "/api/v1/text2image/inpainting/checkQueue";
    private static final String API_V1_TEXT2IMAGE_RUN = URI_ROOT + "/api/v1/text2image/run";
    private static final String API_V1_TEXT2IMAGE_STATUS = URI_ROOT + "/api/v1/text2image/generate/pockets/{pocketId}/status";
    private static final String API_V1_TEXT2IMAGE_ENTITIES = URI_ROOT + "/api/v1/text2image/generate/pockets/{pocketId}/entities";

    private final Map<String, String> extraFormHeaders;

    @Inject
    public DiffusionRemoteService() {
        extraFormHeaders = ImmutableMap.<String,String>builder()
                .put("Host", "fusionbrain.ai")
                .put("Origin", "https://fusionbrain.ai")
                .put("Referer", "https://fusionbrain.ai/diffusion")
                .build();
    }

    public Mono<byte[]> checkQueue(){
        return get(URI.create(API_V1_TEXT2IMAGE_CHECK_QUEUE))
                .doOnError(e->LOGGER.error("Error while checking queue", e));
    }

    public Mono<byte[]> run(String queueType, Map<String, String> parameters, @Nullable byte[] imageBytes){
        Map<String, String> formData = new LinkedHashMap<String, String>();
        formData.put("queueType", queueType);
        formData.putAll(parameters);
        Map<String, FileData> files = new HashMap<String, FileData>();
        if(imageBytes != null && imageBytes.length != 0) {
            files.put("image", new FileData("uploadedFile", "image/png", imageBytes));
        }
        return postForm(URI.create(API_V1_TEXT2IMAGE_RUN), extraFormHeaders, formData, files)
                .doOnError(e->LOGGER.error("Error while running new painting procedure", e));
    }

    public Mono<byte[]> getStatus(String pocketId){
        return get(URI.create(API_V1_TEXT2IMAGE_STATUS.replace("{pocketId}", pocketId)))
                .doOnError(e->LOGGER.error("Error while fetching painting procedure status", e));
    }

    public Mono<byte[]> getEntities(String pocketId){
        return get(URI.create(API_V1_TEXT2IMAGE_ENTITIES.replace("{pocketId}", pocketId)))
                .doOnError(e->LOGGER.error("Error while fetching painting procedure results", e));
    }

}
