package me.cepera.discord.bot.diffusion.local;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import me.cepera.discord.bot.diffusion.converter.BodyConverter;
import me.cepera.discord.bot.diffusion.converter.GenericJsonBodyConverter;
import me.cepera.discord.bot.diffusion.enums.CanvasExpandingDirection;
import me.cepera.discord.bot.diffusion.enums.DefaultDiffusionImageStyle;
import me.cepera.discord.bot.diffusion.enums.QueueStatus;
import me.cepera.discord.bot.diffusion.image.ImageTransformUtils;
import me.cepera.discord.bot.diffusion.remote.DiffusionRemoteService;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionEntitiesResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionGenerationResult;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionPocket;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionQueue;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionQueueResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionStatusResponse;
import me.cepera.discord.bot.diffusion.style.ImageStyle;

@Disabled
public class DiffusionLocalServiceTest {

    static DiffusionLocalService service;

    @BeforeAll
    static void prepare() {

        DiffusionRemoteService remoteService = new DiffusionRemoteService();

        BodyConverter<DiffusionQueueResponse> queueResponseConverter = new GenericJsonBodyConverter<>(DiffusionQueueResponse.class);
        BodyConverter<DiffusionRunResponse> runResponseConverter = new GenericJsonBodyConverter<>(DiffusionRunResponse.class);
        BodyConverter<DiffusionStatusResponse> statusResponseConverter = new GenericJsonBodyConverter<>(DiffusionStatusResponse.class);
        BodyConverter<DiffusionEntitiesResponse> entitiesResponseConverter = new GenericJsonBodyConverter<>(DiffusionEntitiesResponse.class);

        service = new DiffusionLocalService(remoteService,
                queueResponseConverter,
                runResponseConverter,
                statusResponseConverter,
                entitiesResponseConverter);

    }

    @Test
    void testQueueCheck() {

        DiffusionQueue queue = service.checkQueue().block();

        System.err.println("queue: "+queue);

        assertNotNull(queue, "Queue was not received");
    }

    @Test
    void testImageGeneration() throws InterruptedException {

        ImageStyle style = DefaultDiffusionImageStyle.ANIME;
        String description = "Электропоезд ласточка";

        DiffusionPocket pocket = service.runGeneration(description, style, 1, null).block();

        System.err.println("pocket: "+pocket);

        assertNotNull(pocket, "Pocket was not received");

        QueueStatus status = QueueStatus.PROCESSING;

        for(int i = 0; !status.isTerminalStatus() && i < 120; ++i) {
            status = service.getStatus(pocket.getPocketId()).block();
            System.err.println("i: "+i+" status: "+status);
            Thread.sleep(1000L);
        }

        assertEquals(QueueStatus.SUCCESS, status, "Image generation process ended with bad status");

        DiffusionGenerationResult generationResult = service.getGenerationResults(pocket.getPocketId()).blockFirst();

        System.err.println("generated: "+generationResult);

        assertNotNull(generationResult, "Generation result was not received");

        saveImage(generationResult.getResponse().get(0), "test_generate.png");

    }

    @Test
    void testImageRepainting() throws InterruptedException, IOException {

        ImageStyle style = DefaultDiffusionImageStyle.ANIME;
        String description = "Красный трактор";
        byte[] inputImageBytes;
        try(InputStream is = DiffusionLocalServiceTest.class.getClassLoader().getResourceAsStream("test_image.png")){
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readen = 0;
            while((readen = is.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, readen);
            }
            inputImageBytes = out.toByteArray();
        }

        inputImageBytes = ImageTransformUtils.transformImageByDefault(inputImageBytes, CanvasExpandingDirection.ALL);

        System.err.println("input file size: "+inputImageBytes.length);

        DiffusionPocket pocket = service.runGeneration(description, style, 0, inputImageBytes).block();

        System.err.println("pocket: "+pocket);

        assertNotNull(pocket, "Pocket was not received");

        QueueStatus status = QueueStatus.PROCESSING;

        for(int i = 0; !status.isTerminalStatus() && i < 120; ++i) {
            status = service.getStatus(pocket.getPocketId()).block();
            System.err.println("i: "+i+" status: "+status);
            Thread.sleep(1000L);
        }

        assertEquals(QueueStatus.SUCCESS, status, "Image generation process ended with bad status");

        DiffusionGenerationResult generationResult = service.getGenerationResults(pocket.getPocketId()).blockFirst();

        System.err.println("generated: "+generationResult);

        assertNotNull(generationResult, "Generation result was not received");

        saveImage(generationResult.getResponse().get(0), "test_repaint.png");

    }

    private void saveImage(String base64Content, String name) {
        try (OutputStream output = Files.newOutputStream(Paths.get("target", name))){
            byte[] bytes = Base64.getDecoder().decode(new String(base64Content).getBytes(StandardCharsets.UTF_8));
            output.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
