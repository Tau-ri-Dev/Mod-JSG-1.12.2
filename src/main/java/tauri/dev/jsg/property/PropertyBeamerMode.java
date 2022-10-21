package tauri.dev.jsg.property;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import tauri.dev.jsg.beamer.BeamerModeEnum;
import net.minecraft.block.properties.PropertyHelper;

import javax.annotation.Nonnull;
import java.util.Collection;

public class PropertyBeamerMode extends PropertyHelper<BeamerModeEnum> {

    private final ImmutableSet<BeamerModeEnum> allowedValues = ImmutableSet.<BeamerModeEnum>of(BeamerModeEnum.POWER, BeamerModeEnum.FLUID, BeamerModeEnum.ITEMS, BeamerModeEnum.LASER, BeamerModeEnum.NONE);
	
	protected PropertyBeamerMode(String name) {
		super(name, BeamerModeEnum.class);
	}
	
	public static PropertyBeamerMode create(String name) {
		return new PropertyBeamerMode(name);
	}

	@Nonnull
	@Override
	public Collection<BeamerModeEnum> getAllowedValues() {
		return allowedValues;
	}

	@Nonnull
	@Override
	public Optional<BeamerModeEnum> parseValue(@Nonnull String value) {
		if (value == null || value.isEmpty())
			return Optional.of(BeamerModeEnum.POWER);
		
		return Optional.of(BeamerModeEnum.valueOf(value.toUpperCase()));			
	}

	@Override
	public String getName(BeamerModeEnum value) {
		return value.name().toLowerCase();
	}

}
