//Bijay Ranabhat
//Project 6: Part-1


import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Driver 
{
	private static int WIDTH = 128;
	private static int HEIGHT = 128;
	private static int MAX_PIXEL = 255;
	
	private static int[][] orig_pixels;
	private static int[][] pixels;
	private static int [][] avgresult = new int[WIDTH][HEIGHT];  //for average filter
	private static int [][] result = new int[WIDTH][HEIGHT];     //for median filter
	private static int[][] MagEdge= new int[WIDTH][HEIGHT];	    //for Edge Detetction
	private static int[][] Accum;       //for Accumulator Array
	private static int[][] Line = new int[WIDTH][HEIGHT];
	//File Reader
	
	public static void readFile( String file ) throws Exception
	{
		String format;
		int width, height, maxPixel;
		
		Scanner get = new Scanner( new FileReader( file ) );
		
		format = get.next();
		width = get.nextInt();
		height = get.nextInt();
		maxPixel = get.nextInt();
		
		if ( ( width != WIDTH ) || ( height != HEIGHT ) || ( maxPixel != MAX_PIXEL ) )
		{
			System.out.println( "Error in file format. Exiting..." );
			System.exit( 1 );
		}
			
		if ( format.equals("P2") )
		{
			for ( int i = 0; i < WIDTH; i++ )
			{
				for ( int j = 0; j < HEIGHT; j++ )
				{
					orig_pixels[i][j] = get.nextByte( );
				}
			}
		}
		
		if ( format.equals( "P5" ) )
		{
			get.close();
			
			DataInputStream input = new DataInputStream( new FileInputStream( file ) );

			for ( int i = 0; i < 15; i++ )
				input.readUnsignedByte();
			
			for ( int i = 0; i < WIDTH; i++ )
			{
				for ( int j = 0; j < HEIGHT; j++ )
				{
					orig_pixels[i][j] = input.readUnsignedByte();
				}
			}
			
			input.close();
		}
		
	}
	
	
	//Average Filter with writer
	
	public static void avgFilter() throws IOException {
		int average;
		int sum = 0;
		
		//Filtering Corner pixels
		avgresult[0][0]= (pixels[0][0]+pixels[0][1]+pixels[1][0]+pixels[1][1])/4;
		
		avgresult[WIDTH-1][HEIGHT-1]= (pixels[WIDTH-1][HEIGHT-1]+pixels[WIDTH-2][HEIGHT-2]+
				pixels[WIDTH-1][HEIGHT-2]+pixels[WIDTH-2][HEIGHT-1])/4;
		
		avgresult[WIDTH-1][0]=(pixels[WIDTH-1][0]+pixels[WIDTH-2][0]+pixels[WIDTH-2][1]+pixels[WIDTH-1][1])/4;
		
		avgresult[0][HEIGHT-1]=(pixels[0][HEIGHT-1]+pixels[0][HEIGHT-2]+pixels[1][HEIGHT-1]+pixels[1][HEIGHT-2])/4;
	
		//Filtering the left and right edge pixels
		for (int col = 1 ; col < HEIGHT-1 ; col++) {
	        avgresult[0][col] = (pixels[0][col]+pixels[0][col+1]+pixels[0][col-1]+ pixels[1][col+1]+pixels[1][col]+pixels[1][col-1])/6;
	        
	        avgresult[WIDTH - 1][col] = (pixels[WIDTH - 1][col]+pixels[WIDTH - 1][col-1]+pixels[WIDTH - 1][col+1]+
	        		pixels[WIDTH - 2][col]+pixels[WIDTH - 2][col-1]+pixels[WIDTH - 2][col+1])/6;
	    }
		//Filtering the top and bottom edge pixels
	    for (int row = 1 ; row < WIDTH-1 ; row++) {
	    	avgresult[row][0] = (pixels[row][0]+pixels[row+1][0]+pixels[row-1][0]+ pixels[row+1][1]+pixels[row][1]+pixels[row-1][1])/6;
	    	avgresult[row][HEIGHT-1] = (pixels[row][HEIGHT-1]+pixels[row-1][HEIGHT-1]+pixels[row+1][HEIGHT-1]+ 
	    			pixels[row+1][HEIGHT-2]+pixels[row][HEIGHT-2]+pixels[row-1][HEIGHT-2])/6;
	    }
		
		//Filtering inner pixels
		for(int i=1;i<WIDTH-1; i++) {
			for(int j=1; j<HEIGHT-1; j++) {
				
					sum=pixels[i-1][j-1]+pixels[i+1][j+1]+pixels[i][j]+pixels[i-1][j]+pixels[i+1][j]+
							pixels[i][j-1]+pixels[i][j+1]+pixels[i-1][j+1]+pixels[i+1][j-1];
					average= sum/9;
					avgresult[i][j]=average;
			}
		}
		
		BufferedOutputStream writer1 = new BufferedOutputStream( new FileOutputStream("average.pgm" ) );
		
		writer1.write( 'P' );
		writer1.write( '5' );
		writer1.write( ' ' );
		writer1.write( '1' );
		writer1.write( '2' );
		writer1.write( '8' );
		writer1.write( ' ' );
		writer1.write( '1' );
		writer1.write( '2' );
		writer1.write( '8' );
		writer1.write( ' ' );
		writer1.write( '2' );
		writer1.write( '5' );
		writer1.write( '5' );
		writer1.write( ' ' );

		for ( int i = 0; i < WIDTH; i++ )
		{
			for ( int j = 0; j < HEIGHT; j++ )
			{
				writer1.write( avgresult[i][j] ); //Test Average Filter
			   
			}
		}
		
		writer1.close();
	}

	
	//Median Filter with writer 
	
	public static void medFilter() throws IOException {
	
		for(int i = 0; i < WIDTH; i++){
            for(int j = 0; j < HEIGHT; j++){
                int [] buffer = new int[9];
                int count = 0;
                for(int r = i - 1; r <= i + 1; r++){
                    for(int c = j - 1; c <= j + 1; c++){
                        if(r < 0 || r >= WIDTH || c < 0 || c >= HEIGHT) {
                            continue;  //Ignoring the edge pixels and masking beyond the edges
                        }else{
                            buffer[count] = orig_pixels[r][c];
                            count++;
                        }
                    }
                }
                
                //sorting the array
                java.util.Arrays.sort(buffer);
                
                result[i][j]=buffer[count/2];
            }
		}
		BufferedOutputStream writer = new BufferedOutputStream( new FileOutputStream( "median.pgm" ) );
		
		writer.write( 'P' );
		writer.write( '5' );
		writer.write( ' ' );
		writer.write( '1' );
		writer.write( '2' );
		writer.write( '8' );
		writer.write( ' ' );
		writer.write( '1' );
		writer.write( '2' );
		writer.write( '8' );
		writer.write( ' ' );
		writer.write( '2' );
		writer.write( '5' );
		writer.write( '5' );
		writer.write( ' ' );

		for ( int i = 0; i < WIDTH; i++ )
		{
			for ( int j = 0; j < HEIGHT; j++ )
			{
				
			    writer.write( result[i][j]);  //Test Median Filter
			}
		}
		
		writer.close();

	}
	
	//Edge Detection
	//Method will find the magnitude of each pixel and cast it as an image to detect edges
	
	public static void edgeD() throws IOException {
		int[][] delX= new int[WIDTH][HEIGHT];
		int[][] delY= new int[WIDTH][HEIGHT];
		
	//Figuring out delX
		for(int i=0;i<WIDTH; i++) {
			for(int j=0; j<HEIGHT; j++) {
				int a= i-1;
				int b= i+1;
				int c= j+1;
				int d= j-1;
				//ignore the edge pixels
				if(a<0||b<0||c<0||d<0||a>=WIDTH||b>=WIDTH||c>=HEIGHT||d>=HEIGHT) {
					continue;
				}else {
					delX[i][j]=pixels[i-1][j+1]+pixels[i][j+1]+pixels[i+1][j+1]-pixels[i-1][j-1]-pixels[i][j-1]-
							pixels[i+1][j-1];
				}
					
			}
		}
		
		//Figuring out delY
		for(int i=0;i<WIDTH; i++) {
			for(int j=0; j<HEIGHT; j++) {
				int a= i-1;
				int b= i+1;
				int c= j+1;
				int d= j-1;
				//ignore the edge pixels
				if(a<0||b<0||c<0||d<0||a>=WIDTH||b>=WIDTH||c>=HEIGHT||d>=HEIGHT) {
					continue;
				}else {
					delY[i][j]=pixels[i-1][j-1]+pixels[i-1][j]+pixels[i-1][j+1]-pixels[i+1][j-1]-pixels[i+1][j]-
							pixels[i+1][j+1];
				}
					
			}
		}
		
		//Calculating the Magnitude
		for(int i=0;i<WIDTH; i++) {
			for(int j=0; j<HEIGHT; j++) {
				int a= i-1;
				int b= i+1;
				int c= j+1;
				int d= j-1;
				
				//Squaring delX
				int x= delX[i][j]*delX[i][j];
				int y= delY[i][j]*delY[i][j];
				
				//ignore the edge pixels
				if(a<0||b<0||c<0||d<0||a>=WIDTH||b>=WIDTH||c>=HEIGHT||d>=HEIGHT) {
					continue;
				}else {
					MagEdge[i][j]= (int) Math.sqrt(x+y);
				}
					
			}
		}
		
		//finding average
		int sum=0;
		for(int i=0;i<WIDTH; i++) {
			for(int j=0; j<HEIGHT; j++) {
				sum= sum + MagEdge[i][j];	
			}
		}
		int average=sum/(WIDTH*HEIGHT);
		
		
		for(int i=0;i<WIDTH; i++) {
			for(int j=0; j<HEIGHT; j++) {
				if (MagEdge[i][j]>3*average) {//using 3 times the average value as threshold
					MagEdge[i][j]=255;
				}
				else
					MagEdge[i][j]=0;	
			}
		}
		
		BufferedOutputStream writer2 = new BufferedOutputStream( new FileOutputStream( "Edge.pgm" ) );
		
		writer2.write( 'P' );
		writer2.write( '5' );
		writer2.write( ' ' );
		writer2.write( '1' );
		writer2.write( '2' );
		writer2.write( '8' );
		writer2.write( ' ' );
		writer2.write( '1' );
		writer2.write( '2' );
		writer2.write( '8' );
		writer2.write( ' ' );
		writer2.write( '2' );
		writer2.write( '5' );
		writer2.write( '5' );
		writer2.write( ' ' );

		for ( int i = 0; i < WIDTH; i++ )
		{
			for ( int j = 0; j < HEIGHT; j++ )
			{
				
			    writer2.write( MagEdge[i][j]);  //Test Median Filter
			}
		}
		
		writer2.close();
			
	}
	
	//Hough Transform for Line Detetection in a pgm image
	
	//I don't know if this is correct. I followed the algorithm to calculate b and m values for each line
	//It draws the lines.
	
	public static void houghLine() throws IOException{
		double [][]slope=new double[WIDTH][HEIGHT];
		int [][]intercept= new int[WIDTH][HEIGHT];
		
		Accum= new int[WIDTH][HEIGHT];//Accumulator Array
		for(int x=0;x<WIDTH; x++) {
			for(int y=0; y<HEIGHT; y++) {
				Accum[x][y]=0;
			}
		}
		
		
		int Max1=Accum[0][0];
		int bVal=0;
		double mVal = 0;
		
		int Max2 = Accum[0][0];
		int bVal2=0;
		double mVal2=0;
		
		int Max3 = Accum[0][0];
		int bVal3=0;
		double mVal3=0;
		
		for(int x=0; x<MagEdge.length; x++) { // Loop through values for x in the edge detection array
			for(int y=0; y<MagEdge[0].length; y++) { // Loop through values for y in the edge detection array
				if(MagEdge[x][y]==255) {				//if the pixel at x, y is an edge:
					for(double m = -63.5; m<64.5; m++) {//Loop through values for m in the accumulator array:
						//value of m range from -6.35 to 6.45
						//m will later be divided by 10
						int b = (int) (x- ((m/10)*y));//using the y=mx+b equation to calculate the value of b
						if(b<64 && b>-64) {			//setting the range of b to (-64,64)
							Accum[x][y]+=1; 		//incrementing the value in the accumulator array
							slope[x][y]= m/10;		//Storing values of m
							intercept[x][y]=b;	//storing values of b
						
					}
				}
					//Values of m and b for first line
				if(Max1< Accum[x][y]) { //finding the largest value in Accum array
					Max1=Accum[x][y];
					bVal=intercept[x][y];
					mVal=slope[x][y];
					Accum[x][y]=0;	//setting the largest value in the accum array to 0
				}
				if(Max2<Accum[x][y] &&Max2!=Max1 && Max2!=Max3) { //Values of m and b for second line
					Max2=Accum[x][y];
					bVal2=intercept[x][y];
					mVal2=slope[x][y];
					Accum[x][y]=0;
				}
				if(Max3<Accum[x][y] &&Max3!=Max1 && Max3!=Max2) {	//Values of m and b for third line
					Max3=Accum[x][y];
					bVal3=intercept[x][y];
					mVal3=slope[x][y];
					Accum[x][y]=0;
				}
			
		}
		}
		}

		//drawing the lines
		for(int x =0; x<WIDTH; x++) {
			for(int y=0; y<HEIGHT; y++) {
				//used range of 1 to see what happens
				//It showed the line
				//using y=mx+b equation
				if((x < ((int) ( (mVal*y)+bVal)+1)) && (x> ((int) ( (mVal*y)+bVal)-1))){
					Line[x][y]=255;
				}
				if((x < ((int) ( (mVal2*y)+bVal2)+1)) && (x> ((int) ( (mVal2*y)+bVal2)-1))){
					Line[x][y]=255;
				}
				if((x < ((int) ( (mVal3*y)+bVal3)+1)) && (x> ((int) ( (mVal3*y)+bVal3)-1))){
					Line[x][y]=255;
				}
				//else
					//Line[x][y]=0;
					 
					
			}
				
		}
			
		
		BufferedOutputStream writer3 = new BufferedOutputStream( new FileOutputStream( "Line.pgm" ) );
		
		writer3.write( 'P' );
		writer3.write( '5' );
		writer3.write( ' ' );
		writer3.write( '1' );
		writer3.write( '2' );
		writer3.write( '8' );
		writer3.write( ' ' );
		writer3.write( '1' );
		writer3.write( '2' );
		writer3.write( '8' );
		writer3.write( ' ' );
		writer3.write( '2' );
		writer3.write( '5' );
		writer3.write( '5' );
		writer3.write( ' ' );

		for ( int i = 0; i < WIDTH; i++ )
		{
			for ( int j = 0; j < HEIGHT; j++ )
			{
				
			    writer3.write( Line[i][j]);  
			}
		}
		
		writer3.close();
	
}
	
	//Main Function
	
	public static void main( String[] args ) throws Exception
	{
		orig_pixels = new int[WIDTH][HEIGHT];
		pixels = new int[WIDTH][HEIGHT];
		
		Scanner in = new Scanner( System.in );
		
		System.out.println( "Enter a file name: ");
		String file = in.nextLine();
		in.close();
		
		readFile( file );
		
		for ( int i = 0; i < WIDTH; i++ ) {
			for ( int j = 0; j < HEIGHT; j++ ) {
				pixels[i][j] = orig_pixels[i][j];
				//result[i][j]= orig_pixels[i][j];
			}
			
			
		//Testing Functions
			
		avgFilter();
		medFilter();
		edgeD();
		houghLine();
		}
	}
}
