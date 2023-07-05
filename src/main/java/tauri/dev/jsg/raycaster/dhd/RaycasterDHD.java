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
                new Vector3f(0.194732f, 0.41862f, 0.734536f),
                new Vector3f(0.131432f, 0.314995f, 0.884427f),
                new Vector3f(0.469472f, 0.221105f, 0.851502f),
                new Vector3f(0.317212f, 0.18185f, 0.963274f)
        )));
        add(new RayCastedButton(1, Arrays.asList(
                new Vector3f(0.469472f, 0.221105f, 0.851502f),
                new Vector3f(0.317212f, 0.18185f, 0.963274f),
                new Vector3f(0.351322f, 0.339657f, 0.781297f),
                new Vector3f(0.237242f, 0.261759f, 0.915953f)
        )));
        add(new RayCastedButton(2, Arrays.asList(
                new Vector3f(0.351322f, 0.339657f, 0.781297f),
                new Vector3f(0.237242f, 0.261759f, 0.915953f),
                new Vector3f(0.536292f, 0.075897f, 0.937493f),
                new Vector3f(0.362702f, 0.083923f, 1.021265f)
        )));
        add(new RayCastedButton(3, Arrays.asList(
                new Vector3f(0.536292f, 0.075897f, 0.937493f),
                new Vector3f(0.362702f, 0.083923f, 1.021265f),
                new Vector3f(0.544482f, -0.08011f, 1.029879f),
                new Vector3f(0.368752f, -0.021404f, 1.083639f)
        )));
        add(new RayCastedButton(4, Arrays.asList(
                new Vector3f(0.544482f, -0.08011f, 1.029879f),
                new Vector3f(0.368752f, -0.021404f, 1.083639f),
                new Vector3f(0.493172f, -0.229914f, 1.11859f),
                new Vector3f(0.334732f, -0.122722f, 1.143638f)
        )));
        add(new RayCastedButton(5, Arrays.asList(
                new Vector3f(0.493172f, -0.229914f, 1.11859f),
                new Vector3f(0.334732f, -0.122722f, 1.143638f),
                new Vector3f(0.387932f, -0.357156f, 1.193942f),
                new Vector3f(0.264312f, -0.209033f, 1.19475f)
        )));
        add(new RayCastedButton(6, Arrays.asList(
                new Vector3f(0.387932f, -0.357156f, 1.193942f),
                new Vector3f(0.264312f, -0.209033f, 1.19475f),
                new Vector3f(0.222882f, -0.454946f, 1.251851f),
                new Vector3f(0.153402f, -0.275803f, 1.234291f)
        )));
        add(new RayCastedButton(7, Arrays.asList(
                new Vector3f(0.222882f, -0.454946f, 1.251851f),
                new Vector3f(0.153402f, -0.275803f, 1.234291f),
                new Vector3f(0.047192f, -0.494148f, 1.275066f),
                new Vector3f(0.035032f, -0.303148f, 1.250484f)
        )));
        add(new RayCastedButton(8, Arrays.asList(
                new Vector3f(0.047192f, -0.494148f, 1.275066f),
                new Vector3f(0.035032f, -0.303148f, 1.250484f),
                new Vector3f(-0.133748f, -0.481964f, 1.267851f),
                new Vector3f(-0.087238f, -0.295938f, 1.246214f)
        )));
        add(new RayCastedButton(9, Arrays.asList(
                new Vector3f(-0.133748f, -0.481964f, 1.267851f),
                new Vector3f(-0.087238f, -0.295938f, 1.246214f),
                new Vector3f(-0.300218f, -0.419719f, 1.230991f),
                new Vector3f(-0.200168f, -0.254955f, 1.221944f)
        )));
        add(new RayCastedButton(10, Arrays.asList(
                new Vector3f(-0.300218f, -0.419719f, 1.230991f),
                new Vector3f(-0.200168f, -0.254955f, 1.221944f),
                new Vector3f(-0.421968f, -0.327041f, 1.176108f),
                new Vector3f(-0.291498f, -0.184631f, 1.180299f)
        )));
        add(new RayCastedButton(11, Arrays.asList(
                new Vector3f(-0.421968f, -0.327041f, 1.176108f),
                new Vector3f(-0.291498f, -0.184631f, 1.180299f),
                new Vector3f(-0.514048f, -0.192459f, 1.09641f),
                new Vector3f(-0.351348f, -0.092598f, 1.125798f)
        )));
        add(new RayCastedButton(12, Arrays.asList(
                new Vector3f(-0.514048f, -0.192459f, 1.09641f),
                new Vector3f(-0.351348f, -0.092598f, 1.125798f),
                new Vector3f(-0.550158f, -0.039411f, 1.005777f),
                new Vector3f(-0.373208f, 0.011163f, 1.064352f)
        )));
        add(new RayCastedButton(13, Arrays.asList(
                new Vector3f(-0.550158f, -0.039411f, 1.005777f),
                new Vector3f(-0.373208f, 0.011163f, 1.064352f),
                new Vector3f(-0.526348f, 0.115408f, 0.914095f),
                new Vector3f(-0.354728f, 0.115415f, 1.002616f)
        )));
        add(new RayCastedButton(14, Arrays.asList(
                new Vector3f(-0.526348f, 0.115408f, 0.914095f),
                new Vector3f(-0.354728f, 0.115415f, 1.002616f),
                new Vector3f(-0.445228f, 0.25511f, 0.831365f),
                new Vector3f(-0.297898f, 0.20885f, 0.947285f)
        )));
        add(new RayCastedButton(15, Arrays.asList(
                new Vector3f(-0.445228f, 0.25511f, 0.831365f),
                new Vector3f(-0.297898f, 0.20885f, 0.947285f),
                new Vector3f(-0.315628f, 0.364455f, 0.766612f),
                new Vector3f(-0.208888f, 0.281343f, 0.904355f)
        )));
        add(new RayCastedButton(16, Arrays.asList(
                new Vector3f(-0.315628f, 0.364455f, 0.766612f),
                new Vector3f(-0.208888f, 0.281343f, 0.904355f),
                new Vector3f(-0.151708f, 0.431501f, 0.726908f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f)
        )));
        add(new RayCastedButton(17, Arrays.asList(
                new Vector3f(-0.151708f, 0.431501f, 0.726908f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f),
                new Vector3f(0.028662f, 0.448942f, 0.71658f),
                new Vector3f(0.024652f, 0.335189f, 0.872468f)
        )));
        add(new RayCastedButton(18, Arrays.asList(
                new Vector3f(-0.151708f, 0.431501f, 0.726908f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f),
                new Vector3f(0.028662f, 0.448942f, 0.71658f),
                new Vector3f(0.024652f, 0.335189f, 0.872468f)
        )));

        // INNER RING
        add(new RayCastedButton(19, Arrays.asList(
                new Vector3f(0.071232f, 0.21041f, 1.02261f),
                new Vector3f(0.131432f, 0.314995f, 0.884427f),
                new Vector3f(0.128182f, 0.181499f, 1.039731f),
                new Vector3f(0.317212f, 0.18185f, 0.963274f)
        )));
        add(new RayCastedButton(20, Arrays.asList(
                new Vector3f(0.128182f, 0.181499f, 1.039731f),
                new Vector3f(0.237242f, 0.261759f, 0.915953f),
                new Vector3f(0.171142f, 0.138253f, 1.065341f),
                new Vector3f(0.317212f, 0.18185f, 0.963274f)
        )));
        add(new RayCastedButton(21, Arrays.asList(
                new Vector3f(0.171142f, 0.138253f, 1.065341f),
                new Vector3f(0.317212f, 0.18185f, 0.963274f),
                new Vector3f(0.195462f, 0.085344f, 1.096672f),
                new Vector3f(0.362702f, 0.083923f, 1.021265f)
        )));
        add(new RayCastedButton(22, Arrays.asList(
                new Vector3f(0.195462f, 0.085344f, 1.096672f),
                new Vector3f(0.362702f, 0.083923f, 1.021265f),
                new Vector3f(0.198502f, 0.028512f, 1.130328f),
                new Vector3f(0.368752f, -0.021404f, 1.083639f)
        )));
        add(new RayCastedButton(23, Arrays.asList(
                new Vector3f(0.198502f, 0.028512f, 1.130328f),
                new Vector3f(0.368752f, -0.021404f, 1.083639f),
                new Vector3f(0.179942f, -0.026092f, 1.162664f),
                new Vector3f(0.334732f, -0.122722f, 1.143638f)
        )));
        add(new RayCastedButton(24, Arrays.asList(
                new Vector3f(0.179942f, -0.026092f, 1.162664f),
                new Vector3f(0.334732f, -0.122722f, 1.143638f),
                new Vector3f(0.141792f, -0.072556f, 1.190179f),
                new Vector3f(0.264312f, -0.209033f, 1.19475f)
        )));
        add(new RayCastedButton(25, Arrays.asList(
                new Vector3f(0.141792f, -0.072556f, 1.190179f),
                new Vector3f(0.264312f, -0.209033f, 1.19475f),
                new Vector3f(0.081842f, -0.108436f, 1.211427f),
                new Vector3f(0.153402f, -0.275803f, 1.234291f)
        )));
        add(new RayCastedButton(26, Arrays.asList(
                new Vector3f(0.081842f, -0.108436f, 1.211427f),
                new Vector3f(0.153402f, -0.275803f, 1.234291f),
                new Vector3f(0.017932f, -0.123047f, 1.220079f),
                new Vector3f(0.035032f, -0.303148f, 1.250484f)
        )));
        add(new RayCastedButton(27, Arrays.asList(
                new Vector3f(0.017932f, -0.123047f, 1.220079f),
                new Vector3f(0.035032f, -0.303148f, 1.250484f),
                new Vector3f(-0.048018f, -0.119011f, 1.217689f),
                new Vector3f(-0.087238f, -0.295938f, 1.246214f)
        )));
        add(new RayCastedButton(28, Arrays.asList(
                new Vector3f(-0.048018f, -0.119011f, 1.217689f),
                new Vector3f(-0.087238f, -0.295938f, 1.246214f),
                new Vector3f(-0.108888f, -0.096777f, 1.204523f),
                new Vector3f(-0.200168f, -0.254955f, 1.221944f)
        )));
        add(new RayCastedButton(29, Arrays.asList(
                new Vector3f(-0.108888f, -0.096777f, 1.204523f),
                new Vector3f(-0.200168f, -0.254955f, 1.221944f),
                new Vector3f(-0.158068f, -0.058746f, 1.182001f),
                new Vector3f(-0.291498f, -0.184631f, 1.180299f)
        )));
        add(new RayCastedButton(30, Arrays.asList(
                new Vector3f(-0.158068f, -0.058746f, 1.182001f),
                new Vector3f(-0.291498f, -0.184631f, 1.180299f),
                new Vector3f(-0.190238f, -0.009046f, 1.15257f),
                new Vector3f(-0.351348f, -0.092598f, 1.125798f)
        )));
        add(new RayCastedButton(31, Arrays.asList(
                new Vector3f(-0.190238f, -0.009046f, 1.15257f),
                new Vector3f(-0.351348f, -0.092598f, 1.125798f),
                new Vector3f(-0.201908f, 0.04696f, 1.119403f),
                new Vector3f(-0.373208f, 0.011163f, 1.064352f)
        )));
        add(new RayCastedButton(32, Arrays.asList(
                new Vector3f(-0.201908f, 0.04696f, 1.119403f),
                new Vector3f(-0.373208f, 0.011163f, 1.064352f),
                new Vector3f(-0.191838f, 0.103181f, 1.08611f),
                new Vector3f(-0.354728f, 0.115415f, 1.002616f)
        )));
        add(new RayCastedButton(33, Arrays.asList(
                new Vector3f(-0.191838f, 0.103181f, 1.08611f),
                new Vector3f(-0.354728f, 0.115415f, 1.002616f),
                new Vector3f(-0.161088f, 0.153552f, 1.056281f),
                new Vector3f(-0.297898f, 0.20885f, 0.947285f)
        )));
        add(new RayCastedButton(34, Arrays.asList(
                new Vector3f(-0.161088f, 0.153552f, 1.056281f),
                new Vector3f(-0.297898f, 0.20885f, 0.947285f),
                new Vector3f(-0.113008f, 0.192607f, 1.033153f),
                new Vector3f(-0.208888f, 0.281343f, 0.904355f)
        )));
        add(new RayCastedButton(35, Arrays.asList(
                new Vector3f(-0.113008f, 0.192607f, 1.033153f),
                new Vector3f(-0.208888f, 0.281343f, 0.904355f),
                new Vector3f(-0.052798f, 0.216123f, 1.019227f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f)
        )));
        add(new RayCastedButton(36, Arrays.asList(
                new Vector3f(-0.052798f, 0.216123f, 1.019227f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f),
                new Vector3f(0.013022f, 0.221544f, 1.016017f),
                new Vector3f(0.024652f, 0.335189f, 0.872468f)
        )));
        add(new RayCastedButton(37, Arrays.asList(
                new Vector3f(0.013022f, 0.221544f, 1.016017f),
                new Vector3f(-0.097338f, 0.325036f, 0.878481f),
                new Vector3f(0.071232f, 0.21041f, 1.02261f),
                new Vector3f(0.024652f, 0.335189f, 0.872468f)
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
