package me.cepera.discord.bot.diffusion.local;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import me.cepera.discord.bot.diffusion.converter.BodyConverter;
import me.cepera.discord.bot.diffusion.converter.GenericJsonBodyConverter;
import me.cepera.discord.bot.diffusion.enums.CanvasExpandingDirection;
import me.cepera.discord.bot.diffusion.enums.DefaultDiffusionImageStyle;
import me.cepera.discord.bot.diffusion.enums.ProcessStatus;
import me.cepera.discord.bot.diffusion.image.ImageTransformUtils;
import me.cepera.discord.bot.diffusion.model.DiffusionPaintingState;
import me.cepera.discord.bot.diffusion.remote.DiffusionRemoteService;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunParams;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionRunResponse;
import me.cepera.discord.bot.diffusion.remote.dto.DiffusionStatusResponse;
import me.cepera.discord.bot.diffusion.style.ImageStyle;

@Disabled
public class DiffusionLocalServiceTest {

    static DiffusionLocalService service;

    @BeforeAll
    static void prepare() {

        DiffusionRemoteService remoteService = new DiffusionRemoteService();

        BodyConverter<DiffusionRunParams> runParamsConverter = new GenericJsonBodyConverter<DiffusionRunParams>(DiffusionRunParams.class);
        BodyConverter<DiffusionRunResponse> runResponseConverter = new GenericJsonBodyConverter<>(DiffusionRunResponse.class);
        BodyConverter<DiffusionStatusResponse> statusResponseConverter = new GenericJsonBodyConverter<>(DiffusionStatusResponse.class);

        service = new DiffusionLocalService(remoteService,
                runParamsConverter,
                runResponseConverter,
                statusResponseConverter);

    }

    @Test
    void testImageGeneration() throws InterruptedException {

        ImageStyle style = DefaultDiffusionImageStyle.ANIME;
        String description = "Электропоезд ласточка";

        DiffusionPaintingState painting = service.runGeneration(description, style, 1024, 1024, null).block();

        System.err.println("painting: "+painting);

        assertNotNull(painting, "Painting was not received");

        for(int i = 0; !painting.getStatus().isTerminalStatus() && i < 120; ++i) {
            painting = service.checkGeneration(painting.getUuid()).block();
            assertNotNull(painting, "Painting was not received");
            System.err.println("i: "+i+" painting: "+painting);
            Thread.sleep(1000L);
        }

        assertEquals(ProcessStatus.DONE, painting.getStatus(), "Image generation process ended with bad status");

        System.err.println("generated: "+painting);

        saveImage(painting.getImages().get(0), "test_generate.png");

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

        inputImageBytes = ImageTransformUtils.transformImageToSquare(inputImageBytes, 512, 768, CanvasExpandingDirection.ALL);

        System.err.println("input file size: "+inputImageBytes.length);

        DiffusionPaintingState painting = service.runGeneration(description, style, 1024, 1024, inputImageBytes).block();

        System.err.println("painting: "+painting);

        assertNotNull(painting, "Painting was not received");

        for(int i = 0; !painting.getStatus().isTerminalStatus() && i < 120; ++i) {
            painting = service.checkGeneration(painting.getUuid()).block();
            assertNotNull(painting, "Painting was not received");
            System.err.println("i: "+i+" painting: "+painting);
            Thread.sleep(1000L);
        }

        assertEquals(ProcessStatus.DONE, painting.getStatus(), "Image generation process ended with bad status");

        System.err.println("generated: "+painting);

        saveImage(painting.getImages().get(0), "test_repaint.png");

    }

    private void saveImage(byte[] imageBytes, String name) {
        try (OutputStream output = Files.newOutputStream(Paths.get("target", name))){
            output.write(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
