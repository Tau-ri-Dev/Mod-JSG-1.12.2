package tauri.dev.jsg.config.ingame;

import java.util.List;

public interface ITileConfigEntry {
    int getId();
    String getLabel();
    String[] getComment();
    JSGConfigOptionTypeEnum getType();
    String getDefaultValue();
    List<JSGConfigEnumEntry> getPossibleValues();

    int getMin();
    int getMax();
}
