import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Uart {

	private CommPortIdentifier portIdentifier = null;
	private InputStream in = null;
	private OutputStream out = null;
	private SerialPort serialPort = null;
	
	public static void PortList()
	{
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		while (ports.hasMoreElements())
		{
			CommPortIdentifier cpIdentifier = (CommPortIdentifier)ports.nextElement();
			System.out.println(cpIdentifier.getName());
		}
	}
	
	public void Open(String portName)
	{
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

			if (portIdentifier.isCurrentlyOwned())
			{
				System.out.println("Port in use!");
			}
			else
			{
				// points who owns the port and connection timeout
				serialPort = (SerialPort) portIdentifier.open("UART", 2000);

				// setup connection parameters
				serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				// setup serial port reader and writer
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
				
				System.out.println("RXTX start");
			}
		} 
		catch (NoSuchPortException e)
		{
			e.printStackTrace();
		} 
		catch (PortInUseException e)
		{
			e.printStackTrace();
		} 
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void Send(byte[] bHex)
	{ 
       try {
            out.write(bHex);
            out.flush();
        }
       	catch (IOException e)
        {
          e.printStackTrace(); 
        } 	
    }
	
	public void Send(String sHex)
	{
		try {
			out.write(Fun.HexToDecByte(sHex));
            out.flush();
            System.out.println("UART-Tx: "+Fun.PrintHex(sHex));
		}
		catch (IOException e)
		{
			e.printStackTrace();
	    }
	}
	public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
	public String Recv()
	{
		String sHex = "";
		String rString = "";
		String urlString ="";
		String dataTypeString = "";

		try {
            int[] code = new int[256];
			int[] endcode = new int[2];
            int bits = 0;
            int urllen = 0;
            char [] url = new char[256];
            int[] IEEEAddr = new int[8];
            int[] shortAddr = new int[2];
            char[] dataType = new char[5];
            int[] lightValueBuf = new int[3];
            double lightValue=0;
           
            
            
            
            // if stream is not bound in.read() method returns -1 in Windows, but it does not return -1 in Ubuntu
            while((code[bits] = in.read()) != -1)
			{
            	bits++;
				if (bits > 2 && code[bits-2] == 170 && code[bits-1] == 254) break;
            }
            //計算出url長度
            
            /*----------------------------------------*/
            //Here we deal with the DF message
            /*----------------------------------------*/
            
            if (code[0] == 'D' && code[1] == 'F')
            {	
            //把收到的資訊由INT 轉成 BYTE
            //original i = 0
               for (int i=0; i<bits; i++)
               {
     
            	 url[i] = (char) (code[i+3]);
            	 if (url[i] == 32) {
            	    urllen = i;
            	    for (int j=0; j<8; j++) {
            		    IEEEAddr[j] = code[i+1+j+3];
            	    }
            	    for (int j=0; j<2; j++) {
            	 	   shortAddr[j] = code[i+2+8+3+j];
            	    }
            	    break;
            	 }
            	
                }
               if (bits != 0)
               {
               	// the size of url[] is fixed to 256, so we have to create an array url2 whose length equal to 'urllen'.
            	   // in this way, we can use String.valueOf() to turn the char array into String...
            	   char [] url2 = new char[urllen];
                   for (int i=0; i<urllen; i++)
                    	url2[i] = url[i];
            	   urlString = String.valueOf(url2);
            	  
            	//sHex = Fun.DecToHex(code, bits);       	
                   rString = "DF " + urlString +" "+Fun.DecToHex(IEEEAddr, 8) +" "+ Fun.DecToHex(shortAddr, 2) ; 
                   //System.out.println("in Uart:"+rString);
                //System.out.println("urlString length ="+ urlString.length());
            	   bits = 0;
               }
            }    
            else if (code[0] == 'O' && code[1] == 'B') {
            	 /*----------------------------------------*/
                //Here we deal with the OB message
                /*----------------------------------------*/
            	for (int i=0; i<bits; i++)
                {
      
             	 url[i] = (char) (code[i+3]);
             	 if (url[i] == 32) {
             	    urllen = i;
             	    for (int j=0; j<8; j++) {
             		    IEEEAddr[j] = code[i+1+j+3];
             	    }
             	    for (int j=0; j<5; j++) { //Length if data type like "Light", "Temperature" doesnt over 12
             	 	   dataType[j] = (char)code[i+2+8+3+j];
             	    }
             	   dataTypeString = String.valueOf(dataType);
             	    for (int j=0; j<3; j++) {
             	    	lightValueBuf[j] = code[i+2+8+3+6+j];
             	    }
             	    break;
             	 }
             	
                 }
                if (bits != 0)
                {
                	// the size of url[] is fixed to 256, so we have to create an array url2 whose length equal to 'urllen'.
             	   // in this way, we can use String.valueOf() to turn the char array into String...
             	   char [] url2 = new char[urllen];
                    for (int i=0; i<urllen; i++)
                     	url2[i] = url[i];
             	   urlString = String.valueOf(url2);
             	   lightValue = ((((double)lightValueBuf[1]*16*16 + (double)lightValueBuf[2])/4)/4096)*6250*1.5;
             	//sHex = Fun.DecToHex(code, bits);       	
             	   
                    rString = "OB " + urlString +" "+Fun.DecToHex(IEEEAddr, 8) +" "+dataTypeString+" "+ lightValue ; 
                    //System.out.println("in Uart:"+rString);
                 //System.out.println("urlString length ="+ urlString.length());
             	   bits = 0;
                }
            }
                 
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
		//return sHex;
		return rString;
	}
	
	public void Close()
	{
		try {
			in.close();
			out.close();
			serialPort.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}