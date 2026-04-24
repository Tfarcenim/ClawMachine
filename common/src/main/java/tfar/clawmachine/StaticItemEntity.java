package tfar.clawmachine;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * a stripped down version of item entity
 * @see net.minecraft.world.entity.item.ItemEntity
 */
public class StaticItemEntity extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(StaticItemEntity.class, EntityDataSerializers.ITEM_STACK);


    public StaticItemEntity(EntityType<? extends StaticItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public StaticItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        this(ModEntityTypes.STATIC_ITEM_ENTITY,level);
        setPos(posX, posY, posZ);
        setItem(itemStack);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM, ItemStack.EMPTY);
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    /**
     * Sets the item that this entity represents.
     */
    public void setItem(ItemStack stack) {
        this.getEntityData().set(DATA_ITEM, stack);
    }

    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.discard();
        } else {
            super.tick();

            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
                this.applyGravity();

            if (this.level().isClientSide) {
                this.noPhysics = false;
            } else {
                this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7));
                if (this.noPhysics) {
                    this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                }
            }

            if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
                this.move(MoverType.SELF, this.getDeltaMovement());
                float f = 0.98F;
                if (this.onGround()) {
                    f = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.98F;
                }

                this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.98, (double)f));
                if (this.onGround()) {
                    Vec3 vec31 = this.getDeltaMovement();
                    if (vec31.y < 0.0) {
                        this.setDeltaMovement(vec31.multiply(1.0, -0.5, 1.0));
                    }
                }
            }

            this.hasImpulse = this.hasImpulse | this.updateInWaterStateAndDoFluidPushing();
            if (!this.level().isClientSide) {
                double d0 = this.getDeltaMovement().subtract(vec3).lengthSqr();
                if (d0 > 0.01) {
                    this.hasImpulse = true;
                }
            }

        }
    }

    @Override
    public BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return this.getOnPos(0.999999F);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity vehicle, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);BlockPos pos = blockPosition();
        if (!this.level().isClientSide && !level().getBlockState(pos).is(ModBlocks.CLAW_MACHINE) ) {
            ItemStack itemstack = this.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();
            if (player.getInventory().add(itemstack)) {
                player.take(this, i);
                if (itemstack.isEmpty()) {
                    this.discard();
                    itemstack.setCount(i);
                }

                player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                //player.onItemPickup(this);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Item", 10)) {
            CompoundTag compoundtag = compound.getCompound("Item");
            this.setItem(ItemStack.parse(this.registryAccess(), compoundtag).orElse(ItemStack.EMPTY));
        } else {
            this.setItem(ItemStack.EMPTY);
        }

        if (this.getItem().isEmpty()) {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (!this.getItem().isEmpty()) {
            compound.put("Item", this.getItem().save(this.registryAccess()));
        }
    }
}
