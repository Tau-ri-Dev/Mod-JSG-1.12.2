package tauri.dev.jsg.raycaster.ringscontroller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.transportrings.TRControllerActivatedToServer;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.vector.Vector3f;

import java.util.Arrays;
import java.util.List;

public class RaycasterRingsOriController extends RaycasterRingsAbstractController {
    public static final RaycasterRingsOriController INSTANCE = new RaycasterRingsOriController();

    private static final List<Vector3f> VERTICES = Arrays.asList(
            new Vector3f(0.314399f, -0.96172f, 0.201255f),
            new Vector3f(0.3144f, -0.961707f, 0.283443f),
            new Vector3f(0.315226f, -0.961707f, 0.382972f),
            new Vector3f(0.315226f, -0.961707f, 0.468873f),

            new Vector3f(0.431277f, -0.96172f, 0.200429f),
            new Vector3f(0.430864f, -0.96172f, 0.282202f),
            new Vector3f(0.432101f, -0.961707f, 0.386276f),
            new Vector3f(0.431275f, -0.961707f, 0.46846f),

            new Vector3f(0.566167f, -0.96172f, 0.199477f),
            new Vector3f(0.564515f, -0.96172f, 0.28662f),
            new Vector3f(0.566581f, -0.961707f, 0.387802f),
            new Vector3f(0.565342f, -0.961707f, 0.468747f),

            new Vector3f(0.682219f, -0.96172f, 0.201129f),
            new Vector3f(0.682219f, -0.96172f, 0.285381f),
            new Vector3f(0.683045f, -0.96172f, 0.387804f),
            new Vector3f(0.684695f, -0.961707f, 0.468747f)
    );

    @Override
    protected List<Vector3f> getVertices() {
        return VERTICES;
    }

    @Override
    protected void check(World world, BlockPos pos, EntityPlayer player, int x, int i) {
        if (x == 2)
            return; // Middle part

        int num = (2 - i) * 2 + 1;
        num += (x == 1 ? 1 : 0);

        player.swingArm(EnumHand.MAIN_HAND);
        JSGPacketHandler.INSTANCE.sendToServer(new TRControllerActivatedToServer(pos, num, SymbolTypeTransportRingsEnum.ORI));
    }
}
