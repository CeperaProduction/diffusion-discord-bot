package me.cepera.discord.bot.diffusion.repository;

import reactor.core.publisher.Mono;

public interface UserStyleRepository {

    Mono<Integer> getUserImageStyleId(Long userLongId);

    Mono<Void> setUserImageStyleId(Long userLongId, Integer imageStyleId);

}
