package net.anvilcraft.anvillib.util;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

/**
 * A Collector implementation for collecting into an NBTTagList
 */
public class NBTCollector implements Collector<NBTBase, NBTTagList, NBTTagList> {
    @Override
    public Supplier<NBTTagList> supplier() {
        return NBTTagList::new;
    }

    @Override
    public BiConsumer<NBTTagList, NBTBase> accumulator() {
        return NBTTagList::appendTag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BinaryOperator<NBTTagList> combiner() {
        return (a, b) -> {
            for (NBTBase n : (List<? extends NBTBase>) b.tagList) {
                a.appendTag(n);
            }
            return a;
        };
    }

    @Override
    public Function<NBTTagList, NBTTagList> finisher() {
        return x -> x;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
    }
}
