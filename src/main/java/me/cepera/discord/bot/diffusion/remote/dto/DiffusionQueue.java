package me.cepera.discord.bot.diffusion.remote.dto;

import java.util.Objects;

public class DiffusionQueue {

    private int count;

    private int dayCount;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, dayCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiffusionQueue other = (DiffusionQueue) obj;
        return count == other.count && dayCount == other.dayCount;
    }

    @Override
    public String toString() {
        return "DiffusionQueue [count=" + count + ", dayCount=" + dayCount + "]";
    }

}
