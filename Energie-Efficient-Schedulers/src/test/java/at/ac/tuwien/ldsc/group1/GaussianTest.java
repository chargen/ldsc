package at.ac.tuwien.ldsc.group1;

// Demonstrate random Gaussian values. 
import java.util.Random;

import org.junit.Test;
public class GaussianTest { 

	//TODO i think there is a mistake in the InputFilegenerator that is needs to be fixed
	//(Too much App with Timstamp 0 and 50000 will be generated)
	
	@Test
	public void normalDistributionExample() { 

		Random random = new Random(); 
		double val; 
		double sum = 0; 
		int bell[] = new int[10]; 
		for(int i=0; i<100; i++) { 
			val = random.nextGaussian(); 
			sum += val; 
			double t = -2; 
			for(int x=0; x<10; x++, t += 0.5) 
				if(val < t) { 
					bell[x]++; 
					break; 
				} 
		} 
		System.out.println("Average of values: " + 
				(sum/100)); 
		// display bell curve, sideways 
		for(int i=0; i<10; i++) { 
			for(int x=bell[i]; x>0; x--) 
				System.out.print("*"); 
			System.out.println(); 
		} 
} 
}