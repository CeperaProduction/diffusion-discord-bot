package me.cepera.discord.bot.diffusion.enums;

public enum ProcessStatus {

    ERROR,
    INITIAL,
    PROCESSING,
    DONE;

    public boolean isTerminalStatus() {
        return this == ERROR || this == DONE;
    }

}
