import java.util.Arrays;

public class Filter {

	
	public static void filterScale3x (Image img, int component) {
		int pix;
		int u1 = 11;
		
		//First, copy the image
		for (int y=0; y<img.height; y++) {
			for (int x=0; x<img.width; x++) {
				pix = y*img.width + x;
				
				img.YUV_filtered[component][pix] = img.YUV[component][pix];
			}
		}
		
		int A,B,C,D,E,F,G,H,I;
		//Second, filter the image
		for (int y=1; y<img.height - 1; y++) {
			for (int x=1; x<img.width - 1; x++) {
				pix = y*img.width + x;
	
				A = img.YUV[component][pix - img.width - 1];
				B = img.YUV[component][pix - img.width];
				C = img.YUV[component][pix - img.width + 1];
				D = img.YUV[component][pix - 1];
				E = img.YUV[component][pix];
				F = img.YUV[component][pix + 1];
				G = img.YUV[component][pix + img.width - 1];
				H = img.YUV[component][pix + img.width];
				I = img.YUV[component][pix + img.width + 1];
				
				
				img.YUV_filtered[component][pix - img.width - 1] = E;
				img.YUV_filtered[component][pix - img.width] = E;
				img.YUV_filtered[component][pix - img.width + 1] = E;
				img.YUV_filtered[component][pix - 1] = E;
				img.YUV_filtered[component][pix] = E;
				img.YUV_filtered[component][pix + 1] = E;
				img.YUV_filtered[component][pix + img.width - 1] = E;
				img.YUV_filtered[component][pix + img.width] = E;
				img.YUV_filtered[component][pix + img.width + 1] = E;
				
				
				if (D==B && D!=H && B!=F) {
					img.YUV_filtered[component][pix - img.width - 1] = D;
				}
				
				if ((D==B && D!=H && B!=F && E!=C) || (B==F && B!=D && F!=H && E!=A)) {
					img.YUV_filtered[component][pix - img.width] = B;
				}
				
				if (B==F && B!=D && F!=H) {
					img.YUV_filtered[component][pix - img.width + 1] = F;
				}
				
				if ((H==D && H!=F && D!=B && E!=A) || (D==B && D!=H && B!=F && E!=G)) {
					img.YUV_filtered[component][pix - 1] = D;
				}
				
				if ((B==F && B!=D && F!=H && E!=I) || (F==H && F!=B && H!=D && E!=C)) {
					img.YUV_filtered[component][pix + 1] = F;
				}
				
				if (H==D && H!=F && D!=B) {
					img.YUV_filtered[component][pix + img.width - 1] = D;
				}
				
				if ((F==H && F!=B && H!=D && E!=G) || (H==D && H!=F && D!=B && E!=I)) {
					img.YUV_filtered[component][pix + img.width] = H;
				}
				
				if (F==H && F!= B && H!=D) {
					img.YUV_filtered[component][pix + img.width + 1] = F;
				}
				
				
				/*
				if (Math.abs(D-B) < u1 && D!=H && B!=F) {
					img.YUV_filtered[component][pix - img.width - 1] = (D+B)/2;
				}
				
				if (Math.abs(D-B) < u1 && D!=H && B!=F && E!=C) { 
					img.YUV_filtered[component][pix - img.width] = (D+B)/2;
				}
				
				if ((Math.abs(B-F) < u1 && B!=D && F!=H && E!=A)) {
					img.YUV_filtered[component][pix - img.width] = (F+B)/2;
				}
				
				if (Math.abs(B-F) < u1 && B!=D && F!=H) {
					img.YUV_filtered[component][pix - img.width + 1] = (F+B)/2;
				}
				
				if (Math.abs(H-D) < u1 && H!=F && D!=B && E!=A) {
					img.YUV_filtered[component][pix - img.width + 1] = (H+D)/2;
				}
						
				if (Math.abs(D-B) < u1 && D!=H && B!=F && E!=G) {
					img.YUV_filtered[component][pix - 1] = (D+B)/2;
				}
				
				if (Math.abs(B-F) < u1 && B!=D && F!=H && E!=I) {
					img.YUV_filtered[component][pix + 1] = (B+F)/2;
				}
				
				if (Math.abs(F-H) < u1 && F!=B && H!=D && E!=C) {
					img.YUV_filtered[component][pix + 1] = (F+H)/2;
				}
				
				if (Math.abs(H-D) < u1 && H!=F && D!=B) {
					img.YUV_filtered[component][pix + img.width - 1] = (D+H)/2;
				}
				
				if (Math.abs(F-H) < u1 && F!=B && H!=D && E!=G) {
					img.YUV_filtered[component][pix + img.width] = (F+H)/2;
				}
				
				if (Math.abs(H-D) < u1 && H!=F && D!=B && E!=I) {
					img.YUV_filtered[component][pix + img.width] = (H+D)/2;
				}
				
				if (Math.abs(F-H) < u1 && F!= B && H!=D) {
					img.YUV_filtered[component][pix + img.width + 1] = (F+H)/2;
				}
				*/
			}
		}
	}
	
