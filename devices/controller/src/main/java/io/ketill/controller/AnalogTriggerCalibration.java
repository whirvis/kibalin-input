package io.ketill.controller;

/**
 * Used by {@link AnalogTrigger} and {@link TriggerState} to calibrate
 * the current force of an analog trigger to the appropriate bounds of
 * {@code -1.0F} to {@code 1.0F}.
 *
 * @see #apply(float)
 * @see AnalogStickCalibration
 */
public final class AnalogTriggerCalibration {

    private static float normalize(float value, float upper, float lower) {
        return (value - lower) / (upper - lower);
    }

    private final float upperBound;
    private final float lowerBound;

    /**
     * @param upperBound the upper bound. This <i>must</i> have a value
     *                   greater than {@code lowerBound}.
     * @param lowerBound the lower bound. This <i>must</i> have a value
     *                   lower than {@code upperBound}.
     * @throws IllegalArgumentException if the value of {@code upperBound} is
     *                                  not greater than {@code lowerBound}.
     */
    public AnalogTriggerCalibration(float upperBound, float lowerBound) {
        /*
         * If the upper bound is not greater than the lower bound, it would
         * break normalization. This would render calibration useless.
         */
        if (upperBound <= lowerBound) {
            String msg = "upperBound must be greater than lowerBound";
            throw new IllegalArgumentException(msg);
        }

        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    /**
     * @return the upper bound for this calibration. This is guaranteed to be
     * greater than the value returned by {@link #getLowerBound()}.
     */
    public float getUpperBound() {
        return this.upperBound;
    }

    /**
     * @return the lower bound for this calibration. This is guaranteed to be
     * lower than the value returned by {@link #getUpperBound()}.
     */
    public float getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Applies this calibration to the specified value.
     *
     * @param value the value to apply this calibration to. The returned
     *              value will be on a scale of {@code 0.0F} to {@code 1.0F}.
     * @return the calibrated force.
     */
    public float apply(float value) {
        return normalize(value, upperBound, lowerBound);
    }

}
