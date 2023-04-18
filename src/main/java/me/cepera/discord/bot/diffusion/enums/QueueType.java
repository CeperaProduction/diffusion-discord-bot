package me.cepera.discord.bot.diffusion.enums;

public enum QueueType {

    GENERATE("generate");

    private final String stringValue;

    private QueueType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

}
