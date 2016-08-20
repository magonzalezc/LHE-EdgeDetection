import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

	
	public static BufferedImage loadImageToBufferedImage(String pathImagen) {

		BufferedImage img=null;

		try {	
			img = ImageIO.read(new File(pathImagen));
		} catch (IOException e) {
			System.out.println("error loading image");
			System.exit(0);
		}
		
		return img;
	}
	

	/**
	 * Converts BufferedImage object into YUV array
	 * 
	 * @param img
	 * @param YUV
	 */
	public static void imgToInt(BufferedImage img, int[][] YUV) {
		//this bucle converts BufferedImage object ( which is "img") into YUV array (luminance and chrominance)
		int i=0;
		for (int y=0;y<img.getHeight();y++)  {
			for (int x=0;x<img.getWidth();x++)  {
				int c=img.getRGB(x, y);
	
				int red=(c & 0x00ff0000) >> 16;
				int green=(c & 0x0000ff00) >> 8;
				int blue=(c & 0x000000ff);
		
				//identical formulas used in JPEG . model YCbCr (not pure YUV)
				YUV[0][i]=(red*299+green*587+blue*114)/1000; //lumminance [0..255]
				YUV[1][i]=128+(-168*red - 331*green + 500*blue)/1000; //chroma U [0.255]
				YUV[2][i]=128+ (500*red - 418*green -81*blue)/1000; //chroma V [0..255]
		
				if (YUV[0][i]>255) YUV[0][i]=255;
				if (YUV[1][i]>255)  YUV[1][i]=255;
				if (YUV[2][i]>255)  YUV[2][i]=255;
		
				if (YUV[0][i]<0) YUV[0][i]=0;
				if (YUV[1][i]<0)  YUV[1][i]=0;
				if (YUV[2][i]<0)  YUV[2][i]=0;
		
				i++;	
			}
		}
	}
	

	/**
	 * Saves image component (only one YUV component) in BMP format
	 * 
	 * @param pathImagen
	 * @param width
	 * @param height
	 * @param component
	 */
	public static void YUVtoBMP(String pathImagen, int width, int height, int[] component)
	{
		//save image component Only one YUV component) in BMP format

		BufferedImage buff_c=intToImg(width, height, component);
		saveBufferedImage(pathImagen, buff_c);

	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param component
	 * @return
	 */
	private static BufferedImage intToImg(int width, int height, int[] component) {

		BufferedImage img= new BufferedImage (width,height,BufferedImage.TYPE_INT_RGB);
		
		for (int y=0;y<height;y++)  {
			for (int x=0;x<width;x++)  {

				//the set of formulas must be coherent with formulas used for RGB->YUV
				int i=x+(y)*width;
				int red=component[i];//+(1402*(V[i]-128))/1000;
				int green=component[i];//- (334*(U[i]-128)-714*(V[i]-128))/1000;
				int blue=component[i];//+(177*(U[i]-128))/1000;

				int rgb=red+green*256+blue*65536;
				img.setRGB(x, y, rgb);

			}//x
		}//y
		return img;
	}
	
	/**
	 * 
	 * @param pathImagen
	 * @param buff_img
	 * @return
	 */
	private static boolean saveBufferedImage(String pathImagen, BufferedImage buff_img) {

		if (buff_img != null && pathImagen != null ) {

			try {
				ImageIO.write( buff_img, "BMP", new File(pathImagen));
				return true;
			} catch (Exception e){
				System.out.println("failure creating file");// error at saving
			}
			return false;		
		} else {

			return false;
		}
	}	
	
	public static void saveHopsToTxt(int width, int height, int component, int[][] hops, String path_file)
	{
		try{
			System.out.println("Entrando en salvaTXT");
			DataOutputStream d = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path_file)));


			//primero escribo el ancho, alto y primer color:
			d.writeBytes(width+"\n");
			d.writeBytes(height+"\n");
			for (int i=0;i<width*height;i++){

				if ((i%width==0)&& (i>0)) {d.writeBytes("\n");}



				//esto salva los hops normales[0..4..8]
				//d.writeBytes(hops[0][i]+"");

				//esto los salva con signo
				if (hops[component][i]==0)d.writeBytes("-4");
				else if (hops[component][i]==1)d.writeBytes("-3");
				else if (hops[component][i]==2)d.writeBytes("-2");
				else if (hops[component][i]==3)d.writeBytes("-1");
				else if (hops[component][i]==4)d.writeBytes(" 0");
				else if (hops[component][i]==5)d.writeBytes("+1");
				else if (hops[component][i]==6)d.writeBytes("+2");
				else if (hops[component][i]==7)d.writeBytes("+3");
				else if (hops[component][i]==8)d.writeBytes("+4");
				else d.writeBytes("  ");
			}

			d.close();
		}catch(Exception e){System.out.println("ERROR writing hops in txt format:"+e);}	


	}
	
	public static void saveArrayToCSV(int width, int height, int component, int[][] array, String path_file)
	{
		try{
			DataOutputStream d = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path_file)));

			d.writeBytes(";");

			for (int i=0; i<width; i++) {
				d.writeBytes(i+";");
			}

			d.writeBytes("\n");

			for (int i=0; i<height; i++) {
				d.writeBytes(i+";");

				for (int j=0;j<width;j++){
	
					d.writeBytes(array[component][i*width + j] + ";");

				}
			d.writeBytes("\n");
			}

			d.close();
		}catch(Exception e){System.out.println("ERROR writing hops in txt format:"+e);}	


	}
	
}
