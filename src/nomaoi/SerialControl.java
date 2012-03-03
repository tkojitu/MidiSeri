package nomaoi;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

public class SerialControl implements AutoCloseable {
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;
    private static final String PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
    };

    SerialPort serialPort;
    private OutputStream output;
    private ByteArrayOutputStream bout = new ByteArrayOutputStream();
    private OutputStreamWriter writer = new OutputStreamWriter(bout);
    private StringBuilder builder = new StringBuilder();

    public void setup() throws IOException {
        CommPortIdentifier portId = findPortId();
        if (portId == null) {
            throw new IOException("Could not find COM port.");
        }
        try {
            serialPort = (SerialPort)portId.open(this.getClass().getName(), TIME_OUT);
            serialPort.setSerialPortParams(DATA_RATE,
                                           SerialPort.DATABITS_8,
                                           SerialPort.STOPBITS_1,
                                           SerialPort.PARITY_NONE);
            output = serialPort.getOutputStream();
        } catch (PortInUseException
                 | UnsupportedCommOperationException
                 | IOException e) {
            System.err.println(e.toString());
        }
    }

    private CommPortIdentifier findPortId() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier)portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        return portId;
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public void println(int value) {
        System.out.println(value);
        try {
            builder.append(value);
            builder.append('\n');
            String command = builder.toString();
            writer.write(command, 0, command.length());
            writer.flush();
            bout.writeTo(output);
            bout.reset();
            builder.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        if (serialPort == null) {
            return;
        }
        serialPort.close();
    }
}
