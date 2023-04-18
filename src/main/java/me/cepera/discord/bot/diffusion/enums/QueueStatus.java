package me.cepera.discord.bot.diffusion.enums;

public enum QueueStatus {

    ERROR,
    INITIAL,
    PROCESSING,
    SUCCESS;

    public boolean isTerminalStatus() {
        return this == ERROR || this == SUCCESS;
    }

}
