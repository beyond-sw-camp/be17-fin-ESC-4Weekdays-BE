package com.fourweekdays.fourweekdays.image.utils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUploadUtil {
    public static String makeUploadPath() {

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File dir = new File(date);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                return date + "/" + UUID.randomUUID().toString() + "_";
            }
        }
        return date + "/" + UUID.randomUUID().toString() + "_";
    }
}
