package memory.launchpad.api;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launchpad {
    private static final Timer timer = new Timer("Launchpad updater", true);

    private final LaunchpadDevice device = new LaunchpadDevice();
    private final LaunchpadReceiver launchpadReceiver = new LaunchpadReceiver();

    private MidiDevice input;
    private MidiDevice output;

    private LaunchpadBuffer buffer;

    private AtomicBoolean updateRequested = new AtomicBoolean(false);
    private volatile long lastUpdate = -1L;

    public MidiDevice getInput() {
        return input;
    }

    public void setInput(MidiDevice input) throws MidiUnavailableException {
        if (this.input != null) {
            this.input.getTransmitter().setReceiver(null);
            this.input.close();
        }
        this.input = input;

        if (input != null) {
            input.getTransmitter().setReceiver(launchpadReceiver);
            input.open();
        }
    }

    public MidiDevice getOutput() {
        return output;
    }

    public void setOutput(MidiDevice output) throws MidiUnavailableException {
        if (this.output != null)
            this.output.close();
        this.output = output;

        if (output != null) {
            output.open();
            device.setReceiver(output.getReceiver());
            requestUpdate();
        } else {
            device.setReceiver(null);
        }
    }

    public LaunchpadButtonListener getButtonListener() {
        return launchpadReceiver.getButtonListener();
    }

    public void setButtonListener(LaunchpadButtonListener listener) {
        launchpadReceiver.setButtonListener(listener);
    }

    public LaunchpadBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(LaunchpadBuffer buffer) {
        if (this.buffer == buffer)
            return;

        if (this.buffer != null)
            this.buffer.setLaunchpad(null);

        this.buffer = buffer;
        if (buffer != null) {
            buffer.setLaunchpad(this);
            requestUpdate();
        }
    }

    void requestUpdate() {
        if (!updateRequested.getAndSet(true)) {
            var task = new TimerTask() {
                @Override
                public void run() {
                    updateRequested.set(false);
                    device.setGridLightRgb(buffer.getColorValues(), -1, true);
                    lastUpdate = System.nanoTime();
                }
            };

            var delay = 1000 / 30 - (System.nanoTime() - lastUpdate) / 1_000_000;
            if (delay > 0)
                timer.schedule(task, delay);
            else
                timer.schedule(task, 0);
        }
    }

    public void close() {
        try {
            setInput(null);
            setOutput(null);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }
}
