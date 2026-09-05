package net.anvilcraft.anvillib.ae2.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAETagCompound;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.api.ae2.channel.IItemResearchStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IItemResearchStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class ItemResearchStack implements IItemResearchStack {

    private IItemResearchStorageChannel channel = AEApi.instance().storage().getStorageChannel(IItemResearchStorageChannel.class);
    private IAEItemStack item;
    private double researchProgress = 0;

    public ItemResearchStack(IAEItemStack item) {
        this.item = item.empty();
        this.item.setStackSize(1);
    }

    @Override
    public void add(IItemResearchStack is) {
        if (this.isSameType(is)) {
            this.researchProgress = Math.max(this.getResearchProgress(), is.getResearchProgress());
        }
    }

    @Override
    public long getStackSize() {
        return this.researchProgress == 0 ? 0 : 1;
    }

    @Override
    public IItemResearchStack setStackSize(long stackSize) {
        return this;
    }

    @Override
    public long getCountRequestable() {
        return 0;
    }

    @Override
    public IItemResearchStack setCountRequestable(long countRequestable) {
        return this;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public IItemResearchStack setCraftable(boolean isCraftable) {
        return this;
    }

    @Override
    public IItemResearchStack reset() {
        this.researchProgress = 0;
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return this.researchProgress > 0;
    }

    @Override
    public void incStackSize(long i) {
        
    }

    @Override
    public void decStackSize(long i) {
        
    }

    @Override
    public void incCountRequestable(long i) {
        
    }

    @Override
    public void decCountRequestable(long i) {
        
    }

    public static IItemResearchStack readFromNBT(NBTTagCompound i) {
        NBTTagCompound it = i.getCompoundTag("item");
        IAEItemStack item = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createFromNBT(it);
        IItemResearchStack stack = new ItemResearchStack(item);
        stack.setResearchProgress(i.getDouble("researchProgress"));
        return stack;
    }

    @Override
    public void writeToNBT(NBTTagCompound i) {
        NBTTagCompound item = new NBTTagCompound();
        this.item.writeToNBT(item);
        i.setTag("item", item);
        i.setDouble("researchProgress", this.researchProgress);
    }

    @Override
    public boolean fuzzyComparison(Object st, FuzzyMode mode) {
        return this.item.fuzzyComparison(st, mode);
    }

    public static IItemResearchStack readFromPacket(ByteBuf data) {
        int length = data.readInt();
        byte[] bytes = new byte[length];
        data.readBytes(bytes);
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            NBTTagCompound nbt = CompressedStreamTools.read(inputStream);
            return readFromNBT(nbt);
        } catch (IOException e) {
            return null;
        }
        
    }

    @Override
    public void writeToPacket(ByteBuf data) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        CompressedStreamTools.write(nbt, outputStream);

        byte[] bytes = byteOutputStream.toByteArray();
        int length = bytes.length;

        data.writeInt(length);
        data.writeBytes(bytes);
    }

    @Override
    public IItemResearchStack copy() {
        IItemResearchStack stack = new ItemResearchStack(this.item);
        stack.setResearchProgress(this.researchProgress);
        return stack;
    }

    @Override
    public IItemResearchStack empty() {
        return this.copy().reset();
    }

    @Override
    public IAETagCompound getTagCompound() {
        return this.item.getTagCompound();
    }

    @Override
    public IStorageChannel<?> getStorageChannel() {
        return this.channel;
    }

    @Override
    public double getResearchProgress() {
        return this.researchProgress;
    }

    @Override
    public void setResearchProgress(double researchProgress) {
        this.researchProgress = researchProgress;
    }

    @Override
    public IAEItemStack getItemStack() {
        return this.item.copy();
    }

    @Override
    public Item getItem() {
        return this.item.getItem();
    }

    @Override
    public int getItemDamage() {
        return this.item.getItemDamage();
    }

    @Override
    public boolean isSameType(IItemResearchStack stack) {
        return stack.isSameType(this.item);
    }

    @Override
    public boolean isSameType(IAEItemStack otherStack) {
        return this.item.isSameType(otherStack);
    }

    @Override
    public boolean isSameType(ItemStack stored) {
        return this.item.isSameType(stored);
    }
    
}
