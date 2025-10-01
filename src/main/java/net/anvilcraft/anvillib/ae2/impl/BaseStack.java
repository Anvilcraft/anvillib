package net.anvilcraft.anvillib.ae2.impl;

import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;

public abstract class BaseStack<T extends IAEStack<T>> implements IAEStack<T> {

    private IStorageChannel<T> channel;
    protected boolean isCraftable = false;
    protected long requestable = 0;

    public BaseStack(IStorageChannel<T> channel) {
        this.channel = channel;
    }

    @Override
    public void incStackSize(long i) {
        this.setStackSize(this.getStackSize() + i);
    }

    @Override
    public void decStackSize(long i) {
        this.setStackSize(this.getStackSize() - i);
    }

    @Override
    public void incCountRequestable(long i) {
        this.requestable += i;
    }

    @Override
    public void decCountRequestable(long i) {
        this.requestable -= i;
    }

    @Override
    public long getCountRequestable() {
        return this.requestable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setCountRequestable(long countRequestable) {
        this.requestable = countRequestable;
        return (T) this;
    }

    @Override
    public boolean isCraftable() {
        return this.isCraftable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setCraftable(boolean isCraftable) {
        this.isCraftable = isCraftable;
        return (T) this;
    }

    @Override
    public boolean isMeaningful() {
        return this.getStackSize() != 0 || this.requestable > 0 || this.isCraftable;
    }

    @Override
    public T empty() {
        return this.copy().reset();
    }

    @Override
    public IStorageChannel<?> getStorageChannel() {
        return this.channel;
    }
    
}