	public static void filterCascade (Image img, int component) {
		int [] out = new int[img.width * img.height];
		int [] out1 = new int[img.width * img.height];
		int [] out2 = new int[img.width * img.height];
		int [] out3 = new int[img.width * img.height];
		int [] out4 = new int[img.width * img.height];
		int [] out5 = new int[img.width * img.height];
		int [] out6 = new int[img.width * img.height];
		
		filterEPX (img, img.YUV[component], out);
		//filterSNN (img, out, out1, 255);
		filterSNN (img, out, img.YUV_filtered[component], 255);
	
	}
	
	public static void filterSNN (Image img, int[] in, int[]out, int u1) {
		int pix;
		int A,B,C,D,E,F,G,H,I;
		int pixel_1, pixel_2, pixel_3, pixel_4;
		int distance_1, distance_2, distance_3, distance_4, distance_5, distance_6, distance_7, distance_8;
		int average;

		//First, copy the image
		for (int y=0; y<img.height; y++) {
			for (int x=0; x<img.width; x++) {
				pix = y*img.width + x;
				
				out[pix] = in[pix];
			}
		}
		
		//Second, filter the image
		for (int y=1; y<img.height - 1; y++) {
			for (int x=1; x<img.width - 1; x++) {
				pix = y*img.width + x;
				
				A = in[pix - img.width - 1];
				B = in[pix - img.width];
				C = in[pix - img.width + 1];
				D = in[pix - 1];
				E = in[pix];
				F = in[pix + 1];
				G = in[pix + img.width - 1];
				H = in[pix + img.width];
				I = in[pix + img.width + 1];
				
				distance_1 = A-E;			
				distance_2 = B-E;			
				distance_3 = C-E;				
				distance_4 = D-E;			
				distance_5 = F-E;			
				distance_6 = G-E;				
				distance_7 = H-E;
				distance_8 = I-E;

				/*
				if (distance_1 < 0) distance_1 = -distance_1;
				if (distance_2 < 0) distance_2 = -distance_2;
				if (distance_3 < 0) distance_3 = -distance_3;
				if (distance_4 < 0) distance_4 = -distance_4;
				if (distance_5 < 0) distance_5 = -distance_5;
				if (distance_6 < 0) distance_6 = -distance_6;
				if (distance_7 < 0) distance_7 = -distance_7;
				if (distance_8 < 0) distance_8 = -distance_8;
				*/
	
				if (distance_1 < distance_8 && distance_1 < u1) {
					pixel_1 = (A+E)/2;
					pixel_1 = A;
				} else if (distance_1 > distance_8 && distance_8 < u1) {
					pixel_1 = (I+E)/2;
					pixel_1 = I;
				} else {
					pixel_1 = E;
				}
				
				if (distance_2 < distance_7 && distance_2 < u1) {
					pixel_2 = (B+E)/2;
					pixel_2 = B;

				} else if (distance_2 > distance_7 && distance_7 < u1) {
					pixel_2 = (H+E)/2;
					pixel_2 = H;

				} else {
					pixel_2 = E;
				}
				
				if (distance_3 < distance_6 && distance_3 < u1) {
					pixel_3 = (C+E)/2;
					pixel_3 = C;

				} else if (distance_3 > distance_6 && distance_6 < u1) {
					pixel_3 = (G+E)/2;
					pixel_3 = G;

				} else {
					pixel_3 = E;
				}
				
				if (distance_4 < distance_5 && distance_4 < u1) {
					pixel_4 = (D+E)/2;
					pixel_4 = D;

				} else if (distance_4 > distance_5 && distance_5 < u1) {
					pixel_4 = (F+E)/2;
					pixel_4 = F;

				} else {
					pixel_4 = E;
				}
				
				average = (pixel_1 + pixel_2 + pixel_3 + pixel_4) / 4;
				
				out[pix] = (average+E)/2;				
			}
		}
	}
	
