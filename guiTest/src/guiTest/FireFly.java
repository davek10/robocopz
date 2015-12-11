package guiTest;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
 

public class FireFly {

	/** Buffered input stream from the port */
	static private InputStream in;
	/** Output stream to the port */
	static private OutputStream out;
	/** If we want to print Input/Output set this to true*/
	static private boolean printIO = false;

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
	 * Used for sending instruction data to the robot
	 */
	static void instrToRobot(int data){
		try{
			if(printIO){
				System.out.println("Sent: " + data);
			}
			// Send first info of how many bytes we are sending
			out.write(1);
			// Then send the data (1 byte)
			out.write(data);
		} catch (IOException e){
			System.out.println("Unable to send bytes: " + data);
		} catch (NullPointerException e){
			System.out.println("Robot not connected");
		}
	
	}
	/*
	 * Used for sending control params to the robot
	 */
	public static void paramsToRobot(ArrayList<Integer> readList) {
		try{
			// Send first info of how many bytes we are sending
			out.write(readList.size());
			// Then send the data 
			for(Integer i : readList){
				out.write(i);
			}
		} catch (IOException e){
			System.out.print("Unable to send bytes: ");
			for(Integer i : readList){
				System.out.print("," + i );
			}
		}
	}


	/*
	 * Reads from the Bluetooth device using event listener
	 */
	public static class SerialReader implements SerialPortEventListener 
    {
		
        private InputStream in;
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void serialEvent(SerialPortEvent arg0) {
            int data;
            try
            {
                int len = 0;
                data = in.read();
                int[] buffer = new int[data];
                if(data > -1){
                	while ( ( data = in.read()) > -1 )
                    {
                        buffer[len++] = data;
                    }	
                }
                
                if(printIO){
                	for (int i=0; i < buffer.length; i++) {
                		System.out.print(buffer[i] + " ");
                	}
                	System.out.println();
                }
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


