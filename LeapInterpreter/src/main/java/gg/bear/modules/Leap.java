package gg.bear.modules;

import com.leapmotion.leap.Controller;
import gg.bear.core.DetectionEngine;
import gg.bear.helper.Logger;
import gg.bear.helper.Utilities;
import gg.bear.modules.language.LanguageEngine;
import gg.bear.visuals.UI;
import org.nd4j.common.config.ND4JSystemProperties;

import java.awt.*;


public class Leap {

    public static Controller controller;
    public static EventListener listener;
    public static LeapConfig config;
    public static LanguageEngine language;
    public static DetectionEngine detection;
    public static UI visuals;

    public static void Initialize() throws Exception {

        DisableLogs();

        controller = controller == null ? new Controller() : controller;
        listener = listener == null ? new EventListener() : listener;
        detection = detection == null ? new DetectionEngine() : detection;
        config = config == null ? new LeapConfig(LeapConfig.Mode.CONVENTIONAL, 0.8f, 3, 25, 10) : config;
        language = language == null ? new LanguageEngine() : language;
        visuals = visuals == null ? new UI(new Dimension(600, 500)) : visuals;

        controller.addListener(listener);
        Utilities.RestartLeapService();
    }

    private static void DisableLogs(){
        Logger.LOG("Attempting to disable ND4J logs");
        System.setProperty(ND4JSystemProperties.LOG_INITIALIZATION, "false");
        System.setProperty(ND4JSystemProperties.ND4J_IGNORE_AVX, "true");
        System.setProperty(ND4JSystemProperties.VERSION_CHECK_PROPERTY, "false");
    }

    public static void Destroy(){
        controller.removeListener(listener);
    }

}