	public static void filterMedian (Image img, int component) {
		int pix;
		int box [] = new int [9];
		
		//First, copy the image
		for (int y=0; y<img.height; y++) {
			for (int x=0; x<img.width; x++) {
				pix = y*img.width + x;
				
				img.YUV_filtered[component][pix] = img.YUV[component][pix];
			}
		}
		
		//Second, filter the image
		for (int y=0; y<img.height - 2; y++) {
			for (int x=0; x<img.width - 2; x++) {
				pix = y*img.width + x;

				box[0] = img.YUV[component][pix];
				box[1] = img.YUV[component][pix +1];
				box[2] = img.YUV[component][pix +2];
				box[3] = img.YUV[component][pix + img.width];
				box[4] = img.YUV[component][pix + img.width + 1];
				box[5] = img.YUV[component][pix + img.width + 2];
				box[6] = img.YUV[component][pix + 2*img.width];
				box[7] = img.YUV[component][pix + 2*img.width + 1];
				box[8] = img.YUV[component][pix + 2*img.width + 2];

				Arrays.sort(box);

				img.YUV_filtered[component][pix] = (box[0] + box[1] + box[2] + box[3] + box[4] + box[5]+ box[6]+ box[7] +box[8])/9 ;
			}
		}
	}
	
	public static void filterMedian2 (Image img, int component) {
		int pix;
		int box [] = new int [6];
		
		//First, copy the image
		for (int y=0; y<img.height; y++) {
			for (int x=0; x<img.width; x++) {
				pix = y*img.width + x;
				
				img.YUV_filtered[component][pix] = img.YUV[component][pix];
			}
		}
		
		//Second, filter the image
		for (int y=0; y<img.height - 2; y++) {
			for (int x=0; x<img.width - 2; x++) {
				pix = y*img.width + x;

				box[0] = img.YUV[component][pix];
				box[1] = img.YUV[component][pix +1];
				box[2] = img.YUV[component][pix +2];
				box[3] = img.YUV[component][pix + img.width];
				box[4] = img.YUV[component][pix + img.width + 1];
				box[5] = img.YUV[component][pix + img.width + 2];

				Arrays.sort(box);

				img.YUV_filtered[component][pix] = (box[0] + box[1] + box[2] + box[3] + box[4] + box[5])/6 ;
			}
		}
	}
	
