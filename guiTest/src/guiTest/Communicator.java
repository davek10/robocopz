package guiTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.bluetooth.*; // Kanske? Eller?

/* Används på nätet men vi har INTE ANDROID JU!
 * import android.bluetooth.BluetoothAdapter;
 * import android.bluetooth.BluetoothDevice;
 * import android.bluetooth.BluetoothSocket;
 */

public class Communicator {
	private BluetoothAdapter btAdapter = null;
	private BluetoothDevice btDevice = null;
	private BluetoothSocket btSocket = null;
	private final InputStream btInStream;
	private final OutputStream btOutStream;

	// MAC-address of Bluetooth module (you must edit this line)
	private static String address = "00:15:FF:F2:19:5F";

	public static void main(String[] args) {
		bluetoothInit();
		byte[] robotInfo = new byte[25];
		byte ctrlDecision;
		while (true) {
			transmit(ctrlDecision);
			robotInfo = receive();
		}
	}
	public static void bluetoothInit() {
		// get Bluetooth adapter
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		// Set up a pointer to the remote node using its address.
		btDevice = btAdapter.getRemoteDevice(address);

		try {
			btSocket = createBluetoothSocket(device);
		} catch (IOException e) {
			//socket creation failed
		}
		try {
			btSocket.connect();
		} catch (IOException e) {

			try {
				btSocket.close();
			} catch (IOException e2) {

				//socket connection failed and unable to close socket during connection failure

			}

		}
		btInStream = btSocket.getInputStream();
		btOutStream = btSocket.getOutputStream();
	}


	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
		if(Build.VERSION.SDK_INT >= 10){ // va?
			try {
				final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				//Log.e(TAG, "Could not create Insecure RFComm Connection",e);
			}
		}
		return  device.createRfcommSocketToServiceRecord(MY_UUID);
	}

	public static void transmit(byte output) {
		try {
			btOutstream.write(output);
		} catch (IOException e) {
			//fail
		}
	}
	public static byte[] receive() {
		//ArrayList<Integer> buffer = new ArrayList<Integer>(25);
		byte[] input = new byte[25];
		int bytes;
		while (true) {
			try{
				bytes = btInstream.read(input); // Get number of bytes and message
			} catch (IOException e) {
				break;

			}
		}
		return input;
	}

}
