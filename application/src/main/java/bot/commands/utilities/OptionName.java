package bot.commands.utilities;

import lombok.Getter;

@Getter
public enum OptionName {
	AUTOPLAY("Autoplay", false);

	private String displayName;
	private boolean defaultValue;

	OptionName(final String displayName, final boolean defaultValue) {
		this.displayName = displayName;
		this.defaultValue = defaultValue;
	}
}
