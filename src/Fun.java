/***** utility funciton *****/

public class Fun {

	public static String DecToHex(int nDec, int words)
	{
		String sHex = Integer.toHexString(nDec);
		String zero = "";
		
		sHex = sHex.toUpperCase();
		if (sHex.length() < words)
			for (int i = 0; i < (words-sHex.length()); i++)
				zero += "0";
		
		return zero + sHex;
	}
	
	public static String DecToHex(int[] nDec, int bits)
	{
		String sHex = "";
		for (int i = 0; i < bits; i++)
		{
			String hex = Integer.toHexString(nDec[i]);
			hex = hex.toUpperCase();
			if (hex.length() == 1) sHex += "0";
			sHex += hex;
		}
		return sHex;
	}

	public static int[] HexToDec(String sHex)
	{
		sHex = sHex.replaceAll(" ", "");
		int[] nDec = new int[sHex.length()/2];
		for (int i = 0; i < nDec.length; i++)
		{
			nDec[i] = Integer.parseInt(sHex.substring(i*2, i*2+2), 16);
		}
		return nDec;
	}
		
	public static byte[] HexToDecByte(String sHex)
	{
		sHex = sHex.replaceAll(" ", "");
		byte[] bDec = new byte[sHex.length()/2];
		for (int i = 0; i < bDec.length; i++)
		{
			bDec[i] = (byte)Integer.parseInt(sHex.substring(i*2, i*2+2), 16);
		}
		return bDec;
	}
	
	public static String PrintHex(String sHex)
	{
		sHex = sHex.replaceAll(" ", "");
		String n_sHex = "";
		for (int i = 0; i < sHex.length(); i+=2)
		{
			n_sHex += sHex.substring(i, i+2);
			if (i < sHex.length()-2) n_sHex += " ";
		}
		return n_sHex;
	}
}