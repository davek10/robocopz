package guiTest;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.awt.event.*;
import java.awt.*; 

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FireFly {
	/** Command to turn Accelerometer on */
	static final String ACCELONSTRING = "AccelOn";
	static final String ACCELOFFSTRING = "AccelOff";
	static final String VIBREONSTRING = "VibreOn";
	static final String VIBREOFFSTRING = "VibreOff";
	static final String CALIBRATESTRING = "Calibrate";
	/** Buffered input stream from the port */
	static private InputStream in;
	/** Output stream to the port */
	static private OutputStream out;
	
	    public FireFly()
	    {
	        super();
	    }
	    
	    void connect ( String portName ) throws Exception
	    {
	        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
	        if ( portIdentifier.isCurrentlyOwned() )
	        {
	            System.out.println("Error: Port is currently in use");
	        }
	        else
	        {
	            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
	            
	            if ( commPort instanceof SerialPort )
	            {
	                SerialPort serialPort = (SerialPort) commPort;
	                serialPort.setSerialPortParams(19200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
	                
	                in = serialPort.getInputStream();
	                out = serialPort.getOutputStream();
	                               
	                (new Thread(new SerialWriter(out))).start();
	                
	                serialPort.addEventListener(new SerialReader(in));
	                serialPort.notifyOnDataAvailable(true);
	                

	            }
	            else
	            {
	                System.out.println("Error: Das ist kein serieller Port!");
	            }
	        }     
	    }
	    
	    static void accelOn(){
	    	Boolean	sended = false;
	    	
	    if(!sended){
	    	try {
	    		
				out.write(ACCELONSTRING.getBytes());
				

	    	} catch (IOException e) {
	    		System.out.println("Fehler beim Senden");
			}
	    	
	    }
	    	sended =true;

	    }
	    
	    static void accelOff(){
	    	Boolean	sended = false;
	    	
		    if(!sended){
		    	try {
		    		
					out.write(ACCELOFFSTRING.getBytes());
					

		    	} catch (IOException e) {
		    		System.out.println("Fehler beim Senden");
				}
		    	
		    }
		    	sended =true;

		    }
	    
	    static void vibreOn(){
	    	Boolean	sended = false;
	    	
		    if(!sended){
		    	try {
		    		
					out.write(VIBREONSTRING.getBytes());
					

		    	} catch (IOException e) {
		    		System.out.println("Fehler beim Senden");
				}
		    	
		    }
		    	sended =true;

		    }
	    
	    static void vibreOff(){
	    	Boolean	sended = false;
	    	
		    if(!sended){
		    	try {
		    		
					out.write(VIBREOFFSTRING.getBytes());
					

		    	} catch (IOException e) {
		    		System.out.println("Fehler beim Senden");
				}
		    	
		    }
		    	sended =true;

		    }
	    
	    static void calibrate(){
	    	Boolean	sended = false;
	    	
		    if(!sended){
		    	try {
		    		
					out.write(CALIBRATESTRING.getBytes());
					

		    	} catch (IOException e) {
		    		System.out.println("Fehler beim Senden");
				}
		    	
		    }
		    	sended =true;

		    }
	    
	    /**
	     * Handles the keyboard input.
	     * @param: 'A' toggles accelerometer on and off
	     * @param: 'V' toggles vibro on and off
	     */
	    
	    public class KeyReader implements KeyListener{
	    	
	    private boolean accelIsOn=false;
		private boolean vibreIsOn=false;

		public void	keyPressed(KeyEvent e ){
	    	
	    		
	    	 	}
	    
	    public void keyTyped ( KeyEvent e ){  
	    	 
	    	 }  
	    	 
	    	  public void keyReleased ( KeyEvent e ){  
	    	
	    		  if(e.equals('A')){
	    			  if(!accelIsOn){
	    				  accelOn();
	    				  accelIsOn=true;
	    				  System.out.print(accelIsOn);
	    			  }else{
	    				  accelOff();
	    				  accelIsOn=false;
	    			  }
	  	    	}
	    		  if(e.equals('V')){
	    			  if(!vibreIsOn){
	    				  vibreOn();
	    				  vibreIsOn=true;
	    			  }else{
	    				  vibreOff();
	    				  vibreIsOn=false;
	    			  }
	  	    	}
	    	  } 
	    
	    
	    }
	    
	    /**
	     * Handles the input coming from the serial port. A new line character
	     * is treated as the end of a block. 
	     */
	    public static class SerialReader implements SerialPortEventListener 
	    {
	        private InputStream in;
	        private byte[] buffer = new byte[1024];
	        
	        public SerialReader ( InputStream in )
	        {
	            this.in = in;
	        }
	        
	        public void serialEvent(SerialPortEvent arg0) {
	            int data;
	            
	            //wenn daten kommen, dann schalte accel und vibro ein
	            
	            accelOn();
	            vibreOn();
	          
	            try
	            {
	                int len = 0;
	                while ( ( data = in.read()) > -1 )
	                {
	                	
	                    if ( data == '\n' ) {
	                        break;
	                    }
	                    buffer[len++] = (byte) data;
	                }
	                System.out.print(new String(buffer,0,len));
	            }
	            catch ( IOException e )
	            {
	                e.printStackTrace();
	                System.exit(-1);
	            }             
	        }

	    }

	    /** */
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

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		listPorts();
		 
		
		try
       {
           (new FireFly()).connect("COM6");

       }
       catch ( Exception e )
       {
           e.printStackTrace();
       }
		
	}	
	
	
	/** Auflistung aller installierten Com Ports */
	    static void listPorts()
	    {
	        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
	        while ( portEnum.hasMoreElements() ) 
	        {
	            CommPortIdentifier portIdentifier = portEnum.nextElement();
	            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
	        }        
	    }
	    
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

