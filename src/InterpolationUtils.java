
public class InterpolationUtils {
	
	public static int EDGE = 255;
	
	public static int NEIGHBOUR_INTERPOLATION = 0;
	public static int LINEAR_INTERPOLATION = 1;
	public static int EDGE_INTERPOLATION1 = 2;
	public static int EDGE_INTERPOLATION2 = 3;
	public static int EDGE_INTERPOLATION3 = 4;
	public static int EDGE_INTERPOLATION4 = 5;

	public static void detectAndMarkEdge (int hop, int component, int pix, int edges[][]) {
		
		if (hop==0 || hop==1 || hop==2 || hop==6 || hop==7 || hop == 8)
			edges[component][pix] = EDGE;//apuntar también el signo. Solo unir segmentos con el mismo signo.

	}
	
	public static void interpolateEdgesVertical (int component, Image img) {
		int originalWidth = img.width;
	    int originalHeight = img.height;
		    
	    int interpolatedWidth = img.interpolatedWidth;
	    int interpolatedHeight = img.interpolatedHeight;
	    
	    boolean edgeFirstSample, edgeSecondSample;
	    
	    float initSecondSampleFloat, ySecondSampleFloat;
	    int yFirstSample, ySecondSample;
	    
	    float center, pppy;
	    int samples;
	    
    	center = (interpolatedHeight-1f)/2f;
    	pppy = interpolatedHeight/originalHeight;
    	samples = originalHeight/2; //samples from init to center of mesh
    	
	    if (originalHeight % 2 == 0) {
	    	initSecondSampleFloat = center - samples*pppy + (pppy/2f); //first sample position
	    } else {
	    	initSecondSampleFloat = center - samples*pppy;
	    }

		for (int x=0;x<originalWidth;x++) {        
	    	yFirstSample = 0;
	    	ySecondSampleFloat = initSecondSampleFloat;
	    	
            for (int y_sc=0;y_sc<originalHeight;y_sc++) {            
                
            	ySecondSample = (int) (ySecondSampleFloat + 0.5); 
            	
            	for (int i=yFirstSample; i<ySecondSample; i++ ) {
            		
            		edgeFirstSample = false;
            		edgeSecondSample = false;
            		
            		edgeFirstSample = img.edges[component][y_sc*originalWidth+x] == EDGE;
            		
            		if ((y_sc+1)<originalHeight) {
            			edgeSecondSample = img.edges[component][(y_sc+1)*originalWidth+x] == EDGE;
            		} 
            		
            		if (edgeFirstSample || edgeSecondSample)
                		img.edgesVerticalInterpolated[component][i*interpolatedWidth+x] = EDGE;
            	}       	
            	
            	yFirstSample = ySecondSample;
                ySecondSampleFloat+=pppy;               
                
            }//y
	    }//x				    
	}
	
	public static void interpolateEdgesHorizontal (int component, Image img) {
		int originalWidth = img.width;
		    
	    int interpolatedWidth = img.interpolatedWidth;
	    int interpolatedHeight = img.interpolatedHeight;
	    
	    float initSecondSampleFloat, xSecondSampleFloat;
	    int xFirstSample, xSecondSample;
	    
	    boolean edgeFirstSample, edgeSecondSample;
	    float center, pppx;
	    int samples;
	    
    	center = (interpolatedWidth-1f)/2f;
    	pppx = interpolatedWidth/originalWidth;
    	samples = originalWidth/2; //samples from init to center of mesh
    	
	    if (originalWidth % 2 == 0) {
	    	initSecondSampleFloat = center - samples*pppx + (pppx/2f); //first sample position
	    } else {
	    	initSecondSampleFloat = center - samples*pppx;
	    }
		
		for (int y=0; y<interpolatedHeight; y++)
	    { 
			xFirstSample = 0;
			xSecondSampleFloat = initSecondSampleFloat;
	        
	        //Interpolated x coordinates
	        for (int x_sc=0; x_sc<originalWidth; x_sc++)
	        {
        		xSecondSample = (int) (xSecondSampleFloat + 0.5); 
            	
            	for (int i=xFirstSample; i<xSecondSample; i++ ) {

            		edgeFirstSample = img.edgesVerticalInterpolated[component][y*interpolatedWidth+x_sc] == EDGE;
            		edgeSecondSample = false;
            		
            		if ((x_sc+1)<originalWidth)
            			edgeSecondSample = img.edgesVerticalInterpolated[component][y*interpolatedWidth+(x_sc+1)] == EDGE;
  
            		if (edgeFirstSample || edgeSecondSample)
                		img.edgesInterpolated[component][y*interpolatedWidth+i] = EDGE;		
            	}

            	xFirstSample = xSecondSample;
                xSecondSampleFloat+=pppx;     
	        }//x
	    }//y 
	}
	
