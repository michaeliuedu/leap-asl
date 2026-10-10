# leap-asl

Real-time American Sign Language fingerspelling recognition with a Leap Motion controller.

A hand frame from the Leap is reduced to 20 geometric features (normalized finger lengths,
adjacent finger angles, pairwise fingertip distances, palm curvature radius). Those features go
to two classifiers — an SVM and a Keras feed-forward net — and a letter is only accepted when both
agree and the net is confident. Accepted letters are assembled into words, which are spell-checked
by LanguageTool; suggestions can be picked in the air with a screen-tap gesture.

Classes are the 26 static letters `a`–`z`. Java 18 + the Leap Motion SDK (`LeapJava.jar`), models
trained in Python.

## Folders

| Folder | What it does |
| --- | --- |
| `LeapASL/` | Data collection app. Label a gesture, hold it, and each capture tick appends a feature row to a CSV. IntelliJ project (no Maven); expects the Leap SDK jars from `C:\Program Files\LeapSDK\lib`. |
| `LeapASL/src/leap/` | `Leap` (entry point + capture settings), `internal/` (frame listener, feature extraction, CSV writing), `visuals/Mainframe` (capture UI). |
| `LeapASL/data/` | Collected datasets. `filtered_pre.csv` / `filtered_post.csv` are the static-pose sets (20 features + label), `rapidynamic.csv` holds palm-velocity rows for dynamic gestures, `a_validation.csv` is a held-out sample, `backups/` earlier dumps. |
| `LeapASL/out/` | Committed IntelliJ build output. |
| `LeapInterpreter/` | The live interpreter. Maven project (DL4J, ND4J, JPMML, LanguageTool); run `gg.bear.Main`. |
| `LeapInterpreter/src/main/java/gg/bear/` | `Main` + `modules/Leap` (startup and wiring), `modules/LeapConfig` (confidence threshold, frame interval, consecutive-detection count), `modules/EventListener` (per-frame loop, pause/tap handling), `core/` (feature vector → `DetectionUnit`, SVM/DNN agreement check), `modules/detection/` (model wrappers), `modules/language/` (word building + autocorrect), `visuals/` (Swing UI, hand drawing, suggestion buttons), `helper/`. |
| `LeapInterpreter/models/` | Trained models loaded at runtime from the classpath. `post/sequential_e40.h5` and `post/svm.pmml` are the ones in use; the others are different epoch counts / the pre-filter dataset. |
| `LeapInterpreter/target/` | Committed Maven build output. |
| `models/` | Colab notebooks that produced the models. `LeapASL_Basic.ipynb` trains the linear SVM and exports PMML; `LeapASL_Sequential.ipynb` trains the Keras net (20 → 128 → 128 → 26) and saves the `.h5`. |

## Running it

1. Install the Leap Motion SDK and plug in the controller.
2. `cd LeapInterpreter && mvn compile exec:java -Dexec.mainClass=gg.bear.Main`
3. Sign with the right hand over the sensor; turn the palm up to pause and tap a suggestion.

To collect more data instead, open `LeapASL/` in IntelliJ, point the `LeapJava` library at your SDK
jars, set `Leap.GESTURE` to the letter you are signing, and run `leap.Leap`.
