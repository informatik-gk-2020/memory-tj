package memory.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import memory.launchpad.api.Launchpad;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class LaunchpadSelectionDialog extends Dialog<ButtonType> {
    private final ObservableList<MidiDevice.Info> devices = FXCollections.observableArrayList(MidiSystem.getMidiDeviceInfo());

    private final ObservableList<MidiDevice.Info> inputDevices = devices.filtered(info -> {
        try (var device = MidiSystem.getMidiDevice(info)) {
            return device.getMaxTransmitters() != 0;
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        return false;
    });

    private final ObservableList<MidiDevice.Info> outputDevices = devices.filtered(info -> {
        try (var device = MidiSystem.getMidiDevice(info)) {
            return device.getMaxReceivers() != 0;
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        return false;
    });

    private final SimpleObjectProperty<MidiDevice.Info> inputDevice = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<MidiDevice.Info> outputDevice = new SimpleObjectProperty<>();

    public LaunchpadSelectionDialog() {
        var content = new GridPane();
        content.setHgap(16);
        content.setVgap(8);

        var inputBox = new ComboBox<>(inputDevices);
        inputDevice.bind(inputBox.getSelectionModel().selectedItemProperty());
        content.addRow(0, new Label("Eingang: "), inputBox);

        var outputBox = new ComboBox<>(outputDevices);
        outputDevice.bind(outputBox.getSelectionModel().selectedItemProperty());
        content.addRow(1, new Label("Ausgang: "), outputBox);

        var dialogPane = getDialogPane();
        dialogPane.setContent(content);
        dialogPane.setHeaderText("Launchpad Ein- und Ausgang wählen");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setTitle("Launchpad");
    }

    public void apply(Launchpad launchpad) {
        try {
            MidiDevice.Info input = inputDevice.get();
            if (launchpad.getInput() == null || !launchpad.getInput().getDeviceInfo().equals(input)) {
                launchpad.setInput(input == null ? null : MidiSystem.getMidiDevice(input));
            }

            MidiDevice.Info output = outputDevice.get();
            if (launchpad.getOutput() == null || !launchpad.getOutput().getDeviceInfo().equals(output)) {
                launchpad.setOutput(output == null ? null : MidiSystem.getMidiDevice(output));
            }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }
}
