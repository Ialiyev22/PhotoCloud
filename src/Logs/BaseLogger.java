package Logs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseLogger {
    private static final String INFO_LOG_FILE = "C:\\Users\\ismay\\IdeaProjects\\PhotoCloud\\src\\Logs\\application_info.txt";
    private static final String ERROR_LOG_FILE = "C:\\Users\\ismay\\IdeaProjects\\PhotoCloud\\src\\Logs\\application_error.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy");

    public static LoggerInfo info() {
        return new LoggerInfo(INFO_LOG_FILE);
    }

    public static LoggerError error() {
        return new LoggerError(ERROR_LOG_FILE);
    }

    public static class LoggerInfo {
        private final String logFile;

        private LoggerInfo(String logFile) {
            this.logFile = logFile;
        }

        public void log(String message) {
            logToFile("[INFO] " + message, logFile);
        }
    }

    public static class LoggerError {
        private final String logFile;

        private LoggerError(String logFile) {
            this.logFile = logFile;
        }

        public void log(String message) {
            logToFile("[ERROR] " + message, logFile);
        }
    }

    private static void logToFile(String message, String logFile) {
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            String logEntry = "[" + getDate() + "]" + message;
            printWriter.println("[" + getDate() + "]" + message);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDate() {
        return DATE_FORMAT.format(new Date());
    }
}
