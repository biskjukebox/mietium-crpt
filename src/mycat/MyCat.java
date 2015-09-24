import java.io.IOException;

class MyCat
{
	public static void main(String[] args) throws java.io.IOException 
	{
		int i;
		do 
		{
			System.out.write(i = System.in.read());
	        }
		while (i != -1);
    	}
}
