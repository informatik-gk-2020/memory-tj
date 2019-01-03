package memory.launchpad.api;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class LaunchpadReceiver implements Receiver {
    private LaunchpadButtonListener buttonListener;

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (buttonListener == null)
            return;

        if (message instanceof ShortMessage) {
            var shortMessage = (ShortMessage) message;
            var data1 = shortMessage.getData1();
            var data2 = shortMessage.getData2();

            var x = data1 % 10;
            var y = 9 - (data1 - x) / 10;

            switch (shortMessage.getCommand()) {
                case ShortMessage.NOTE_ON:
                case ShortMessage.CONTROL_CHANGE:
                    if (data2 > 0)
                        buttonListener.buttonPressed(x, y, data2);
                    else
                        buttonListener.buttonReleased(x, y);
                    break;
                case ShortMessage.NOTE_OFF:
                    buttonListener.buttonReleased(x, y);
                    break;
                case ShortMessage.CHANNEL_PRESSURE:
                    buttonListener.buttonPressure(0, 0, data1);
                    break;
                case ShortMessage.POLY_PRESSURE:
                    buttonListener.buttonPressure(x, y, data2);
                    break;
            }
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    public LaunchpadButtonListener getButtonListener() {
        return buttonListener;
    }

    public void setButtonListener(LaunchpadButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }
}
