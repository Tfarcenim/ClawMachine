package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {
    public static final EntityType<StaticItemEntity> STATIC_ITEM_ENTITY = EntityType.Builder.<StaticItemEntity>of(StaticItemEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20)
            .build("static_item_entity");

    static {
        Registry.register(BuiltInRegistries.ENTITY_TYPE,ClawMachine.id("static_item_entity"),STATIC_ITEM_ENTITY);
    }

    public static void init() {

    }
}