	public static void filterEPX (Image img, int[] in, int[]out) {
		int um1 = 11;
		int um2 = 16;
		int umbral=11;

		int mezcla;
		int[] matriz=new int[9];
		
		int i;
		
		//First, copy the image
		for (int y=0; y<img.height; y++) {
			for (int x=0; x<img.width; x++) {
				i = y*img.width + x;
				
				out[i] = in[i];
			}
		}

		for (int y=1; y<img.height-1;y++)
		{
			for (int x=1; x<img.width-1;x++)
			{
					
				boolean modif=false;
				
				int u1=um1;//11;
				int u2=um2;//16;
				
				i=y*img.width+x;		
				matriz[0]=out[i-1-img.width];
				matriz[1]=out[i-img.width];
				matriz[2]=out[i+1-img.width];
				matriz[3]=out[i-1];
				matriz[4]=out[i];
				matriz[5]=out[i+1];
				matriz[6]=out[i-1+img.width];
				matriz[7]=out[i+img.width];
				matriz[8]=out[i+1+img.width];
				
				//marco arriba izquierdo 
				if ((Math.abs(matriz[1]-matriz[2])<u1) &&
				    (Math.abs(matriz[3]-matriz[6])<u1) &&
				    (Math.abs(matriz[1]-matriz[3])<u2)) 
				{
					
					mezcla=(matriz[1]+matriz[3])/2;
					
					out[i]=mezcla;
					modif=true;
				}
				//marco arriba derecho
				  
				if ((Math.abs(matriz[0]-matriz[1])<u1) &&
						(Math.abs(matriz[5]-matriz[8])<u1) &&
					    (Math.abs(matriz[5]-matriz[1])<u2)) 
				{
					mezcla=(matriz[1]+matriz[5])/2;
					out[i]=mezcla;
					modif=true;
				 }
				
				//marco abajo izq
				if ((Math.abs(matriz[7]-matriz[8])<u1) &&
						(Math.abs(matriz[0]-matriz[3])<u1) &&
						(Math.abs(matriz[3]-matriz[7])<u2)) 
				{
					
					mezcla=(matriz[3]+matriz[7])/2;
					out[i]=mezcla;
					modif=true;
				}
					
				//marco abajo dere
				if ((Math.abs(matriz[6]-matriz[7])<u1) &&
				    (Math.abs(matriz[2]-matriz[5])<u1) &&
					(Math.abs(matriz[7]-matriz[5])<u2)) 
				{
					mezcla=(matriz[7]+matriz[5])/2;
					out[i]=mezcla;
					modif=true;
				}
					

				if (!modif)
				{
						//marco arriba izquierdo 
					if (((Math.abs(matriz[1]-matriz[2])<u1) &&
						    (Math.abs(matriz[1]-matriz[3])<u2))) 
				    {
						mezcla=(matriz[1]+matriz[3]+matriz[4])/3;
						mezcla=(matriz[4]+matriz[1])/2;
						out[i]=mezcla;
						modif=true;
				    }
					
					if ((Math.abs(matriz[3]-matriz[6])<u1) &&
					     (Math.abs(matriz[1]-matriz[3])<u2))
					{
						mezcla=(matriz[1]+matriz[3]+matriz[4])/3;
						mezcla=(matriz[3]+matriz[4])/2;
						out[i]=mezcla;
						modif=true;
					}
						
					//marco arriba derecho
					if ((Math.abs(matriz[0]-matriz[1])<u1) &&
							(Math.abs(matriz[5]-matriz[1])<u2)) 
					{
						  
						mezcla=(matriz[1]+matriz[5]+matriz[4])/3;
						mezcla=(matriz[1]+matriz[4])/2;
						out[i]=mezcla;
						modif=true;
					}
					
					if ((Math.abs(matriz[5]-matriz[8])<u1) &&
							  (Math.abs(matriz[5]-matriz[1])<u2))
					{
						  mezcla=(matriz[1]+matriz[5]+matriz[4])/3;
						  mezcla=(matriz[5]+matriz[4])/2;
						  out[i]=mezcla;
						  modif=true;
					}
					
					
					//marco abajo izq
					if ((Math.abs(matriz[7]-matriz[8])<u1) &&
							(Math.abs(matriz[3]-matriz[7])<umbral))
					{
						mezcla=(matriz[3]+matriz[7]+matriz[4])/3;
						mezcla=(matriz[7]+matriz[4])/2;
						out[i]=mezcla;
						modif=true;
							
					}
								    
					if ((Math.abs(matriz[0]-matriz[3])<u2) &&
							(Math.abs(matriz[3]-matriz[7])<umbral))
					{
						mezcla=(matriz[3]+matriz[7]+matriz[4])/3;
						mezcla=(matriz[3]+matriz[4])/2;
						out[i]=mezcla;
						modif=true;
					}
					
					//marco abajo dere
					if ((Math.abs(matriz[6]-matriz[7])<u1) &&
							(Math.abs(matriz[7]-matriz[5])<u2))
					{
						mezcla=(matriz[7]+matriz[5]+matriz[4])/3;
						mezcla=(matriz[7]+matriz[4])/2;
						out[i]=mezcla;
						modif=true;
					}
										    
					if ((Math.abs(matriz[2]-matriz[5])<u1) &&
						(Math.abs(matriz[7]-matriz[5])<u2))
					{
						mezcla=(matriz[7]+matriz[5]+matriz[4])/3;
						mezcla=(matriz[5]+matriz[4])/2;
						out[i]=mezcla;
						modif=true;
					}												
				}
			}		
		}
	}
}
