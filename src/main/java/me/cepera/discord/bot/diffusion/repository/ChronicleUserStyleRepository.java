package me.cepera.discord.bot.diffusion.repository;

import java.io.File;
import java.io.IOException;

import net.openhft.chronicle.core.values.IntValue;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.values.Values;
import reactor.core.publisher.Mono;

public class ChronicleUserStyleRepository implements UserStyleRepository {

    private final ChronicleMap<LongValue, IntValue> storedStyles;

    public ChronicleUserStyleRepository(File storageFile) {
        this.storedStyles = createChronicle(storageFile);
    }

    private ChronicleMap<LongValue, IntValue> createChronicle(File storageFile){
        try {
            return ChronicleMap
                    .of(LongValue.class, IntValue.class)
                    .name("user-styles")
                    .putReturnsNull(false)
                    .entries(1<<20)
                    .createPersistedTo(storageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Integer> getUserImageStyleId(Long userLongId) {
        LongValue key = Values.newHeapInstance(LongValue.class);
        key.setValue(userLongId);
        return Mono.fromSupplier(()->storedStyles.get(key)).map(IntValue::getValue);
    }

    @Override
    public Mono<Void> setUserImageStyleId(Long userLongId, Integer imageStyleId) {
        LongValue key = Values.newHeapInstance(LongValue.class);
        key.setValue(userLongId);
        IntValue value = Values.newHeapInstance(IntValue.class);
        value.setValue(imageStyleId);
        return Mono.fromRunnable(()->storedStyles.put(key, value));
    }

}
