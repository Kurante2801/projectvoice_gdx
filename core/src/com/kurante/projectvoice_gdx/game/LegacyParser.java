package com.kurante.projectvoice_gdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kurante.projectvoice_gdx.level.ChartSection;
import com.kurante.projectvoice_gdx.level.Level;
import com.kurante.projectvoice_gdx.util.extensions.NumberKt;

import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("Java8MapApi")
public class LegacyParser {
    public static Chart parseChart(Level level, ChartSection section) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);

        String tracksName = section.getChartFilename();
        String notesName = tracksName.replace("track_", "note_");
        LegacyTrack[] legacyTracks = json.fromJson(LegacyTrack[].class, level.getFile().child(tracksName));
        LegacyNote[] legacyNotes = json.fromJson(LegacyNote[].class, level.getFile().child(notesName));

        // Sort same way as editor
        Arrays.sort(legacyTracks, (a, b) -> Float.compare(a.Start, b.Start));
        Arrays.sort(legacyNotes, (a, b) -> Float.compare(a.Time, b.Time));

        for (LegacyNote legacyNote : legacyNotes) {
            for (int i = 0; i < legacyTracks.length; i++) {
                if (legacyNote.Track == legacyTracks[i].id) {
                    legacyNote.Track = i;
                    break;
                }
            }
        }

        for (int i = 0; i < legacyTracks.length; i++)
            legacyTracks[i].id = i;

        // Start parsing
        Array<JavaTrack> tracks = new Array<>();
        for (LegacyTrack legacy : legacyTracks) {
            JavaTrack track = new JavaTrack();
            track.id = legacy.id;
            track.spawnTime = NumberKt.toMillis(legacy.Start);
            track.spawnDuration = legacy.EntranceOn ? 350 : 0;
            track.despawnTime = NumberKt.toMillis(legacy.End);
            track.despawnDuration = 250;

            track.moveTransitions = convertTransitions(track, legacy.Move, legacy.X, TransitionEase.EXIT_MOVE);
            track.scaleTransitions = convertTransitions(track, legacy.Scale, legacy.Size, TransitionEase.EXIT_SCALE);
            track.colorTransitions = new Array<>();
            // Convert color numbers into color objects
            Array<JavaTransition> colorTransitions = convertTransitions(track, legacy.ColorChange, legacy.Color, TransitionEase.EXIT_COLOR);
            for (int i = 0; i < colorTransitions.size; i++) {
                JavaTransition transition = colorTransitions.get(i);
                track.colorTransitions.add(new JavaColorTransition() {{
                    startTime = transition.startTime;
                    endTime = transition.endTime;
                    startValue = parseColor((int) transition.startValue);
                    endValue = parseColor((int) transition.endValue);
                    easing = transition.easing;
                }});
            }

            track.notes = new Array<>();
            tracks.add(track);
        }

        // Parse notes
        for (LegacyNote legacy : legacyNotes) {
            // Match note's track by its id
            JavaTrack track = null;
            for (int i = 0; i < tracks.size; i++) {
                if (tracks.get(i).id == legacy.Track)
                    track = tracks.get(i);
            }
            if (track == null) continue;

            NoteType type = parseType(legacy.Type);
            int data = type == NoteType.HOLD ? NumberKt.toMillis(legacy.Hold) : (legacy.Dir <= 0 ? -1 : 1);
            track.notes.add(new Note(legacy.Id, NumberKt.toMillis(legacy.Time), type, data));
        }

        // Convert from Java classes to Kotlin classes
        Track[] result = new Track[tracks.size];
        for (int i = 0; i < tracks.size; i++) {
            result[i] = tracks.get(i).convertTrack();
        }

        return new Chart(0, null, 0, result);
    }

    @SuppressWarnings("ComparatorCombinators")
    public static Array<JavaTransition> convertTransitions(JavaTrack track, Array<LegacyTransition> legacy, float initialValue, TransitionEase exit) {
        Array<JavaTransition> transitions = new Array<>();

        // No transitions, so we create one transition with just the initial value
        if (legacy.size < 1) {
            float finalInitialValue = initialValue;
            transitions.add(new JavaTransition() {{
                startTime = track.spawnTime;
                endTime = track.despawnTime;
                startValue = finalInitialValue;
                endValue = finalInitialValue;
                easing = TransitionEase.NONE;
            }});
            return transitions;
        }

        // Fix gaps from spawn to initial transition
        LegacyTransition transition = legacy.get(0);
        int start = NumberKt.toMillis(transition.Start);
        if (start != track.spawnTime) {
            float finalInitialValue = initialValue;
            transitions.add(new JavaTransition() {{
                startTime = track.spawnTime;
                endTime = start;
                startValue = finalInitialValue;
                endValue = finalInitialValue;
                easing = TransitionEase.NONE;
            }});
        }

        // Convert transitions
        for (int i = 0; i < legacy.size; i++) {
            transition = legacy.get(i);
            // If this is not the first legacy transition, we get the value from the previous transition
            if (i > 0)
                initialValue = transitions.get(transitions.size - 1).endValue;

            LegacyTransition finalTransition = transition;
            float finalInitialValue = initialValue;
            TransitionEase finalEase = parseEase(finalTransition.Ease);
            transitions.add(new JavaTransition() {{
                startTime = NumberKt.toMillis(finalTransition.Start);
                endTime = NumberKt.toMillis(finalTransition.End);
                startValue = finalInitialValue;
                endValue = finalTransition.To;
                easing = finalEase == TransitionEase.EXIT ? exit : finalEase;
            }});
        }

        transitions.sort((a, b) -> Integer.compare(a.startTime, b.startTime));
        return transitions;
    }

    private static final HashMap<String, TransitionEase> easings = new HashMap<String, TransitionEase>() {{
        put("easelinear", TransitionEase.LINEAR);
        put("easeinquad", TransitionEase.QUAD_IN);
        put("easeoutquad", TransitionEase.QUAD_OUT);
        put("easeinoutquad", TransitionEase.QUAD_INOUT);
        put("easeoutinquad", TransitionEase.QUAD_OUTIN);
        put("easeincirc", TransitionEase.CIRC_IN);
        put("easeoutcirc", TransitionEase.CIRC_OUT);
        put("easeinoutcirc", TransitionEase.CIRC_INOUT);
        put("easeoutincirc", TransitionEase.CIRC_OUTIN);
        put("easeinexpo", TransitionEase.EXP_IN);
        put("easeoutexpo", TransitionEase.EXP_OUT);
        put("easeinoutexpo", TransitionEase.EXP_INOUT);
        put("easeoutinexpo", TransitionEase.EXP_OUTIN);
        put("easeinback", TransitionEase.BACK_IN);
        put("easeoutback", TransitionEase.BACK_OUT);
        put("easeinoutback", TransitionEase.EXIT); // As parsed by editor
        put("easeoutinback", TransitionEase.BACK_OUTIN);
        put("easeintelastic", TransitionEase.ELASTIC_IN); // The 'T' in easeinTelastic is not a typo on my part
        put("easeoutelastic", TransitionEase.ELASTIC_OUT);
        put("easeinoutelastic", TransitionEase.ELASTIC_INOUT);
        put("easeoutinelastic", TransitionEase.ELASTIC_OUTIN);
    }};

    private static TransitionEase parseEase(String ease) {
        return easings.containsKey(ease) ? easings.get(ease) : TransitionEase.NONE;
    }

    // Legacy colors are stored as int that reference these values (meaning that legacy can't do RGB sadly)
    public static final String[] legacyColors = {
            "#F98F95", "#F9E5A1", "#D3D3D3", "#77D1DE", "#97D384", "#F3B67E", "#E2A0CB", "#8CBCE7", "#76DBCB", "#AEA6F0"
    };

    private static Color parseColor(int color) {
        return legacyColors.length - 1 >= color ? Color.valueOf(legacyColors[color]) : Color.WHITE;
    }

    private static final HashMap<String, NoteType> noteTypes = new HashMap<String, NoteType>() {{
        put("click", NoteType.CLICK);
        put("swipe", NoteType.SWIPE);
        put("hold", NoteType.HOLD);
        put("slide", NoteType.SLIDE);
    }};

    private static NoteType parseType(String type) {
        type = type.toLowerCase();
        return noteTypes.containsKey(type) ? noteTypes.get(type) : NoteType.CLICK;
    }

    // Legacy classes
    private static class LegacyTrack {
        public int id;
        public boolean EntranceOn;
        public float X;
        public float Size;
        public float Start;
        public float End;
        public int Color;

        public Array<LegacyTransition> Move;
        public Array<LegacyTransition> Scale;
        public Array<LegacyTransition> ColorChange;
    }

    private static class LegacyTransition {
        public float To;
        public String Ease;
        public float Start;
        public float End;
    }

    private static class LegacyNote {
        public int Id;
        public String Type;
        public int Track;
        public float Time;
        public float Hold;
        public int Dir;
    }

    // Current classes (that will be later converted to kotlin)
    private static class JavaTrack {
        public int id;
        public int spawnTime;
        public int spawnDuration;
        public int despawnTime;
        public int despawnDuration;

        public Array<JavaTransition> moveTransitions;
        public Array<JavaTransition> scaleTransitions;
        public Array<JavaColorTransition> colorTransitions;

        public Array<Note> notes;

        private Transition[] convertTransition(Array<JavaTransition> transitions) {
            Transition[] result = new Transition[transitions.size];

            for (int i = 0; i < transitions.size; i++) {
                JavaTransition transition = transitions.get(i);
                result[i] = new Transition(transition.easing, transition.startTime, transition.endTime, transition.startValue, transition.endValue);
            }

            return result;
        }

        private ColorTransition[] convertColorTransition(Array<JavaColorTransition> transitions) {
            ColorTransition[] result = new ColorTransition[transitions.size];

            for (int i = 0; i < transitions.size; i++) {
                JavaColorTransition transition = transitions.get(i);
                result[i] = new ColorTransition(transition.startTime, transition.endTime, transition.startValue, transition.endValue, transition.easing);
            }

            return result;
        }

        private Track convertTrack() {
            Note[] newNotes = new Note[notes.size];

            for (int i = 0; i < notes.size; i++)
                newNotes[i] = notes.get(i);

            return new Track(id, spawnTime, spawnDuration, despawnTime, despawnDuration, convertTransition(moveTransitions), convertTransition(scaleTransitions), convertColorTransition(colorTransitions), newNotes);
        }
    }

    private static class JavaTransition {
        public TransitionEase easing;
        public int startTime;
        public int endTime;
        public float startValue;
        public float endValue;
    }

    private static class JavaColorTransition {
        public TransitionEase easing;
        public int startTime;
        public int endTime;
        public Color startValue;
        public Color endValue;
    }
}
