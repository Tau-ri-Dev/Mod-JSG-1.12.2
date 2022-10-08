package tauri.dev.jsg.stargate.codesender;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author matousss
 */
public enum CodeSenderType {
    PLAYER(0, PlayerCodeSender::new), COMPUTER(1, ComputerCodeSender::new);

    private static final Map<Integer, CodeSenderType> idMap = new HashMap<>();

    public final Supplier<CodeSender> constructor;
    public final int id;

    CodeSenderType(int id, Supplier<CodeSender> constructor) {
        this.constructor = constructor;
        this.id = id;
    }

    public static CodeSenderType fromId(int id) {
        return idMap.get(id);
    }


    static {
        for (CodeSenderType member : CodeSenderType.values()) {
            idMap.put(member.id, member);
        }
    }

}