	public static void interpolateVertical (int component,Image img, int interpolationMode) {

	    int originalWidth = img.width;
	    int originalHeight = img.height;
	    
	    int interpolatedWidth = img.interpolatedWidth;
	    int interpolatedHeight = img.interpolatedHeight;
	    
	    boolean edge;
	    
	    float initFirstSampleFloat, initSecondSampleFloat, yFirstSampleFloat, ySecondSampleFloat;
	    int yFirstSample, ySecondSample;
	    
	    float center, pppy, componentValue, average;
	    int samples;
	    
    	center = (interpolatedHeight-1f)/2f;
    	pppy = interpolatedHeight/originalHeight;
    	samples = originalHeight/2; //samples from init to center of mesh
    	
    	initFirstSampleFloat = 0;

	    if (originalHeight % 2 == 0) {
	    	initSecondSampleFloat = center - samples*pppy + (pppy/2); //first sample position
	    } else {
	    	initSecondSampleFloat = center - samples*pppy;
	    }

		for (int x=0;x<originalWidth;x++) {        
            // bucle for horizontal scanline 
            // scans the downsampled image, pixel by pixel
	    	yFirstSample = 0;
	    	yFirstSampleFloat = initFirstSampleFloat;
	    	ySecondSampleFloat = initSecondSampleFloat;
	    	
	    	
            for (int y_sc=0;y_sc<originalHeight;y_sc++) {            
                
            	ySecondSample = (int) (ySecondSampleFloat + 0.5); 
            	
            	for (int i=yFirstSample; i<ySecondSample; i++ ) {
            		average = 0;
            		componentValue = 0;
            		
            		edge = img.edgesInterpolated[component][i*interpolatedWidth+x] == EDGE;
            		
            		
            		//EDGE1
            		if (interpolationMode == EDGE_INTERPOLATION1) {
            			if ((y_sc+1)<originalHeight) {	
	            			if (edge){ //if there is an edge, we should copy the nearest sample.
	            				
	            				//Looks for nearest sample
	            				if ((i-yFirstSampleFloat) > (ySecondSampleFloat-i)) { //ySecond is nearest 
	            					componentValue = img.YUV[component][(y_sc+1)*originalWidth+x];
	            					average = 1;
	            				} else { //yFirst is nearest
	            					componentValue = img.YUV[component][y_sc*originalWidth+x];
	            					average = 1;
	            				}

            					average = 1;
            					
            					
            				} else {//linear average
            					componentValue = (ySecondSampleFloat-i)*img.YUV[component][y_sc*originalWidth+x] 
    	            					+ (i-yFirstSampleFloat)*img.YUV[component][(y_sc+1)*originalWidth+x];
            					average = ySecondSampleFloat - yFirstSampleFloat;
            				}	            			
	            		} else {
	            			componentValue = img.YUV[component][y_sc*originalWidth+x];
	            			average = 1;
	            		}
            		}
	            	
            		
            		//LINEAR
            		if (interpolationMode == LINEAR_INTERPOLATION) {
	            		if (y_sc>0) {
	            			
	            			
	            			componentValue = (ySecondSampleFloat-i)*img.YUV[component][(y_sc-1)*originalWidth+x] 
	            					+ (i-yFirstSampleFloat)*img.YUV[component][y_sc*originalWidth+x];
	            			
	            			average = ySecondSampleFloat-yFirstSampleFloat;
	            		} else {
	            			componentValue = img.YUV[component][y_sc*originalWidth+x];
	            			average = 1;
	            		}
            		}
            		
            		//NEIGHBOUR
            		if (interpolationMode == NEIGHBOUR_INTERPOLATION) {
	            		componentValue = img.YUV[component][y_sc*originalWidth+x];
	            		average = 1;
            		}
            		
            		img.YUV_intermediateInterpolated[component][i*interpolatedWidth+x]= (int) (componentValue / average);        		
            	}

            	yFirstSampleFloat = ySecondSampleFloat;
            	yFirstSample = ySecondSample;
                ySecondSampleFloat+=pppy;               
                
            }//y
	    }//x	
	}
	
