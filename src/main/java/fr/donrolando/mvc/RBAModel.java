package fr.donrolando.mvc;

import com.pi4j.mvc.util.mvcbase.ObservableValue;

/**
 * In MVC the 'Model' mainly consists of 'ObservableValues'.
 *
 * There should be no need for additional methods.
 *
 * All the application logic is handled by the 'Controller'
 */
public class RBAModel {
    public final ObservableValue<String>  systemInfo = new ObservableValue<>("JavaFX " + System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".");
    public final ObservableValue<Integer> counter    = new ObservableValue<>(73);
    public final ObservableValue<Boolean> ledGlows   = new ObservableValue<>(false);

    public final ObservableValue<Boolean> connected   = new ObservableValue<>(false);

    public final ObservableValue<String> ip = new ObservableValue<>("192.168.0.9");
}
