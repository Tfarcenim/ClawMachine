package tfar.clawmachine;

import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Function;

public record ColorFamily<T>(EnumMap<DyeColor, T> map) {
    public static final DyeColor[] DYE_COLORS = DyeColor.values();

    public static <T> ColorFamily<T> createAndRegister(Registry<? super T> deferredRegister, Function<DyeColor,T> creator, String suffix) {
        EnumMap<DyeColor,T> m = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DYE_COLORS) {
            m.put(color, Registry.register(deferredRegister,ClawMachine.id(color.getName()+"_"+ suffix),creator.apply(color)));
        }
        return new ColorFamily<>(m);
    }

    public void forEach(Consumer<T> consumer) {
        map.forEach((color, supplier) -> consumer.accept(supplier));
    }

    public T getEntry(DyeColor color) {
        return map.get(color);
    }
}
