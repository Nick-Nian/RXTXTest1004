import java.util.Scanner;

public class RXTXTest {
	private Uart uart = null;
	private final String UART_PORT = "/dev/ttyUSB0";
	
	public static void main(String[] args)
	{
		//Uart.PortList(); // port list
		(new RXTXTest()).Run();
	}
	
	public void Run()
	{
		InitUart();
		ListenZPacket();
		// Transmit data
		Scanner scanner = new Scanner(System.in);
		String cmd = "";
		
		while (true)
		{
			cmd = scanner.next();
			
			//DF command => cmd = "44";
			//OB command => cmd = "4F";
			uart.Send(cmd);
			Sleep(1000);
		}
	}
	
	public void InitUart()
	{
		uart = new Uart();
		uart.Open(UART_PORT);
	}
	
	public void ListenZPacket() // ZPacket form ZigBee
	{
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{   
					
					// Receive data
					String rawPkt = uart.Recv();
					if (rawPkt.length() != 0)
					{
						System.out.println(rawPkt);
						//¤Á³Î¦r¦ê
						String[] msg = rawPkt.split(" ");
						if (msg[0].equals("DF")) {
						   System.out.println("Url:"+msg[1]);
						   System.out.println("IEEE Address:"+msg[2]);
						   System.out.println("Short Address:"+msg[3]);
						}
						else if (msg[0].equals("OB")) {
							System.out.println("Url:"+msg[1]);
							System.out.println("IEEE Address:"+msg[2]);
							System.out.println("Data type:"+msg[3]);  
						    System.out.println("Value:"+msg[4]);
						}
					}
					Sleep(100);
				}
			}
		});
		t.start();
	}
	
	public void Sleep(int ms)
	{
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {}
	}
}