	public static void interpolateHorizontal (int component,Image img, int interpolationMode) {
	    int originalWidth = img.width;
	    
	    int interpolatedWidth = img.interpolatedWidth;
	    int interpolatedHeight = img.interpolatedHeight;
	    
	    float initFirstSampleFloat, initSecondSampleFloat, xFirstSampleFloat, xSecondSampleFloat;
	    int xFirstSample, xSecondSample;
	    
	    boolean edge;
	    float center, pppx, componentValue, average;
	    int samples;
	    
    	center = (interpolatedWidth-1f)/2f;
    	pppx = interpolatedWidth/originalWidth;
    	samples = originalWidth/2; //samples from init to center of mesh
    	
    	initFirstSampleFloat = 0;

	    if (originalWidth % 2 == 0) {
	    	initSecondSampleFloat = center - samples*pppx + (pppx/2f); //first sample position
	    } else {
	    	initSecondSampleFloat = center - samples*pppx;
	    }
		
		for (int y=0; y<interpolatedHeight; y++)
	    { 
			xFirstSample = 0;
			xFirstSampleFloat = initFirstSampleFloat;
			xSecondSampleFloat = initSecondSampleFloat;
	        
	        //Interpolated x coordinates
	        for (int x_sc=0; x_sc<originalWidth; x_sc++)
	        {
        		xSecondSample = (int) (xSecondSampleFloat + 0.5); 
            	
            	for (int i=xFirstSample; i<xSecondSample; i++ ) {
            		average = 0;
            		componentValue = 0;

            		edge = img.edgesInterpolated[component][y*interpolatedWidth+i] == EDGE;

            		if (interpolationMode == EDGE_INTERPOLATION1) {
            			if ((x_sc+1)<originalWidth) {
	            			if (edge) { //if there is an edge, copy the nearest sample
	            				//Looks for the nearest sample
	            				if ((i-xFirstSampleFloat)>(xSecondSampleFloat-i)) {//xSecond is nearest
	            					componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
	            					average = 1;
	            				} else {
	            					componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	            					average = 1;
	            				}
    	            			
            				} else { //linear average
            					componentValue = (xSecondSampleFloat-i)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc] 
    	            					+ (i-xFirstSampleFloat)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
    	            			average = xSecondSampleFloat - xFirstSampleFloat;	
            				}
	            		} else {
	            			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
        					average = 1;
	            			
	            		}
            		}

            		
            		//LINEAR
            		if (interpolationMode == LINEAR_INTERPOLATION) {
	            		if (x_sc>0) {
	            			componentValue = (xSecondSampleFloat-i)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc-1)] 
	            					+ (i-xFirstSampleFloat)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	            			average = xSecondSampleFloat - xFirstSampleFloat;
	            			
	            		} else {
	            			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	            			average = 1;
	            		}
            		}
            		
            		//NEIGHBOUR
            		if (interpolationMode == NEIGHBOUR_INTERPOLATION) {
	        			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	        			average = 1;
            		}
	
            		img.YUV_interpolated[component][y*interpolatedWidth+i]= (int) (componentValue / average);  		
            	}

