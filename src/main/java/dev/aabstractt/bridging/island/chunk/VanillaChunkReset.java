package dev.aabstractt.bridging.island.chunk;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;
import org.bukkit.Chunk;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class VanillaChunkReset {

    public void setSections(@NonNull net.minecraft.server.v1_8_R3.Chunk nmsChunk, @NonNull ChunkSection[] sections) {
        setField("sections", nmsChunk, sections);

        nmsChunk.getWorld().getWorld().refreshChunk(nmsChunk.locX, nmsChunk.locZ);
    }

    public @NonNull ChunkSection[] cloneSections(ChunkSection[] sections) {
        ChunkSection[] newSections = new ChunkSection[sections.length];

        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] == null) {
                continue;
            }

            newSections[i] = cloneSection(sections[i]);
        }

        return newSections;
    }

    @SneakyThrows
    public ChunkSection cloneSection(@NonNull ChunkSection chunkSection) {
        ChunkSection section = new ChunkSection(chunkSection.getYPosition(), chunkSection.getSkyLightArray() != null);

        setField("nonEmptyBlockCount", section, getFromField("nonEmptyBlockCount", chunkSection));
        setField("tickingBlockCount", section, getFromField("tickingBlockCount", chunkSection));
        setField("blockIds", section, chunkSection.getIdArray().clone());
        if (chunkSection.getEmittedLightArray() != null) {
            section.a(cloneNibbleArray(chunkSection.getEmittedLightArray()));
        }

        if (chunkSection.getSkyLightArray() != null) {
            section.b(cloneNibbleArray(chunkSection.getSkyLightArray()));
        }

        return section;
    }

    public NibbleArray cloneNibbleArray(@NonNull NibbleArray nibbleArray) {
        return new NibbleArray(nibbleArray.a().clone());
    }

    @SneakyThrows
    public void setField(@NonNull String fieldName, @NonNull Object clazz, @Nullable Object value) {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(clazz, value);
    }

    public @Nullable Object getFromField(@NonNull String fieldName, @NonNull Object clazz) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field.get(clazz);
    }

    public static @NonNull String chunkHash(@NonNull Chunk chunk) {
        return chunk.getX() + ":" + chunk.getZ();
    }
}