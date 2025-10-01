package net.anvilcraft.anvillib.ae2.impl;

import java.util.*;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public final class AEItemList<T extends IAEStack<T>> implements IItemList<T> {
    private final Map<T, T> records
        = new HashMap<T, T>();

    @Override
    public void add(final T option) {
        if (option == null) {
            return;
        }

        final T st = this.getFluidRecord(option);

        if (st != null) {
            st.add(option);
            return;
        }

        final T opt = option.copy();

        this.putFluidRecord(opt);
    }

    @Override
    public T findPrecise(final T fluidStack) {
        if (fluidStack == null) {
            return null;
        }

        return this.getFluidRecord(fluidStack);
    }

    @Override
    public Collection<T>
    findFuzzy(final T filter, final FuzzyMode fuzzy) {
        if (filter == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(this.findPrecise(filter));
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }

    @Override
    public void addStorage(final T option) {
        if (option == null) {
            return;
        }

        final T st = this.getFluidRecord(option);

        if (st != null) {
            st.incStackSize(option.getStackSize());
            return;
        }

        final T opt = option.copy();

        this.putFluidRecord(opt);
    }

    @Override
    public void addCrafting(final T option) {
        if (option == null) {
            return;
        }

        final T st = this.getFluidRecord(option);

        if (st != null) {
            st.setCraftable(true);
            return;
        }

        final T opt = option.copy();
        opt.setStackSize(0);
        opt.setCraftable(true);

        this.putFluidRecord(opt);
    }

    @Override
    public void addRequestable(final T option) {
        if (option == null) {
            return;
        }

        final T st = this.getFluidRecord(option);

        if (st != null) {
            st.setCountRequestable(
                st.getCountRequestable() + option.getCountRequestable()
            );
            return;
        }

        final T opt = option.copy();
        opt.setStackSize(0);
        opt.setCraftable(false);
        opt.setCountRequestable(option.getCountRequestable());

        this.putFluidRecord(opt);
    }

    @Override
    public T getFirstItem() {
        for (final T stackType : this) {
            return stackType;
        }

        return null;
    }

    @Override
    public int size() {
        return this.records.values().size();
    }

    @Override
    public Iterator<T> iterator() {
        return new StackIterator<T>(this.records.values().iterator()
        );
    }

    @Override
    public void resetStatus() {
        for (final T i : this) {
            i.reset();
        }
    }

    private T getFluidRecord(final T fluid) {
        return this.records.get(fluid);
    }

    private T putFluidRecord(final T fluid) {
        return this.records.put(fluid, fluid);
    }
}
