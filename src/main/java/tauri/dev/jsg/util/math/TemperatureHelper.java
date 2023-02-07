package tauri.dev.jsg.util.math;

@SuppressWarnings("unused")
public class TemperatureHelper {
    public enum EnumTemperatureUnit {
        KELVIN(0, "K"),
        CELSIUS(1, "\u00B0C"),
        FAHRENHEITS(2, "\u00B0F");

        public final int id;
        public final String displayUnit;

        EnumTemperatureUnit(int id, String displayUnit){
            this.id = id;
            this.displayUnit = displayUnit;
        }

        public double getTemperature(Kelvins temperature){
            switch(this){
                default:
                    return temperature.n;
                case CELSIUS:
                    return temperature.toCelsius();
                case FAHRENHEITS:
                    return temperature.toFahrenheits();
            }
        }

        public String getTemperatureToDisplay(Kelvins temperature, int decimals){
            final double temp = getTemperature(temperature);
            return String.format("%." + decimals + "f", temp) + displayUnit;
        }
    }

    public static Kelvins asKelvins(double n) {
        return new Kelvins(n);
    }

    public static Celsius asCelsius(double n) {
        return new Celsius(n);
    }

    public static Fahrenheits asFahrenheits(double n) {
        return new Fahrenheits(n);
    }

    public static class Kelvins {
        public final double n;

        private Kelvins(double n) {
            this.n = n;
        }

        public double toFahrenheits() {
            return (toCelsius() * 1.8) + 32;
        }

        public double toCelsius() {
            return n - 273.15;
        }
    }

    public static class Celsius {
        public final double n;

        private Celsius(double n) {
            this.n = n;
        }

        public double toFahrenheits() {
            return (n * 1.8) + 32;
        }

        public double toKelvins() {
            return n + 273.15;
        }
    }

    public static class Fahrenheits {
        public final double n;

        private Fahrenheits(double n) {
            this.n = n;
        }

        public double toCelsius() {
            return (n - 32) / 1.8;
        }

        public double toKelvins() {
            return toCelsius() + 273.15;
        }
    }
}
