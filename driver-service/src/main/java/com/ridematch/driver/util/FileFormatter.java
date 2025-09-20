package com.ridematch.driver.util;

import java.util.UUID;

public class FileFormatter {

    public static String ext(String name) {
        int i = (name == null) ? -1 : name.lastIndexOf('.');
        return (i >= 0) ? name.substring(i + 1) : "bin";
    }

    public static String license(Long driverId, String originalName) {
        return String.format(
                "drivers/%d/license/%s.%s", driverId, UUID.randomUUID(), ext(originalName));
    }

    public static String registration(Long driverId, String originalName) {
        return String.format(
                "drivers/%d/registration/%s.%s", driverId, UUID.randomUUID(), ext(originalName));
    }

    public static String image(Long driverId, int index, String originalName) {
        return String.format(
                "drivers/%d/images/%02d-%s.%s",
                driverId, index, UUID.randomUUID(), ext(originalName));
    }
}
