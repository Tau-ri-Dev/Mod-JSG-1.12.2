package tauri.dev.jsg.util.math;

@SuppressWarnings("unused")
public class TemperatureHelper {
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
        final double n;

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
        final double n;

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
        final double n;

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
