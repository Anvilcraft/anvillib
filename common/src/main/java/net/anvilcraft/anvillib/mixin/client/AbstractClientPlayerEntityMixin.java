package net.anvilcraft.anvillib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.authlib.GameProfile;

import net.anvilcraft.anvillib.cosmetics.CosmeticsManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    private static Identifier ELYTRA = new Identifier("textures/entity/elytra.png");

    public AbstractClientPlayerEntityMixin(
        World world, BlockPos pos, float yaw, GameProfile profile
    ) {
        super(world, pos, yaw, profile);
    }

    /**
     * @reason Custom capes & no Mojank capes
     * @author tilera
     */
    @Overwrite
    public Identifier getCapeTexture() {
        return CosmeticsManager.getCape(this.uuid);
    }

    /**
     * @reason Custom capes & no Mojank capes
     * @author tilera
     */
    @Overwrite
    public Identifier getElytraTexture() {
        return ELYTRA;
    }
}
