package tauri.dev.jsg.api;


import tauri.dev.jsg.util.math.TemperatureHelper;

/**
 * All things about temperature utils can be found it {@link TemperatureHelper}
 * <p>
 * This class is for better understanding how {@link TemperatureHelper} works
 */
@SuppressWarnings("unused")
public class TemperatureUtils {
    public static double kelvinsToCelsius(double temp){
        return TemperatureHelper.asKelvins(temp).toCelsius();
    }
    public static double fahrenheitsToCelsius(double temp){
        return TemperatureHelper.asFahrenheits(temp).toCelsius();
    }

    public static double celsiusToKelvins(double temp){
        return TemperatureHelper.asCelsius(temp).toKelvins();
    }
    public static double fahrenheitsToKelvins(double temp){
        return TemperatureHelper.asFahrenheits(temp).toKelvins();
    }

    public static double celsiusToFahrenheits(double temp){
        return TemperatureHelper.asCelsius(temp).toFahrenheits();
    }
    public static double kelvinsToFahrenheits(double temp){
        return TemperatureHelper.asKelvins(temp).toFahrenheits();
    }
}
