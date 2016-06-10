package controller.communication;

import java.util.regex.Pattern;

public class SensorRequestFormatValidator {

    private static final String PART_SEPARATOR = ";";
    private static final String NUMBER_SEPARATOR = ",";
    private final static String FLOATING_POINT_NUMBER = "-?\\d+\\.\\d+";
    private final Pattern expctedPattern;

    public SensorRequestFormatValidator() {
        super();
        final String coordinatePattern = "(" + FLOATING_POINT_NUMBER + NUMBER_SEPARATOR + "){2}(" + FLOATING_POINT_NUMBER + ")";
        this.expctedPattern = Pattern.compile(coordinatePattern + PART_SEPARATOR + coordinatePattern);
    }

    public boolean isValid(final String requestMessage) {
        return null != requestMessage && requestMessage.matches(this.expctedPattern.pattern());
    }
}
