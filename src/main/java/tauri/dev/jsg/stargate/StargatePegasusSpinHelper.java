package tauri.dev.jsg.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

/**
 * Client-side class helping with the ring's rotation.
 *
 * @author MrJake222
 */
public class StargatePegasusSpinHelper implements ISpinHelper {


  // IntelliJ is stupid, and because of that, this constructor MUST be there!!!
  public StargatePegasusSpinHelper() {}

  public SymbolTypeEnum symbolType;

  public boolean isSpinning;
  public SymbolInterface currentSymbol;
  public EnumSpinDirection direction;

  private long spinStartTime;
  private SymbolInterface targetSymbol;
  private float startOffset;


  public boolean getIsSpinning() {
    return isSpinning;
  }

  public void setIsSpinning(boolean value) {
    isSpinning = value;
  }

  public SymbolInterface getCurrentSymbol() {
    return currentSymbol;
  }

  public void setCurrentSymbol(SymbolInterface symbol) {
    currentSymbol = symbol;
  }

  public SymbolInterface getTargetSymbol() {
    return targetSymbol;
  }

  public StargatePegasusSpinHelper(SymbolTypeEnum symbolType, SymbolInterface currentSymbol, EnumSpinDirection spinDirection, boolean isSpinning, SymbolInterface targetRingSymbol, long spinStartTime, int plusRounds) {
    this.symbolType = symbolType;
    this.currentSymbol = currentSymbol;
    this.direction = spinDirection;
    this.isSpinning = isSpinning;
    this.targetSymbol = targetRingSymbol;
    this.spinStartTime = spinStartTime;
  }

  public void initRotation(long totalWorldTime, SymbolInterface targetSymbol, EnumSpinDirection direction, float startOffset, int plusRounds) {
    this.targetSymbol = targetSymbol;
    this.direction = direction;
    this.spinStartTime = totalWorldTime;
    this.startOffset = startOffset;

    isSpinning = true;
  }

  private float calculate(float tick) {
    if (tick < 0) {
      //JSG.logger.warn("Negative argument");
      return 0;
    }

    return (tick) % 36;
  }

  public float apply(double tick) {
    float slot = calculate((float) tick - spinStartTime);
    return (float) ((direction.mul == -1 ? Math.ceil(36 - slot) : Math.floor(slot)) + startOffset) % 36;
  }

  public void toBytes(ByteBuf buf) {
    buf.writeInt(symbolType.id);

    buf.writeBoolean(isSpinning);
    buf.writeInt(currentSymbol.getId());
    buf.writeInt(direction.id);

    buf.writeLong(spinStartTime);
    buf.writeInt(targetSymbol.getId());
  }

  public void fromBytes(ByteBuf buf) {
    symbolType = SymbolTypeEnum.valueOf(buf.readInt());

    isSpinning = buf.readBoolean();
    currentSymbol = symbolType.valueOfSymbol(buf.readInt());
    direction = EnumSpinDirection.valueOf(buf.readInt());

    spinStartTime = buf.readLong();
    targetSymbol = symbolType.valueOfSymbol(buf.readInt());

    if (isSpinning) {
      initRotation(spinStartTime, targetSymbol, direction, 0, 0);
    }
  }
}
