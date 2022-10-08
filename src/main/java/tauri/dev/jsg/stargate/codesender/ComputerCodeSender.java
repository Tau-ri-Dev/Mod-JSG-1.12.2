package tauri.dev.jsg.stargate.codesender;

import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentBase;

/**
 * @author matousss
 */
public class ComputerCodeSender extends CodeSender {
    StargatePos originGate;

//    /**
//     * @param args Require :StargatePos: on first position
//     * */
//    @Override
//    public void prepareToLoad(Object[] args) {
//        originGate = (StargatePos) args[0];
//    }

    public ComputerCodeSender(StargatePos originGate) {
        this.originGate = originGate;
    }

    public ComputerCodeSender() {
    }

    @Override
    public void sendMessage(TextComponentBase message) {
        originGate.getTileEntity().sendSignal(null, "code_respond", message.getFormattedText());
    }


    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setInteger("symbolType", originGate.getTileEntity().getSymbolType().id);
        compound.setTag("originGate", originGate.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(nbt.getInteger("symbolType"));
        originGate = new StargatePos(symbolType, nbt.getCompoundTag("originGate"));
    }

    @Override
    public CodeSenderType getType() {
        return CodeSenderType.COMPUTER;
    }
}
