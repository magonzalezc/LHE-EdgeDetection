import java.awt.image.BufferedImage;

public class Image {
	
	int width, height;
	int interpolatedWidth, interpolatedHeight;
	
	public int[][] YUV;
	public int[][] YUV_result;// resulting luminance and chrominance components after 1st LHE encoding process
	public int[][] YUV_resultHorizontal;// resulting luminance and chrominance components after 1st LHE encoding process
	public int[][] YUV_resultVertical;// resulting luminance and chrominance components after 1st LHE encoding process

	public int[][] hops;// hops[0] is the hops of luminance, hops[1] and hops[2] are chrominance
	public int[][] hopsHorizontal;// hops[0] is the hops of luminance, hops[1] and hops[2] are chrominance
	public int[][] hopsVertical;// hops[0] is the hops of luminance, hops[1] and hops[2] are chrominance
	
	public int[][] edgesPartial;
	public int[][] edges;
	public int[][] edgesVerticalInterpolated;
	public int[][] edgesInterpolated;

	public int[][] YUV_interpolated;// resulting luminance and chrominance components after 1st LHE encoding process
	public int[][] YUV_intermediateInterpolated;// resulting luminance and chrominance components after 1st LHE encoding process

	public Image (BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		initOriginalImageArrays(width, height);
		
	}
	
	public void initInterpolatedImage (int interpolatedWidth, int interpolatedHeight) {
		this.interpolatedWidth = interpolatedWidth;
		this.interpolatedHeight = interpolatedHeight;
		
		YUV_intermediateInterpolated=new int[3][interpolatedWidth*interpolatedHeight];
		YUV_interpolated=new int[3][interpolatedWidth*interpolatedHeight];	
		edgesVerticalInterpolated = new int[3][interpolatedWidth*interpolatedHeight];
		edgesInterpolated = new int[3][interpolatedWidth*interpolatedHeight];
	}
	
	public void initOriginalImageArrays (int width, int height) {
		
		YUV=new int[3][width*height];
		YUV_result=new int[3][width*height];
		YUV_resultHorizontal=new int[3][width*height];
		YUV_resultVertical=new int[3][width*height];
		hops=new int[3][width*height];
		hopsHorizontal=new int[3][width*height];
		hopsVertical=new int[3][width*height];
		edgesPartial=new int[3][width*height];
		edges=new int[3][width*height];
	}
}
