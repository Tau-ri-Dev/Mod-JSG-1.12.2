package mrjake.aunis.stargate;


import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.stargate.network.SymbolInterface;

public interface ISpinHelper {
  boolean getIsSpinning();

  void setIsSpinning(boolean value);

  SymbolInterface getCurrentSymbol();

  void setCurrentSymbol(SymbolInterface symbol);

  SymbolInterface getTargetSymbol();

  void initRotation(long totalWorldTime, SymbolInterface targetSymbol, EnumSpinDirection direction, float startOffset);

  float apply(double tick);

  void toBytes(ByteBuf buf);

  void fromBytes(ByteBuf buf);
}