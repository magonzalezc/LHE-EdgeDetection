import java.awt.image.BufferedImage;

public class Main {
	
	static int INTERPOLATED = 2048;

	static int INTERPOLATED_WIDTH = INTERPOLATED;
	static int INTERPOLATED_HEIGHT = INTERPOLATED;

	static String PATH_IMAGE = "./img/lena_bw.bmp";

	public static int INTERPOLATION = 	InterpolationUtils.EDGE_INTERPOLATION1;
	public static boolean EDGES_AT_SAME_TIME = false;

	public static void main(String [ ] args)
	{
		Cache cache = new Cache();
		cache.init();
		
		BufferedImage bufferedImg = ImageUtils.loadImageToBufferedImage(PATH_IMAGE); //Loads img from file
		
		Image img = new Image (bufferedImg); //Creates image model
		ImageUtils.imgToInt(bufferedImg, img.YUV); //Fills original YUV array
				
		//Luminance original image to file
		ImageUtils.YUVtoBMP("./output_debug/orig_YUV_BN.bmp", img.width, img.height, 
				img.YUV[0]);

		//Compress basic frame
		LHEUtils.compressBasicFrame(img); //compressBasic Frame
		
		//Interpolates image
		img.initInterpolatedImage(INTERPOLATED_WIDTH, INTERPOLATED_HEIGHT);
		
		if (EDGES_AT_SAME_TIME) {
			System.out.println(" interpolating edges at same time");

			InterpolationUtils.interpolateVertical_edgesAtSameTime(LHEUtils.LUMINANCE, img, INTERPOLATION);
			InterpolationUtils.interpolateHorizontal_edgesAtSameTime(LHEUtils.LUMINANCE, img, INTERPOLATION);
		} else {
			System.out.println(" interpolating edges");

			InterpolationUtils.interpolateEdgesVertical(LHEUtils.LUMINANCE, img);
			InterpolationUtils.interpolateEdgesHorizontal(LHEUtils.LUMINANCE, img);
			
			System.out.println(" interpolating image");

			InterpolationUtils.interpolateVertical(LHEUtils.LUMINANCE, img, INTERPOLATION);
			InterpolationUtils.interpolateHorizontal(LHEUtils.LUMINANCE, img, INTERPOLATION);
		}

		
		//SAVING RESULTS AND DEBUG OUTPUT
		
		//Ready to save the result in BMP format
		
		System.out.println(" edges image is ./output_img/Edges.bmp");
		ImageUtils.YUVtoBMP("./output_img/EdgesComplete.bmp",img.width, img.height, 
				img.edges[LHEUtils.LUMINANCE]);
		
		ImageUtils.YUVtoBMP("./output_debug/EdgesPartial.bmp",img.width, img.height, 
				img.edgesPartial[LHEUtils.LUMINANCE]);
		
		ImageUtils.YUVtoBMP("./output_debug/EdgesVerticalInterpolated.bmp",img.interpolatedWidth, img.interpolatedHeight, 
				img.edgesVerticalInterpolated[LHEUtils.LUMINANCE]);
		ImageUtils.YUVtoBMP("./output_img/EdgesInterpolated.bmp",img.interpolatedWidth, img.interpolatedHeight, 
				img.edgesInterpolated[LHEUtils.LUMINANCE]);

		System.out.println(" intermediate interpolated image is ./output_debug/IntermediateInterpolate.bmp");
		System.out.println(" final interpolated image is ./output_img/Interpolated.bmp");

		ImageUtils.YUVtoBMP("./output_debug/IntermediateInterpolated.bmp",img.interpolatedWidth, img.interpolatedHeight, 
				img.YUV_intermediateInterpolated[LHEUtils.LUMINANCE]);
		ImageUtils.YUVtoBMP("./output_img/Interpolated.bmp",img.interpolatedWidth, img.interpolatedHeight, 
				img.YUV_interpolated[LHEUtils.LUMINANCE]);

		System.out.println(" result image is ./output_debug/BasicLHE.bmp");
		System.out.println(" result image is ./output_debug/BasicLHE_Horizontal.bmp");
		System.out.println(" result image is ./output_debug/BasicLHE_Vertical.bmp");

		ImageUtils.YUVtoBMP("./output_debug/BasicLHE.bmp",img.width, img.height, 
				img.YUV_result[LHEUtils.LUMINANCE]);
		ImageUtils.YUVtoBMP("./output_debug/BasicLHE_Horizontal.bmp",img.width, img.height, 
				img.YUV_resultHorizontal[LHEUtils.LUMINANCE]);
		ImageUtils.YUVtoBMP("./output_debug/BasicLHE_Vertical.bmp",img.width, img.height, 
				img.YUV_resultVertical[LHEUtils.LUMINANCE]);
		
		System.out.println(" hops are in ./output_debug/HorizontalHops.csv");
		System.out.println(" hops are in ./output_debug/VerticalHops.csv");

		ImageUtils.saveArrayToCSV(img.width, img.height, LHEUtils.LUMINANCE, 
				img.hops, "./output_debug/Hops.csv");
		ImageUtils.saveArrayToCSV(img.width, img.height, LHEUtils.LUMINANCE, 
				img.hopsHorizontal, "./output_debug/HorizontalHops.csv");
		ImageUtils.saveArrayToCSV(img.height, img.height, LHEUtils.LUMINANCE, 
				img.hopsVertical, "./output_debug/VerticalHops.csv");
		
		System.out.println(" original luminances are in ./output_debug/OriginalLuminance.csv");
		ImageUtils.saveArrayToCSV(img.width, img.height, LHEUtils.LUMINANCE, 
				img.YUV, "./output_debug/OriginalLuminance.csv");
		
		ImageUtils.saveArrayToCSV(img.interpolatedWidth, img.interpolatedHeight, LHEUtils.LUMINANCE, 
				img.YUV_intermediateInterpolated, "./output_debug/IntermediateLuminance.csv");
		ImageUtils.saveArrayToCSV(img.interpolatedWidth, img.interpolatedHeight, LHEUtils.LUMINANCE, 
				img.YUV_interpolated, "./output_debug/FinalLuminance.csv");
		
	}
	
}


