package entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kredes on 25/05/2017.
 */
public class Report {

    public interface ReportUpdatedListener {
        void onErrorAdded(String error);
        void onWarningAdded(String error);
        void onInfoAdded(String error);
    }

    private List<String> errors;
    private List<String> warnings;
    private List<String> info;

    private ReportUpdatedListener listener;

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");;

    Report() {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
        info = new ArrayList<>();
        listener = null;
    }

    Report(ReportUpdatedListener listener) {
        this();
        this.listener = listener;
    }

    /* -----------------
        GETTERS/SETTERS
       ----------------- */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public boolean hasInfo() {
        return !info.isEmpty();
    }


    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getInfo() {
        return info;
    }


    public void addError(String error) {
        errors.add(addTimestamp(error));
        if (listener != null) listener.onErrorAdded(error);
    }

    public void addWarning(String warning) {
        warnings.add(addTimestamp(warning));
        if (listener != null) listener.onWarningAdded(warning);
    }

    public void addInfo(String info) {
        this.info.add(addTimestamp(info));
        if (listener != null) listener.onInfoAdded(info);
    }

    private String addTimestamp(String message) {
        return String.format("%s: %s", dateFormat.format(Calendar.getInstance().getTime()), message);
    }
}

