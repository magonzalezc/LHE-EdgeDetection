
public class Cache {
	
	public static int[][][][] pccr; //precomputed color component
	
	public void init() {
		initPrecomputedColorComponent();
	}

	public void initPrecomputedColorComponent()
	{
		//This is the function to initialize pre-computed hop values
		System.out.println(" initializing LHE pre-computed hops");
		
		// This is a cache of ratio ("r") to avoid pow functions
		float[][][][] cache_ratio; //meaning : [+/-][h1][h0][rmax]
		// we will compute cache ratio for different rmax values, although we will use 
		// finally only rmax=25 (which means 2.5f). This function is generic and experimental
		// and this is the cause to compute more things than needed.

		
		// h1 value belongs to [4..10]
		// Given a certain h1 value, and certaing h0 luminance, the "luminance hop" of hop "i" is stored here:
		// hn[absolute h1 value][luminance of h0 value]
		// for example,  h4 (null hop) is always 0, h1 is always hop1 (from 4 to 10), h2 is hop1*r,
		// however this is only the hop. the final luminance of h2 is luminace=(luminance of h0)+hop1*r    
		// hn is, therefore, the array of "hops" in terms of luminance but not the final luminances.
		float[][] h0,h1,h2,h6,h7,h8;// h0,h1,h2 are negative hops. h6,h7,h8 are possitive hops
		
		int h1range=20;//in fact h1range is only from 4 to 10. However i am going to fill more possible values in the pre-computed hops
		
		
		h0=new float[h1range][256];
		h1=new float[h1range][256];
		h2=new float[h1range][256];
		// in the center is located h3=h4-hop1, and h5=h4+hop1, but I dont need array for them
		h6=new float[h1range][256];
		h7=new float[h1range][256];
		h8=new float[h1range][256];
		
		
		// pccr is the value of the REAL cache. This is the cache to be used in the LHE quantizer
		// sorry...i dont remenber why this cache is named "pccr" :) instead of "cache"
		// this array takes into account the ratio
		// meaning: pccr [h1][luminance][ratio][hop_index]
		pccr=new int[h1range][256][50][9];
		
		//cache of ratios ( to avoid Math.pow operation)
		//---------------------------------------------
		cache_ratio=new float[2][h1range][256][50];
		
		
		for (int hop0=0;hop0<=255;hop0++) {
			for (int hop1=1;hop1<h1range;hop1++)
			{
				float percent_range=0.8f;//0.8 is the  80%
				
				//this bucle allows computations for different values of rmax from 20 to 40. 
				//however, finally only one value (25) is used in LHE
				for (int rmax=20;rmax<=40;rmax++)
				{
				// r values for possitive hops	
				cache_ratio[0][(int)(hop1)][hop0][rmax]=(float)Math.pow(percent_range*(255-hop0)/(hop1), 1f/3f);
				
				// r' values for negative hops
				cache_ratio[1][(int)(hop1)][hop0][rmax]=(float)Math.pow(percent_range*(hop0)/(hop1), 1f/3f);
				
				// control of limits
				float max=(float)rmax/10f;// if rmax is 25 then max is 2.5f;
				if (cache_ratio[0][(int)(hop1)][hop0][rmax]>max)cache_ratio[0][hop1][hop0][rmax]=max;
				if (cache_ratio[1][(int)(hop1)][hop0][rmax]>max)cache_ratio[1][hop1][hop0][rmax]=max;
				}
			}
			
			//assignment of precomputed hop values, for each ofp value
			//--------------------------------------------------------
			for (int hop1=1;hop1<h1range;hop1++)
			{
				//finally we will only use one value of rmax rmax=30, 
				//however we compute from r=2.0f (rmax=20) to r=4.0f (rmax=40)
				for (int rmax=20;rmax<=40;rmax++)
				{
			        //get r value for possitive hops from cache_ratio	
				float ratio_pos=cache_ratio[0][hop1][hop0][rmax];
				
				//get r' value for negative hops from cache_ratio
				float ratio_neg=cache_ratio[1][hop1][hop0][rmax];

				// COMPUTATION OF LUMINANCES:
				// luminance of possitive hops
				h6[hop1][hop0] = hop1*ratio_pos;
				h7[hop1][hop0] = h6[hop1][hop0]*ratio_pos;
				h8[hop1][hop0] = h7[hop1][hop0]*ratio_pos;

				//luminance of negative hops	                        
				h2[hop1][hop0] =hop1*ratio_neg;
				h1[hop1][hop0] = h2[hop1][hop0]*ratio_neg;
				h0[hop1][hop0] = h1[hop1][hop0]*ratio_neg;

				
				//final color component ( luminance or chrominance). depends on hop1
				//from most negative hop (pccr[hop1][hop0][0]) to most possitive hop (pccr[hop1][hop0][8])
				//--------------------------------------------------------------------------------------
				pccr[hop1][hop0][rmax][0]= hop0  - (int) h0[hop1][hop0] ; if (pccr[hop1][hop0][rmax][0]<=0) { pccr[hop1][hop0][rmax][0]=1;}
				pccr[hop1][hop0][rmax][1]= hop0  - (int) h1[hop1][hop0]; if (pccr[hop1][hop0][rmax][1]<=0) {pccr[hop1][hop0][rmax][1]=1;}
				pccr[hop1][hop0][rmax][2]= hop0  - (int) h2[hop1][hop0]; if (pccr[hop1][hop0][rmax][2]<=0) { pccr[hop1][hop0][rmax][2]=1;}
				pccr[hop1][hop0][rmax][3]=hop0-hop1;if (pccr[hop1][hop0][rmax][3]<=0) pccr[hop1][hop0][rmax][3]=1;
				pccr[hop1][hop0][rmax][4]=hop0; //null hop
				
				  //check of null hop value. This control is used in "LHE advanced", where value of zero is forbidden
				  //in basic LHE there is no need for this control
				  if (pccr[hop1][hop0][rmax][4]<=0) pccr[hop1][hop0][rmax][4]=1; //null hop
				  if (pccr[hop1][hop0][rmax][4]>255) pccr[hop1][hop0][rmax][4]=255;//null hop
				
				pccr[hop1][hop0][rmax][5]= hop0+hop1;if (pccr[hop1][hop0][rmax][5]>255) pccr[hop1][hop0][rmax][5]=255;
				pccr[hop1][hop0][rmax][6]= hop0  + (int) h6[hop1][hop0]; if (pccr[hop1][hop0][rmax][6]>255) {pccr[hop1][hop0][rmax][6]=255;}
				pccr[hop1][hop0][rmax][7]= hop0  + (int) h7[hop1][hop0]; if (pccr[hop1][hop0][rmax][7]>255) {pccr[hop1][hop0][rmax][7]=255;}
				pccr[hop1][hop0][rmax][8]= hop0  + (int) h8[hop1][hop0]; if (pccr[hop1][hop0][rmax][8]>255) {pccr[hop1][hop0][rmax][8]=255;}
				
				}//rmax
			}//hop1

		}//hop0
		
	}
}
