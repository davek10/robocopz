package guiTest;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
 

public class FireFly {

	/** Buffered input stream from the port */
	static private InputStream in;
	/** Output stream to the port */
	static private OutputStream out;

	public FireFly()
	{
		super();
		
		EventQueue.invokeLater(new Runnable() {
			// Run method for the frame
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	/*
	 * For connecting to the FireFly module, throws exception if connection failed. 
	 */
	void connect ( String portName ) throws Exception
	{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if ( portIdentifier.isCurrentlyOwned() )
		{
			System.out.println("Error: Port is currently in use");
		}
		else
		{
			int timeout = 2000;
			CommPort commPort = portIdentifier.open(this.getClass().getName(),timeout);

			if ( commPort instanceof SerialPort )
			{
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();

				//(new Thread(new SerialReader(in))).start();
                serialPort.addEventListener(new SerialReader(in));
                serialPort.notifyOnDataAvailable(true);
				(new Thread(new SerialWriter(out))).start();	    
			}
			else
			{
				System.out.println("Error: We can only use serial ports");
			}
		}     
	}
	/*
	 * Used for sending data to the robot
	 */
	static void toRobot(byte data){
		try{
			System.out.println("Sent: " + data);
			out.write(data);
		} catch (IOException e){
			System.out.println("Unable to send bytes: " + data);
		}
	}

	/*
	 * Reads from the Bluetooth device using event listener
	 */
	public static class SerialReader implements SerialPortEventListener 
    {
		
        private InputStream in;
        private byte[] buffer = new byte[25];
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void serialEvent(SerialPortEvent arg0) {
            int data;
			System.out.println("Event");
            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )
                {
                    buffer[len++] = (byte) data;
                }
                for (int i=0; i<25; i++) {
                System.out.print(buffer[i] + " ");
                }
                System.out.println();
                GUI.update(buffer);
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }             
        }

    }
	/*
	 *  Writes to the robot via Bluetooth, using threads. 
	 */
	public static class SerialWriter implements Runnable 
	{
		OutputStream out;

		public SerialWriter ( OutputStream out )
		{
			this.out = out;
		}

		public void run ()
		{
			try
			{                
				int c = 0;
				while ( ( c = System.in.read()) > -1 )
				{
					this.out.write(c);
				}                
			}
			catch ( IOException e )
			{
				e.printStackTrace();
				System.exit(-1);
			}            
		}
	}

	/*
	 * Connects to the FireFly unit. 
	 */
	public static void main(String[] args) {		
		//listPorts(); // Don't need to list the ports as of now

		try
		{
			(new FireFly()).connect("COM40"); // Port number connected to FireFly module
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

	}	

	/*
	 *  Prints every port found.
	 */
	static void listPorts()
	{
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while ( portEnum.hasMoreElements() ) 
		{
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
		}        
	}

	/*
	 *  Different types of ports, we only use serial. 
	 */
	static String getPortTypeName ( int portType )
	{
		switch ( portType )
		{
		case CommPortIdentifier.PORT_I2C:
			return "I2C";
		case CommPortIdentifier.PORT_PARALLEL:
			return "Parallel";
		case CommPortIdentifier.PORT_RAW:
			return "Raw";
		case CommPortIdentifier.PORT_RS485:
			return "RS485";
		case CommPortIdentifier.PORT_SERIAL:
			return "Serial";
		default:
			return "unknown type";
		}

	}

}