            	xFirstSampleFloat = xSecondSampleFloat;
            	xFirstSample = xSecondSample;
                xSecondSampleFloat+=pppx;     
	        }//x
	    }//y 
	}

	
	public static void interpolateVertical_edgesAtSameTime (int component,Image img, int interpolationMode) {

	    int originalWidth = img.width;
	    int originalHeight = img.height;
	    
	    int interpolatedWidth = img.interpolatedWidth;
	    int interpolatedHeight = img.interpolatedHeight;
	    
	    boolean edgeFirstSample, edgeSecondSample;
	    
	    float initFirstSampleFloat, initSecondSampleFloat, yFirstSampleFloat, ySecondSampleFloat;
	    int yFirstSample, ySecondSample;
	    
	    float center, pppy, componentValue, average;
	    int samples;
	    
    	center = (interpolatedHeight-1f)/2f;
    	pppy = interpolatedHeight/originalHeight;
    	samples = originalHeight/2; //samples from init to center of mesh
    	
    	initFirstSampleFloat = 0;

	    if (originalHeight % 2 == 0) {
	    	initSecondSampleFloat = center - samples*pppy + (pppy/2f); //first sample position
	    } else {
	    	initSecondSampleFloat = center - samples*pppy;
	    }

		for (int x=0;x<originalWidth;x++) {        
            // bucle for horizontal scanline 
            // scans the downsampled image, pixel by pixel
	    	yFirstSample = 0;
	    	yFirstSampleFloat = initFirstSampleFloat;
	    	ySecondSampleFloat = initSecondSampleFloat;
	    	
            for (int y_sc=0;y_sc<originalHeight;y_sc++) {            
                
            	ySecondSample = (int) (ySecondSampleFloat + 0.5); 
            	
            	for (int i=yFirstSample; i<ySecondSample; i++ ) {
            		average = 0;
            		componentValue = 0;
            		
            		edgeFirstSample = img.edges[component][y_sc*originalWidth+x] == EDGE;
            		
            		if ((y_sc+1)<originalHeight) {
            			edgeSecondSample = img.edges[component][(y_sc+1)*originalWidth+x] == EDGE;
            		} else {
            			//This is pretending to force the situation when there is no second sample so first has to be used;
            			edgeFirstSample = false;
            			edgeSecondSample = true;
            		}	
            		
            		//EDGE1
            		if (interpolationMode == EDGE_INTERPOLATION1) {
	            		if (!edgeFirstSample && !edgeSecondSample){ 
	            			componentValue = (i-yFirstSampleFloat)*img.YUV[component][y_sc*originalWidth+x] 
	            					+ (ySecondSampleFloat-i)*img.YUV[component][(y_sc+1)*originalWidth+x];
	            			average = ySecondSampleFloat - yFirstSampleFloat;
	            		
	            		} else {
	            			componentValue = img.YUV[component][y_sc*originalWidth+x];
	            			average = 1;
	            		}
            		}
            		
            		//EDGE2
            		if (interpolationMode == EDGE_INTERPOLATION2) {
	            		if (edgeFirstSample  || edgeSecondSample){ 
	            			componentValue = img.YUV[component][y_sc*originalWidth+x];
	            			average = 1;	
	            		} else {
	            			componentValue = (i-yFirstSampleFloat)*img.YUV[component][y_sc*originalWidth+x] 
	            					+ (ySecondSampleFloat-i)*img.YUV[component][(y_sc+1)*originalWidth+x];
	            			average = ySecondSampleFloat - yFirstSampleFloat;
	            		}
            		}
            		
            		//EDGE3
            		if (interpolationMode == EDGE_INTERPOLATION3) {
            			if ((y_sc+1)<originalHeight) {	
	            			if (edgeFirstSample  && edgeSecondSample){ 
	            					componentValue = img.YUV[component][y_sc*originalWidth+x] + img.YUV[component][(y_sc+1)*originalWidth+x];
	    	            			average = 2;	
            				} else {
            					componentValue = (i-yFirstSampleFloat)*img.YUV[component][y_sc*originalWidth+x] 
    	            					+ (ySecondSampleFloat-i)*img.YUV[component][(y_sc+1)*originalWidth+x];
    	            			average = ySecondSampleFloat - yFirstSampleFloat;
            				}	            			
	            		} else {
	            			componentValue = img.YUV[component][y_sc*originalWidth+x];
	            			average = 1;
	            		}
            			
            		
            		}
            		
            		
            		//LINEAR
            		if (interpolationMode == LINEAR_INTERPOLATION) {
	            		if ((y_sc+1)<originalHeight) {
	            			componentValue = (i-yFirstSampleFloat)*img.YUV[component][y_sc*originalWidth+x] 
	            					+ (ySecondSampleFloat-i)*img.YUV[component][(y_sc+1)*originalWidth+x];
	            			average = ySecondSampleFloat - yFirstSampleFloat;
	            		} else {
	            			componentValue = img.YUV[component][y_sc*originalWidth+x];
	            			average = 1;
	            		}
            		}
            		
            		//NEIGHBOUR
            		if (interpolationMode == NEIGHBOUR_INTERPOLATION) {
	            		componentValue = img.YUV[component][y_sc*originalWidth+x];
	        			average = 1;
            		}
            		
            		if (edgeFirstSample || edgeSecondSample)
                		img.edgesVerticalInterpolated[component][i*interpolatedWidth+x] = EDGE;

            		
            		img.YUV_intermediateInterpolated[component][i*interpolatedWidth+x]= (int) (componentValue / average);
            	}

            	yFirstSampleFloat = ySecondSampleFloat;
            	yFirstSample = ySecondSample;
                ySecondSampleFloat+=pppy;               
                
            }//y
	    }//x	
	}
	
	public static void interpolateHorizontal_edgesAtSameTime (int component,Image img, int interpolationMode) {
	    int originalWidth = img.width;
	    
	    int interpolatedWidth = img.interpolatedWidth;
	    int interpolatedHeight = img.interpolatedHeight;
	    
	    float initFirstSampleFloat, initSecondSampleFloat, xFirstSampleFloat, xSecondSampleFloat;
	    int xFirstSample, xSecondSample;
	    
	    boolean edgeFirstSample, edgeSecondSample;
	    float center, pppx, componentValue, average;
	    int samples;
	    
    	center = (interpolatedWidth-1f)/2f;
    	pppx = interpolatedWidth/originalWidth;
    	samples = originalWidth/2; //samples from init to center of mesh
    	
    	initFirstSampleFloat = 0;

	    if (originalWidth % 2 == 0) {
	    	initSecondSampleFloat = center - samples*pppx + (pppx/2f); //first sample position
	    } else {
	    	initSecondSampleFloat = center - samples*pppx;
	    }
		
		for (int y=0; y<interpolatedHeight; y++)
	    { 
			xFirstSample = 0;
			xFirstSampleFloat = initFirstSampleFloat;
			xSecondSampleFloat = initSecondSampleFloat;
	        
	        //Interpolated x coordinates
	        for (int x_sc=0; x_sc<originalWidth; x_sc++)
	        {
        		xSecondSample = (int) (xSecondSampleFloat + 0.5); 
            	
            	for (int i=xFirstSample; i<xSecondSample; i++ ) {
            		average = 0;
            		componentValue = 0;

            		edgeFirstSample = img.edgesVerticalInterpolated[component][y*interpolatedWidth+x_sc] == EDGE;
            		
            		if ((x_sc+1)<originalWidth)
            			edgeSecondSample = img.edgesVerticalInterpolated[component][y*interpolatedWidth+(x_sc+1)] == EDGE;
            		else {
            			//This is pretending to force the situation when there is no second sample so first has to be used;
            			edgeFirstSample = false;
            			edgeSecondSample = true;
            		}

            		if (interpolationMode == EDGE_INTERPOLATION1) {
	            		if (!edgeFirstSample && !edgeSecondSample) { 
	            			componentValue = (i-xFirstSampleFloat)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc] 
	            					+ (xSecondSampleFloat-i)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
	            			average = xSecondSampleFloat - xFirstSampleFloat;
	            		
	            		} else {
	            			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	            			average = 1;
	            		}
            		}
            		
            		if (interpolationMode == EDGE_INTERPOLATION2) {
	            		if (edgeFirstSample || edgeSecondSample) { 
	            			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	            			average = 1;
	            		} else {
	            			componentValue = (i-xFirstSampleFloat)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc] 
	            					+ (xSecondSampleFloat-i)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
	            			average = xSecondSampleFloat - xFirstSampleFloat;	
	            		}
            		}
            		
            		//EDGE3
            		if (interpolationMode == EDGE_INTERPOLATION3) {
            			if ((x_sc+1)<originalWidth) {
	            			if (edgeFirstSample && edgeSecondSample) { 
            					componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc] 
    	            					+ img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
            					average = 2;
            				} else {
            					componentValue = (i-xFirstSampleFloat)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc] 
    	            					+ (xSecondSampleFloat-i)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
    	            			average = xSecondSampleFloat - xFirstSampleFloat;	
            				}
	            		} else {
	            			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
        					average = 1;
	            			
	            		}
            		}
            		
            		//LINEAR
            		if (interpolationMode == LINEAR_INTERPOLATION) {
	            		if ((x_sc+1)<originalWidth) {
	            			componentValue = (i-xFirstSampleFloat)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc] 
	            					+ (xSecondSampleFloat-i)*img.YUV_intermediateInterpolated[component][y*interpolatedWidth+(x_sc+1)];
	            			average = xSecondSampleFloat - xFirstSampleFloat;
	            		} else {
	            			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	            			average = 1;
	            		}
            		}
            		
            		//NEIGHBOUR
            		if (interpolationMode == NEIGHBOUR_INTERPOLATION) {
	        			componentValue = img.YUV_intermediateInterpolated[component][y*interpolatedWidth+x_sc];
	        			average = 1;
            		}
            		
            		if (edgeFirstSample || edgeSecondSample)
                		img.edgesInterpolated[component][y*interpolatedWidth+i] = EDGE;
        			
            		
            		img.YUV_interpolated[component][y*interpolatedWidth+i]= (int) (componentValue / average);
            	
            		
            	}

            	xFirstSampleFloat = xSecondSampleFloat;
            	xFirstSample = xSecondSample;
                xSecondSampleFloat+=pppx;     
	        }//x
	    }//y 
	}
}
