package mrjake.aunis.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.util.math.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side class helping with the ring's rotation.
 *
 * @author MrJake222
 */
public class StargatePegasusSpinHelper implements ISpinHelper {
  public StargatePegasusSpinHelper() {

  }

  public static float A_ANGLE_PER_TICK = 5;
  public static final float U_SPEEDUP_TIME = 0;
  public static final float S_STOP_TIME = 0;

  public SymbolTypeEnum symbolType;

  public boolean isSpinning;
  public SymbolInterface currentSymbol;
  public EnumSpinDirection direction = EnumSpinDirection.CLOCKWISE;

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

  /**
   * First phase function (with default values).
   */
  private static final MathRangedFunction SPEEDUP_PHASE_DEFAULT = getSpeedupRangedFunction(A_ANGLE_PER_TICK, U_SPEEDUP_TIME);

  /**
   * First phase ranged function generation method.
   *
   * @param a Angle per tick
   * @param u Speedup time
   *
   * @return 1st phase function
   */
  private static MathRangedFunction getSpeedupRangedFunction(float a, float u) {
    return new MathRangedFunction(new MathRange(0, u), new MathFunctionQuadratic(a / (2 * u), 0, 0));
  }

  /**
   * Second phase's function (with default values).
   */
  private static final MathFunctionLinear LINEAR_SPIN_FUNCTION_DEFAULT = getLinearSpinFunction(A_ANGLE_PER_TICK, U_SPEEDUP_TIME);

  /**
   * Second phase function generation method.
   *
   * @param a Angle per tick
   * @param u Speedup time
   *
   * @return 2nd phase function
   */
  private static MathFunctionLinear getLinearSpinFunction(float a, float u) {
    return new MathFunctionLinear(a, -a * u / 2);
  }

  private static MathFunctionQuadratic getStopFunction(float a, float u, float s, float x0) {
    return new MathFunctionQuadratic(-a / (2 * s), a + (a * x0 / s), -(a * u / 2 + a * x0 * x0 / (2 * s)));
  }

  private static float getx0(float targetAngle) {
    return targetAngle / A_ANGLE_PER_TICK + (U_SPEEDUP_TIME - S_STOP_TIME) / 2;
  }

  private static float getTargetRotation(float x0) {
    return A_ANGLE_PER_TICK * x0 + A_ANGLE_PER_TICK * (S_STOP_TIME - U_SPEEDUP_TIME) / 2;
  }

  public static float getMinimalDistance() {
    return getTargetRotation(U_SPEEDUP_TIME);
  }

  public static int getAnimationDuration(float distance) {
    return (int) (getx0(distance) + S_STOP_TIME);
  }

  /**
   * {@link Map} containing the phases.
   */
  private Map<MathRange, MathFunction> phases = new HashMap<MathRange, MathFunction>(3);

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
      Aunis.logger.warn("Negative argument");
      return 0;
    }

    return (tick) % 36;
  }

  public float apply(double tick) {
    float slot = calculate((float) tick - spinStartTime);
    return ((direction.mul == -1 ? 36 - slot : slot) + startOffset) % 36;
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
