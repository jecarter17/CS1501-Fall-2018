public class PowerTest{

	static int power(int x, int y, int p){ 
        // Initialize result 
        int res = 1;      
         
        // Update x if it is more   
        // than or equal to p 
        x = x % p;
		System.out.println("x = "+x);
      
        while (y > 0) 
        { 
            // If y is odd, multiply x 
            // with result 
            if((y & 1)==1){
                res = (res * x) % p;
				System.out.println("result = "+res);
			}
      
            // y must be even now 
            // y = y / 2 
            y = y >> 1;
			System.out.println("y = "+y);
            x = (x * x) % p;
			System.out.println("x = "+x);
			System.out.println();
        } 
        return res; 
    } 
  
    // Driver Program to test above functions 
    public static void main(String args[]){ 
        int x = 118; 
        int y = 72; 
        int p = 23; 
        System.out.println("Power is " + power(x, y, p)); 
    } 
} 