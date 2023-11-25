package tauri.dev.jsg.raycaster.dhd;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.stargate.DHDButtonClickedToServer;
import tauri.dev.jsg.packet.stargate.DHDPegasusButtonClickedToServer;
import tauri.dev.jsg.raycaster.Raycaster;
import tauri.dev.jsg.raycaster.util.RayCastedButton;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolPegasusEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDPegasusTile;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RaycasterDHD extends Raycaster {
    public static final RaycasterDHD INSTANCE = new RaycasterDHD();
    private static final ArrayList<RayCastedButton> BUTTONS = new ArrayList<RayCastedButton>() {{

        // OUTER RING
        add(new RayCastedButton(0, Arrays.asList(
                new Vector3f(0.151162f, 0.325817f, 0.872069f),
                new Vector3f(0.200706f, 0.419512f, 0.752736f),
                new Vector3f(0.341068f, 0.367061f, 0.79154f),
                new Vector3f(0.239813f, 0.277472f, 0.900207f)
        )));
        add(new RayCastedButton(1, Arrays.asList(
                new Vector3f(0.257162f, 0.267575f, 0.904695f),
                new Vector3f(0.356484f, 0.33789f, 0.798802f),
                new Vector3f(0.46274f, 0.236758f, 0.859916f),
                new Vector3f(0.327733f, 0.197505f, 0.946375f)
        )));
        add(new RayCastedButton(2, Arrays.asList(
                new Vector3f(0.338614f, 0.180977f, 0.962849f),
                new Vector3f(0.475763f, 0.21738f, 0.86965f),
                new Vector3f(0.529858f, 0.091275f, 0.944577f),
                new Vector3f(0.380741f, 0.093335f, 1.00891f)
        )));
        add(new RayCastedButton(3, Arrays.asList(
                new Vector3f(0.38389f, 0.073741f, 1.01739f),
                new Vector3f(0.537155f, 0.074784f, 0.955587f),
                new Vector3f(0.546035f, -0.062997f, 1.03641f),
                new Vector3f(0.383892f, -0.021578f, 1.07731f)
        )));
        add(new RayCastedButton(4, Arrays.asList(
                new Vector3f(0.388207f, -0.035676f, 1.0899f),
                new Vector3f(0.543544f, -0.080902f, 1.0537f),
                new Vector3f(0.492682f, -0.210578f, 1.1326f),
                new Vector3f(0.357156f, -0.126609f, 1.14128f)
        )));
        add(new RayCastedButton(5, Arrays.asList(
                new Vector3f(0.337227f, -0.139854f, 1.15372f),
                new Vector3f(0.484249f, -0.228512f, 1.14004f),
                new Vector3f(0.385004f, -0.335046f, 1.20474f),
                new Vector3f(0.276343f, -0.219463f, 1.19763f)
        )));
        add(new RayCastedButton(6, Arrays.asList(
                new Vector3f(0.263829f, -0.228907f, 1.20686f),
                new Vector3f(0.371425f, -0.364816f, 1.21541f),
                new Vector3f(0.230695f, -0.430621f, 1.26497f),
                new Vector3f(0.177192f, -0.284324f, 1.24117f)
        )));
        add(new RayCastedButton(7, Arrays.asList(
                new Vector3f(0.222882f, -0.454946f, 1.251851f),
                new Vector3f(0.153402f, -0.275803f, 1.234291f),
                new Vector3f(0.035032f, -0.303148f, 1.250484f),
                new Vector3f(0.047192f, -0.494148f, 1.275066f)
        )));
        add(new RayCastedButton(8, Arrays.asList(
                new Vector3f(0.047192f, -0.494148f, 1.275066f),
                new Vector3f(0.035032f, -0.303148f, 1.250484f),
                new Vector3f(-0.087238f, -0.295938f, 1.246214f),
                new Vector3f(-0.133748f, -0.481964f, 1.267851f)
        )));
        add(new RayCastedButton(9, Arrays.asList(
                new Vector3f(-0.133748f, -0.481964f, 1.267851f),
                new Vector3f(-0.087238f, -0.295938f, 1.246214f),
                new Vector3f(-0.200168f, -0.254955f, 1.221944f),
                new Vector3f(-0.300218f, -0.419719f, 1.230991f)
        )));
        add(new RayCastedButton(10, Arrays.asList(
                new Vector3f(-0.300218f, -0.419719f, 1.230991f),
                new Vector3f(-0.200168f, -0.254955f, 1.221944f),
                new Vector3f(-0.291498f, -0.184631f, 1.180299f),
                new Vector3f(-0.421968f, -0.327041f, 1.176108f)
        )));
        add(new RayCastedButton(11, Arrays.asList(
                new Vector3f(-0.421968f, -0.327041f, 1.176108f),
                new Vector3f(-0.291498f, -0.184631f, 1.180299f),
                new Vector3f(-0.351348f, -0.092598f, 1.125798f),
                new Vector3f(-0.514048f, -0.192459f, 1.09641f)
        )));
        add(new RayCastedButton(12, Arrays.asList(
                new Vector3f(-0.514048f, -0.192459f, 1.09641f),
                new Vector3f(-0.351348f, -0.092598f, 1.125798f),
                new Vector3f(-0.373208f, 0.011163f, 1.064352f),
                new Vector3f(-0.550158f, -0.039411f, 1.005777f)
        )));
        add(new RayCastedButton(13, Arrays.asList(
                new Vector3f(-0.550158f, -0.039411f, 1.005777f),
                new Vector3f(-0.373208f, 0.011163f, 1.064352f),
                new Vector3f(-0.354728f, 0.115415f, 1.002616f),
                new Vector3f(-0.526348f, 0.115408f, 0.914095f)
        )));
        add(new RayCastedButton(14, Arrays.asList(
                new Vector3f(-0.526348f, 0.115408f, 0.914095f),
                new Vector3f(-0.354728f, 0.115415f, 1.002616f),
                new Vector3f(-0.297898f, 0.20885f, 0.947285f),
                new Vector3f(-0.445228f, 0.25511f, 0.831365f)
        )));
        add(new RayCastedButton(15, Arrays.asList(
                new Vector3f(-0.445228f, 0.25511f, 0.831365f),
                new Vector3f(-0.297898f, 0.20885f, 0.947285f),
                new Vector3f(-0.208888f, 0.281343f, 0.904355f),
                new Vector3f(-0.315628f, 0.364455f, 0.766612f)
        )));
        add(new RayCastedButton(16, Arrays.asList(
                new Vector3f(-0.315628f, 0.364455f, 0.766612f),
                new Vector3f(-0.208888f, 0.281343f, 0.904355f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f),
                new Vector3f(-0.151708f, 0.431501f, 0.726908f)
        )));
        add(new RayCastedButton(17, Arrays.asList(
                new Vector3f(-0.151708f, 0.431501f, 0.726908f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f),
                new Vector3f(0.024652f, 0.335189f, 0.872468f),
                new Vector3f(0.028662f, 0.448942f, 0.71658f)
        )));
        add(new RayCastedButton(18, Arrays.asList(
                new Vector3f(0.021218f, 0.34566f, 0.85676f),
                new Vector3f(0.028662f, 0.45552f, 0.72822f),
                new Vector3f(0.19175f, 0.42916f, 0.73982f),
                new Vector3f(0.1327f, 0.32998f, 0.86706f)
        )));

        // INNER RING
        add(new RayCastedButton(19, Arrays.asList(
                new Vector3f(0.080502f, 0.213395f, 1.0239f),
                new Vector3f(0.136089f, 0.304313f, 0.916279f),
                new Vector3f(0.208985f, 0.265438f, 0.938817f),
                new Vector3f(0.125742f, 0.190252f, 1.03535f)
        )));
        add(new RayCastedButton(20, Arrays.asList(
                new Vector3f(0.138327f, 0.178406f, 1.04151f),
                new Vector3f(0.235845f, 0.246163f, 0.947391f),
                new Vector3f(0.296105f, 0.185647f, 0.979417f),
                new Vector3f(0.17235f, 0.148579f, 1.06767f)
        )));
        add(new RayCastedButton(21, Arrays.asList(
                new Vector3f(0.182565f, 0.138913f, 1.07575f),
                new Vector3f(0.305456f, 0.171177f, 0.996411f),
                new Vector3f(0.332695f, 0.099577f, 1.04316f),
                new Vector3f(0.194296f, 0.097187f, 1.09793f)
        )));
        add(new RayCastedButton(22, Arrays.asList(
                new Vector3f(0.198945f, 0.085891f, 1.10777f),
                new Vector3f(0.339591f, 0.076627f, 1.05571f),
                new Vector3f(0.343414f, 0.002782f, 1.09758f),
                new Vector3f(0.200461f, 0.039924f, 1.1338f)
        )));
        add(new RayCastedButton(23, Arrays.asList(
                new Vector3f(0.200461f, 0.023708f, 1.14352f),
                new Vector3f(0.342901f, -0.02621f, 1.1149f),
                new Vector3f(0.311701f, -0.097979f, 1.15505f),
                new Vector3f(0.185001f, -0.016097f, 1.1671f)
        )));
        add(new RayCastedButton(24, Arrays.asList(
                new Vector3f(0.180988f, -0.031792f, 1.17984f),
                new Vector3f(0.305457f, -0.111036f, 1.17095f),
                new Vector3f(0.249234f, -0.172103f, 1.20743f),
                new Vector3f(0.147667f, -0.066553f, 1.20029f)
        )));
        add(new RayCastedButton(25, Arrays.asList(
                new Vector3f(0.134508f, -0.066833f, 1.20154f),
                new Vector3f(0.226542f, -0.187811f, 1.21846f),
                new Vector3f(0.1481f, -0.231161f, 1.24002f),
                new Vector3f(0.093645f, -0.101634f, 1.21938f)
        )));
        add(new RayCastedButton(26, Arrays.asList(
                new Vector3f(0.079127f, -0.110041f, 1.22117f),
                new Vector3f(0.129774f, -0.244233f, 1.24369f),
                new Vector3f(0.047655f, -0.265521f, 1.25722f),
                new Vector3f(0.032954f, -0.113f, 1.22795f)
        )));
        add(new RayCastedButton(27, Arrays.asList(
                new Vector3f(0.009213f, -0.118123f, 1.23024f),
                new Vector3f(0.015219f, -0.265521f, 1.25933f),
                new Vector3f(-0.068628f, -0.263051f, 1.25255f),
                new Vector3f(-0.038542f, -0.113f, 1.22795f)
        )));
        add(new RayCastedButton(28, Arrays.asList(
                new Vector3f(-0.059851f, -0.113f, 1.2225f),
                new Vector3f(-0.099771f, -0.251217f, 1.25255f),
                new Vector3f(-0.178323f, -0.21824f, 1.2334f),
                new Vector3f(-0.107714f, -0.098129f, 1.21119f)
        )));
        add(new RayCastedButton(29, Arrays.asList(
                new Vector3f(-0.116201f, -0.087238f, 1.20918f),
                new Vector3f(-0.197702f, -0.206911f, 1.22504f),
                new Vector3f(-0.258965f, -0.156237f, 1.19932f),
                new Vector3f(-0.154082f, -0.059624f, 1.19099f)
        )));
        add(new RayCastedButton(30, Arrays.asList(
                new Vector3f(-0.164657f, -0.050965f, 1.1877f),
                new Vector3f(-0.27448f, -0.141422f, 1.19125f),
                new Vector3f(-0.315615f, -0.075952f, 1.14903f),
                new Vector3f(-0.186804f, -0.011368f, 1.16467f)
        )));
        add(new RayCastedButton(31, Arrays.asList(
                new Vector3f(-0.186804f, 0.004032f, 1.15384f),
                new Vector3f(-0.32645f, -0.055455f, 1.13674f),
                new Vector3f(-0.343561f, 0.010932f, 1.09443f),
                new Vector3f(-0.202667f, 0.045195f, 1.13177f)
        )));
        add(new RayCastedButton(32, Arrays.asList(
                new Vector3f(-0.202667f, 0.061166f, 1.12374f),
                new Vector3f(-0.343561f, 0.036527f, 1.08189f),
                new Vector3f(-0.32645f, 0.116972f, 1.03894f),
                new Vector3f(-0.194461f, 0.104245f, 1.09621f)
        )));
        add(new RayCastedButton(33, Arrays.asList(
                new Vector3f(-0.190743f, 0.114643f, 1.0886f),
                new Vector3f(-0.324708f, 0.128795f, 1.0214f),
                new Vector3f(-0.280391f, 0.199214f, 0.98182f),
                new Vector3f(-0.164088f, 0.149229f, 1.06496f)
        )));
        add(new RayCastedButton(34, Arrays.asList(
                new Vector3f(-0.160209f, 0.167729f, 1.06271f),
                new Vector3f(-0.269631f, 0.212853f, 0.970854f),
                new Vector3f(-0.208329f, 0.265349f, 0.941268f),
                new Vector3f(-0.12214f, 0.194183f, 1.04021f)
        )));
        add(new RayCastedButton(35, Arrays.asList(
                new Vector3f(-0.111777f, 0.204922f, 1.03702f),
                new Vector3f(-0.190777f, 0.2782f, 0.936247f),
                new Vector3f(-0.114548f, 0.311841f, 0.915977f),
                new Vector3f(-0.066287f, 0.229501f, 1.02738f)
        )));
        add(new RayCastedButton(36, Arrays.asList(
                new Vector3f(-0.054314f, 0.221615f, 1.02209f),
                new Vector3f(-0.089957f, 0.315725f, 0.910635f),
                new Vector3f(-0.00464f, 0.330327f, 0.905571f),
                new Vector3f(-0.001933f, 0.239845f, 1.02193f)
        )));
        add(new RayCastedButton(37, Arrays.asList(
                new Vector3f(0.016991f, 0.23951f, 1.0201f),
                new Vector3f(0.026698f, 0.32611f, 0.89934f),
                new Vector3f(0.11012f, 0.31195f, 0.91009f),
                new Vector3f(0.0629f, 0.22099f, 1.0201f)
        )));

        // BRB
        add(new RayCastedButton(38, Arrays.asList(
                new Vector3f(0.071232f, 0.21041f, 1.02261f),
                new Vector3f(0.128182f, 0.181499f, 1.039731f),
                new Vector3f(0.171142f, 0.138253f, 1.065341f),
                new Vector3f(0.195462f, 0.085344f, 1.096672f),
                new Vector3f(0.198502f, 0.028512f, 1.130328f),
                new Vector3f(0.179942f, -0.026092f, 1.162664f),
                new Vector3f(0.141792f, -0.072556f, 1.190179f),
                new Vector3f(0.081842f, -0.108436f, 1.211427f),
                new Vector3f(0.017932f, -0.123047f, 1.220079f),
                new Vector3f(-0.048018f, -0.119011f, 1.217689f),
                new Vector3f(-0.108888f, -0.096777f, 1.204523f),
                new Vector3f(-0.158068f, -0.058746f, 1.182001f),
                new Vector3f(-0.190238f, -0.009046f, 1.15257f),
                new Vector3f(-0.201908f, 0.04696f, 1.119403f),
                new Vector3f(-0.191838f, 0.103181f, 1.08611f),
                new Vector3f(-0.161088f, 0.153552f, 1.056281f),
                new Vector3f(-0.113008f, 0.192607f, 1.033153f),
                new Vector3f(-0.052798f, 0.216123f, 1.019227f),
                new Vector3f(0.013022f, 0.221544f, 1.016017f)
        )));
    }};

    @Override
    protected List<RayCastedButton> getButtons() {
        return BUTTONS;
    }

    private boolean isSneaking = false;

    public boolean onActivated(World world, BlockPos pos, EntityPlayer player, EnumHand hand, boolean isSneaking) {
        float rotation = world.getBlockState(pos).getValue(JSGProps.ROTATION_HORIZONTAL) * -22.5f;
        this.isSneaking = isSneaking;
        return super.onActivated(world, pos, player, rotation, hand);
    }

    private static final Vector3f TRANSLATION = new Vector3f(0.5f, 0, 0.5f);

    @Override
    protected Vector3f getTranslation(World world, BlockPos pos) {
        return TRANSLATION;
    }

    @Override
    protected boolean buttonClicked(World world, EntityPlayer player, int button, BlockPos pos, EnumHand hand) {
        if (button != -1 && hand == EnumHand.MAIN_HAND) {
            player.swingArm(hand);

            // DHD inner ring rotation
            if (button >= 19 && button < 38) {
                // Inner ring
                button += 11;

                if (button >= 38)
                    button -= 19;
            }

            if (world.isRemote) {
                boolean pegasusDHD = world.getTileEntity(pos) instanceof DHDPegasusTile;
                if (!pegasusDHD) {
                    SymbolMilkyWayEnum symbol = SymbolMilkyWayEnum.valueOf(button);
                    if (!isSneaking) {
                        JSGPacketHandler.INSTANCE.sendToServer(new DHDButtonClickedToServer(pos, symbol));
                        return true;
                    }
                } else {
                    SymbolPegasusEnum symbol = SymbolPegasusEnum.valueOf(button);
                    if (!isSneaking) {
                        if (symbol == SymbolPegasusEnum.UNKNOW1 || symbol == SymbolPegasusEnum.UNKNOW2)
                            player.sendStatusMessage(
                                    new TextComponentTranslation("tile.jsg.dhd_pegasus_block.unknown_buttons"),
                                    true);
                        else JSGPacketHandler.INSTANCE.sendToServer(new DHDPegasusButtonClickedToServer(pos, symbol));
                        return true;
                    } else if (symbol.brb()) {
                        JSGPacketHandler.INSTANCE.sendToServer(new DHDPegasusButtonClickedToServer(pos, symbol, true));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
