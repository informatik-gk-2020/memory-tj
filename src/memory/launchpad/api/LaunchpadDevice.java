package memory.launchpad.api;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LaunchpadDevice {
    private static final byte[] prefix = new byte[]{(byte) 0xF0, 0x00, 0x20, 0x29, 0x02, 0x10};
    private static final int messageStart = prefix.length + 1;
    private static final byte suffix = (byte) 0xF7;
    private static final byte textScrollCommand = 0x14;
    private static final byte singleLightCommand = 0x0A;
    private static final byte columnLightCommand = 0x0C;
    private static final byte rowLightCommand = 0x0D;
    private static final byte allLightCommand = 0x0E;
    private static final byte flashLightCommand = 0x23;
    private static final byte pulseLightCommand = 0x28;
    private static final byte singleLightRgbCommand = 0x0B;
    private static final byte gridLightRgbCommand = 0x0F;

    private final SysexMessage cachedMessage = new SysexMessage();
    private final byte[] data = new byte[prefix.length + 1 /* command */ + 301 /* max length of message (RGB grid) */ + 1 /* suffix */];
    private Receiver receiver = null;

    public LaunchpadDevice() {
        System.arraycopy(prefix, 0, data, 0, prefix.length);
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setSingleLight(int x, int y, byte color) {
        sendColorData(singleLightCommand, (byte) getLightIndex(x, y), color);
    }

    public void setSideLight(byte color) {
        sendColorData(singleLightCommand, (byte) 99, color);
    }

    public void setColumnLight(int x, byte[] colors) {
        if (colors.length > 10)
            throw new IllegalArgumentException("There are only 10 light in a column");

        data[messageStart] = (byte) x;
        System.arraycopy(colors, 0, data, messageStart + 1, colors.length);
        sendData(columnLightCommand, colors.length + 1);
    }

    public void setRowLight(int y, byte[] colors) {
        if (colors.length > 10)
            throw new IllegalArgumentException("There are only 10 light in a row");

        data[messageStart] = (byte) y;
        System.arraycopy(colors, 0, data, messageStart + 1, colors.length);
        sendData(rowLightCommand, colors.length + 1);
    }

    public void setAllLight(byte color) {
        data[messageStart] = color;
        sendData(allLightCommand, 1);
    }

    public void flashLight(int x, int y, byte color) {
        sendColorData(flashLightCommand, (byte) getLightIndex(x, y), color);
    }

    public void flashSideLight(byte color) {
        sendColorData(flashLightCommand, (byte) 99, color);
    }

    public void pulseLight(int x, int y, byte color) {
        sendColorData(pulseLightCommand, (byte) getLightIndex(x, y), color);
    }

    public void pulseSideLight(byte color) {
        sendColorData(pulseLightCommand, (byte) 99, color);
    }

    public void setLightRgb(int x, int y, byte red, byte green, byte blue) {
        setLightRgb((byte) getLightIndex(x, y), red, green, blue);
    }

    public void setSideLightRgb(byte red, byte green, byte blue) {
        setLightRgb((byte) 99, red, green, blue);
    }

    public void setGridLightRgb(byte[] colorData, int indexCount, boolean fullGrid) {
        if (colorData.length > 300)
            throw new IllegalArgumentException();

        if (indexCount == -1)
            indexCount = colorData.length / 3;

        var colorsSize = indexCount * 3;

        data[messageStart] = fullGrid ? (byte) 0 : (byte) 1;
        System.arraycopy(colorData, 0, data, messageStart + 1, colorsSize);
        sendData(gridLightRgbCommand, colorsSize + 1);
    }

    private void setLightRgb(byte index, byte red, byte green, byte blue) {
        data[messageStart] = index;
        data[messageStart + 1] = red;
        data[messageStart + 2] = green;
        data[messageStart + 3] = blue;
        sendData(singleLightRgbCommand, 4);
    }

    public void startTextScrolling(String text, boolean loop /* false */, byte color /* 3 */) {
        if (receiver == null)
            return;

        var textBytes = StandardCharsets.US_ASCII.encode(text);

        var buffer = ByteBuffer.allocate(prefix.length + 1 + 1 + 1 + textBytes.remaining() + 1);
        buffer.put(prefix);
        buffer.put(textScrollCommand);
        buffer.put(color);
        buffer.put(loop ? (byte) 0 : (byte) 1);
        buffer.put(textBytes);
        buffer.put(suffix);
        buffer.flip();

        var data = new byte[buffer.remaining()];
        buffer.get(data);

        try {
            cachedMessage.setMessage(data, data.length);
            receiver.send(cachedMessage, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    void stopTextScrolling() {
        sendData(textScrollCommand, 0);
    }

    private void sendColorData(byte command, byte position, byte color) {
        data[messageStart] = position;
        data[messageStart + 1] = color;
        sendData(command, 2);
    }

    private void sendData(byte command, int length) {
        if (receiver == null)
            return;

        data[prefix.length] = command;
        data[messageStart + length] = suffix;

        try {
            cachedMessage.setMessage(data, messageStart + length + 1);
            receiver.send(cachedMessage, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public static int getLightIndex(int x, int y) {
        return (9 - y) * 10 + x;
    }
}