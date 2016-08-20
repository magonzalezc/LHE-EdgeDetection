public class LHEUtils {
	public static int LUMINANCE = 0;
	public static int CHROMINANCE_U = 1;
	public static int CHROMINANCE_V = 2;
	/**
	 * use this function if you have already an ImgUtil object created and
	 * an image loaded on it
	 * 
	 * this is your function if the source of the frame is not a file or
	 * if you want to re-use the ImgUtil object
	 * 
	 * @param img
	 * @param cf
	 */
	public static void compressBasicFrame(Image img)
	{
		System.out.println(" quantizing into hops...");
		
		quantizeOneHopPerPixelHorizontal(img,LUMINANCE);
		quantizeOneHopPerPixelVertical(img,LUMINANCE);
		quantizeOneHopPerPixel(img,LUMINANCE);
	}
	
	
	public static void quantizeOneHopPerPixelHorizontal(Image img, int component)
	{	
		int max_hop1=10; //hop1 interval 4..10
		int min_hop1=4;// minimum value of hop1 is 4 
		int start_hop1=(max_hop1+min_hop1)/2;
		
		int hop1=start_hop1;
		int hop0=0; // predicted luminance signal
		int emin;//error of predicted signal
		int hop_number=4;//pre-selected hop // 4 is NULL HOP
		int oc=0;// original color
		int pix=0;//pixel possition, from 0 to image size        
		boolean last_small_hop=false;// indicates if last hop is small. used for h1 adaptation mechanism
		int rmax=25;
		
		for (int y=0;y<img.height;y++)  {
			for (int x=0;x<img.width;x++)  {

				//original image luminances are in the array img.YUV[0]
				// chrominance signals are stored in img.YUV[1] and img.YUV[2] but they are not
				// used in this function, designed for learning LHE basics
				oc=img.YUV[component][pix];

				
				//prediction of signal (hop0) , based on pixel's coordinates 
				//----------------------------------------------------------
				if (x>0){
					hop0=img.YUV_resultHorizontal[component][pix-1];	
				} else if (x==0) {
					hop0=oc;//first line always is perfectly predicted! :-)  
					last_small_hop=false;
					hop1=start_hop1;	
				} 

				//hops computation. 
				//error initial values 
				emin=256;//current minimum prediction error 
				int e2=0;//computed error for each hop 

				//positive hops computation
				//-------------------------
				if (oc-hop0>=0) 
				{
					for (int j=4;j<=8;j++) {
						e2=oc-Cache.pccr[hop1][hop0][rmax][j];
						
						if (e2<0) e2=-e2;
						if (e2<emin) {hop_number=j;emin=e2;}
						else break;
					}
				}
				//negative hops computation
				//-------------------------
				else 
				{
					for (int j=4;j>=0;j--) {
						e2=Cache.pccr[hop1][hop0][rmax][j]-oc;
						
						if (e2<0) e2=-e2;
						if (e2<emin) {hop_number=j;emin=e2;}
						else break;
					}
				}

				
				//assignment of final color value
				//--------------------------------
				img.YUV_resultHorizontal[component][pix]=Cache.pccr[hop1][hop0][25][hop_number];//final luminance
				img.hopsHorizontal[component][pix]=hop_number; //final hop value
				
				//edge detection
				//--------------------------------
				InterpolationUtils.detectAndMarkEdge (hop_number, component, pix, img.edgesPartial);

				//tunning hop1 for the next hop ( "h1 adaptation")
				//------------------------------------------------
				boolean small_hop=false;
				if (hop_number<=5 && hop_number>=3) small_hop=true;// 4 is in the center, 4 is null hop
				else small_hop=false;     

				if( (small_hop) && (last_small_hop))  {
					hop1=hop1-1;
					if (hop1<min_hop1) hop1=min_hop1;
				} 
				else {
					hop1=max_hop1;
				}
				
				//lets go for the next pixel
				//--------------------------
				last_small_hop=small_hop;
				pix++;            
			}//for x
		}//for y
		
		
		
	}//end function
	
	
	public static void quantizeOneHopPerPixelVertical(Image img, int component)
	{	
		int max_hop1=10; //hop1 interval 4..10
		int min_hop1=4;// minimum value of hop1 is 4 
		int start_hop1=(max_hop1+min_hop1)/2;
		
		int hop1=start_hop1;
		int hop0=0; // predicted luminance signal
		int emin;//error of predicted signal
		int hop_number=4;//pre-selected hop // 4 is NULL HOP
		int oc=0;// original color
		int pix=0;//pixel possition, from 0 to image size        
		boolean last_small_hop=false;// indicates if last hop is small. used for h1 adaptation mechanism
		int rmax=25;
		
		for (int y=0;y<img.height;y++)  {
			for (int x=0;x<img.width;x++)  {

				//original image luminances are in the array img.YUV[0]
				// chrominance signals are stored in img.YUV[1] and img.YUV[2] but they are not
				// used in this function, designed for learning LHE basics
				oc=img.YUV[component][pix];

				
				//prediction of signal (hop0) , based on pixel's coordinates 
				//----------------------------------------------------------
				if (y>0){
					hop0=img.YUV_resultVertical[component][pix-img.width];	
					
					if (x==0) {
						last_small_hop=false;
						hop1=start_hop1;
					}
				}
				else if (y==0){
					hop0=oc;//first pixel always is perfectly predicted! :-)  
				}

				//hops computation. 
				//error initial values 
				emin=256;//current minimum prediction error 
				int e2=0;//computed error for each hop 

				//positive hops computation
				//-------------------------
				if (oc-hop0>=0) 
				{
					for (int j=4;j<=8;j++) {
						e2=oc-Cache.pccr[hop1][hop0][rmax][j];
						
						if (e2<0) e2=-e2;
						if (e2<emin) {hop_number=j;emin=e2;}
						else break;
					}
				}
				//negative hops computation
				//-------------------------
				else 
				{
					for (int j=4;j>=0;j--) {
						e2=Cache.pccr[hop1][hop0][rmax][j]-oc;
						
						if (e2<0) e2=-e2;
						if (e2<emin) {hop_number=j;emin=e2;}
						else break;
					}
				}

				
				//assignment of final color value
				//--------------------------------
				img.YUV_resultVertical[component][pix]=Cache.pccr[hop1][hop0][25][hop_number];//final luminance
				img.hopsVertical[component][pix]=hop_number; //final hop value
				
				//edge detection
				//--------------------------------
				InterpolationUtils.detectAndMarkEdge (hop_number, component, pix, img.edgesPartial);
				
				//tunning hop1 for the next hop ( "h1 adaptation")
				//------------------------------------------------
				boolean small_hop=false;
				if (hop_number<=5 && hop_number>=3) small_hop=true;// 4 is in the center, 4 is null hop
				else small_hop=false;     

				if( (small_hop) && (last_small_hop))  {
					hop1=hop1-1;
					if (hop1<min_hop1) hop1=min_hop1;
				} 
				else {
					hop1=max_hop1;
				}
				
				//lets go for the next pixel
				//--------------------------
				last_small_hop=small_hop;
				pix++;            
			}//for x
		}//for y
		
	}//end function
	
	public static void quantizeOneHopPerPixel(Image img, int component)
	{	
		int max_hop1=10; //hop1 interval 4..10
		int min_hop1=4;// minimum value of hop1 is 4 
		int start_hop1=(max_hop1+min_hop1)/2;
		
		int hop1=start_hop1;
		int hop0=0; // predicted luminance signal
		int emin;//error of predicted signal
		int hop_number=4;//pre-selected hop // 4 is NULL HOP
		int oc=0;// original color
		int pix=0;//pixel possition, from 0 to image size        
		boolean last_small_hop=false;// indicates if last hop is small. used for h1 adaptation mechanism
		int rmax=25;
		
		for (int y=0;y<img.height;y++)  {
			for (int x=0;x<img.width;x++)  {

				//original image luminances are in the array img.YUV[0]
				// chrominance signals are stored in img.YUV[1] and img.YUV[2] but they are not
				// used in this function, designed for learning LHE basics
				oc=img.YUV[component][pix];

				
				//prediction of signal (hop0) , based on pixel's coordinates 
				//----------------------------------------------------------
				/*
				if ((y>0) &&(x>0) && x!=img.width-1){
					hop0=(4*img.YUV_result[component][pix-1]+3*img.YUV_result[component][pix+1-img.width])/7;	
				}
				else if ((x==0) && (y>0)){
					hop0=img.YUV_result[component][pix-img.width];
					last_small_hop=false;
					hop1=start_hop1;
				}
				else if ((x==img.width-1) && (y>0)) {
					hop0=(4*img.YUV_result[component][pix-1]+2*img.YUV_result[component][pix-img.width])/6;				
				}else if (y==0 && x>0) {
					hop0=img.YUV_result[component][x-1];
				}else if (x==0 && y==0) {  
					hop0=oc;//first pixel always is perfectly predicted! :-)  
				}
				*/
				
				if ((y>0) &&(x>0) && x!=img.width-1){
					hop0=(img.YUV_result[component][pix-1]+img.YUV_result[component][pix-img.width])/2;	
				}
				else if ((x==0) && (y>0)){
					hop0=img.YUV_result[component][pix-img.width];
					last_small_hop=false;
					hop1=start_hop1;
				}
				else if ((x==img.width-1) && (y>0)) {
					hop0=(img.YUV_result[component][pix-1]+img.YUV_result[component][pix-img.width])/2;				
				}else if (y==0 && x>0) {
					hop0=img.YUV_result[component][x-1];
				}else if (x==0 && y==0) {  
					hop0=oc;//first pixel always is perfectly predicted! :-)  
				}

				//hops computation. 
				//error initial values 
				emin=256;//current minimum prediction error 
				int e2=0;//computed error for each hop 

				//positive hops computation
				//-------------------------
				if (oc-hop0>=0) 
				{
					for (int j=4;j<=8;j++) {
						e2=oc-Cache.pccr[hop1][hop0][rmax][j];
						
						if (e2<0) e2=-e2;
						if (e2<emin) {hop_number=j;emin=e2;}
						else break;
					}
				}
				//negative hops computation
				//-------------------------
				else 
				{
					for (int j=4;j>=0;j--) {
						e2=Cache.pccr[hop1][hop0][rmax][j]-oc;
						
						if (e2<0) e2=-e2;
						if (e2<emin) {hop_number=j;emin=e2;}
						else break;
					}
				}

				
				//assignment of final color value
				//--------------------------------
				img.YUV_result[component][pix]=Cache.pccr[hop1][hop0][25][hop_number];//final luminance
				img.hops[component][pix]=hop_number; //final hop value
				
				//edge detection
				//--------------------------------
				InterpolationUtils.detectAndMarkEdge (hop_number, component, pix, img.edges);
				
				//tunning hop1 for the next hop ( "h1 adaptation")
				//------------------------------------------------
				boolean small_hop=false;
				if (hop_number<=5 && hop_number>=3) small_hop=true;// 4 is in the center, 4 is null hop
				else small_hop=false;     

				if( (small_hop) && (last_small_hop))  {
					hop1=hop1-1;
					if (hop1<min_hop1) hop1=min_hop1;
				} 
				else {
					hop1=max_hop1;
				}
				
				//lets go for the next pixel
				//--------------------------
				last_small_hop=small_hop;
				pix++;            
			}//for x
		}//for y
		
	}//end function

}
