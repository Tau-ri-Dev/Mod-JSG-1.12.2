package tauri.dev.jsg.stargate;


import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.network.SymbolInterface;

public interface ISpinHelper {
  boolean getIsSpinning();

  void setIsSpinning(boolean value);

  SymbolInterface getCurrentSymbol();

  void setCurrentSymbol(SymbolInterface symbol);

  SymbolInterface getTargetSymbol();

  void initRotation(long totalWorldTime, SymbolInterface targetSymbol, EnumSpinDirection direction, float startOffset, int plusRounds);

  float apply(double tick);

  void toBytes(ByteBuf buf);

  void fromBytes(ByteBuf buf);
}