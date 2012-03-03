package nomaoi;

import java.io.IOException;
import javax.sound.midi.MidiUnavailableException;

public class MidiSeri implements AutoCloseable, ToneListener {
    private SerialControl serial = new SerialControl();
    private MidiInControl midi = new MidiInControl();

    public void setup() throws IOException, MidiUnavailableException {
        serial.setup();
        midi.setup(new ToneReceiver(this));
    }

    @Override
    public void tone(double hz) {
        serial.println((int)hz);
    }

    @Override
    public synchronized void close() {
        serial.close();
    }

    public static void main(String[] args) throws Exception {
        MidiSeri app = new MidiSeri();
        app.setup();
    }
}
