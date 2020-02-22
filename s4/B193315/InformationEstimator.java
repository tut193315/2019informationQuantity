package s4.B193315; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID. 
import java.lang.*;
import s4.specification.*;

/* What is imported from s4.specification
package s4.specification;
public interface InformationEstimatorInterface{
    void setTarget(byte target[]); // set the data for computing the information quantities
    void setSpace(byte space[]); // set data for sample space to computer probability
    double estimation(); // It returns 0.0 when the target is not set or Target's length is zero;
// It returns Double.MAX_VALUE, when the true value is infinite, or space is not set.
// The behavior is undefined, if the true value is finete but larger than Double.MAX_VALUE.
// Note that this happens only when the space is unreasonably large. We will encounter other problem anyway.
// Otherwise, estimation of information quantity, 
}                        
*/

public class InformationEstimator implements InformationEstimatorInterface{
    // Code to tet, *warning: This code condtains intentional problem*
    byte [] myTarget; // data to compute its information quantity
    byte [] mySpace;  // Sample space to compute the probability
    FrequencerInterface myFrequencer; // Object for counting frequency
    
    byte [] subBytes(byte [] x, int start, int end) {
	// corresponding to substring of String for  byte[] ,
	// It is not implement in class library because internal structure of byte[] requires copy.
    	byte [] result = new byte[end - start];
    	for(int i = 0; i<end - start; i++) { 
    		result[i] = x[start + i]; 
    	};
    	return result;
    }

    // IQ: information quantity for a count,  -log2(count/sizeof(space))
    double iq(int freq) {
		return  - Math.log10((double) freq / (double) mySpace.length)/ Math.log10((double) 2.0);
    }

    public void setTarget(byte [] target) { 
    	myTarget = target;
    }
    public void setSpace(byte []space) { 
    	myFrequencer = new Frequencer();
    	mySpace = space; 
    	myFrequencer.setSpace(space); 
    }

    public double estimation(){
    	if (mySpace == null || mySpace.length <= 0) {
    		return Double.MAX_VALUE;
    	}
    	if (myTarget == null || myTarget.length <= 0) {
    		return 0.0;
    	}
		double value;		
		double[][]  dp = new double[myTarget.length][myTarget.length];  // DPテーブル 
		// DP初期条件
		for(int j = 0; j < myTarget.length; j++){
			myFrequencer.setTarget(subBytes(myTarget,j,j+1));
			dp[0][j] = iq(myFrequencer.frequency());
		}
		// DPループ
		for(int i = 1; i < myTarget.length; i++){
			value = Double.MAX_VALUE;
			for(int j = 0; j < myTarget.length-i; j++){
				myFrequencer.setTarget(subBytes(myTarget,j,j+i+1));
				value = iq(myFrequencer.frequency());
				if(i == 1){
					dp[i][j] = Math.min(value,dp[i-1][j]+dp[i-1][j+1]);
				}
				else {
					for(int k = 0; k < myTarget.length-1; k++){
						value = Math.min(value, dp[k][0]+dp[myTarget.length-(2+k)][k+1]);
					}
					dp[i][j] = value;
				}
			}
		}
		return dp[myTarget.length-1][0];
	}

    public static void main(String[] args) {
	InformationEstimator myObject;
	double value;
	myObject = new InformationEstimator();
	myObject.setSpace("3210321001230123".getBytes());
	myObject.setTarget("0".getBytes());
	value = myObject.estimation();
	System.out.println(">0 "+value);
	myObject.setTarget("01".getBytes());
	value = myObject.estimation();
	System.out.println(">01 "+value);
	myObject.setTarget("0123".getBytes());
	value = myObject.estimation();
	System.out.println(">0123 "+value);
	myObject.setTarget("00".getBytes());
	value = myObject.estimation();
	System.out.println(">00 "+value);
    }
}
