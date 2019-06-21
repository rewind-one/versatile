package one.rewind.versatile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GapFinder {

	/**
	 *
	 * @param length
	 * @param max
	 * @return
	 */
	public static List<Integer> genList(int length, int max) {

		List<Integer> list = new ArrayList<>();
		for(int i=0; i<length; i++) {

			list.add((int) Math.ceil(new Random().nextInt(max) * (1-((double)(Math.abs(i-length/2))/length))));
		}
		return list;
	}

	/**
	 *
	 * @param list
	 * @return
	 */
	public static List<Integer> getFilledList(List<Integer> list) {

		long t1 = System.currentTimeMillis();

		List<Integer> filledList = IntStream.range(0, list.size()).parallel()
				.boxed()
				.map(i -> (int) Math.min(leftMax(i, list), rightMax(i, list)))
				.collect(Collectors.toList());

		System.out.println("Time usage: " + (System.currentTimeMillis() - t1));

		return filledList;
	}

	/**
	 * 将图案的注水部分填充颜色
	 */
	public static BufferedImage fillGap(List<Integer> list, List<Integer> filledList, BufferedImage image){

		int max = list.stream().max(Integer::compare).get() * 4;

		Graphics2D g = image.createGraphics();  // not sure on this line, but this seems more right
		g.setColor(Color.cyan);

		for(int i=0; i<list.size(); i++) {

			int x = i*4;
			int w = 4;
			int y = max - filledList.get(i) * 4;
			int h = (filledList.get(i) - list.get(i))* 4;
			g.fillRect(x , y,  w, h);
		}

		g.setColor(Color.CYAN);

		return image;
	}

	/**
	 * 计算当前位置左侧的最高高度
	 */
	public static int leftMax(int i, List<Integer> list){
		int temp = list.get(i);

		for(int j=i-1; j>=0; j--){
			if(list.get(j) <= temp)
				continue;
			else{
				if(temp<list.get(j))
					temp=list.get(j);
			}
		}
		return temp;
	}

	/**
	 * 计算当前位置右侧的最高高度
	 * @param i
	 * @param list
	 * @return
	 */
	public static int rightMax(int i, List<Integer> list){

		int temp=list.get(i);

		for(int k=i+1;k<list.size();k++){
			if(list.get(k)<=temp)
				continue;
			else{
				if(temp<list.get(k))
					temp=list.get(k);
			}
		}
		return temp;
	}

	/**
	 *
	 * @param list
	 * @return
	 */
	public static BufferedImage genImage(List<Integer> list) {

		int height = list.stream().max(Integer::compare).get() * 4;

		BufferedImage image = new BufferedImage(list.size() * 4, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();  // not sure on this line, but this seems more right
		g.setBackground(Color.white);

		g.setColor(Color.white);
		g.fillRect(0 , 0,  list.size() * 4, height);

		g.setColor(Color.blue);

		for(int i=0; i<list.size(); i++){

			int x = i*4;
			int w = 4;
			int y = height - list.get(i) * 4;
			int h = list.get(i) * 4;

			g.fillRect(x , y,  w, h);
		}
        /*//将图案的注水部分填充颜色
        fillGap(list, g, height);*/
		return image;
	}

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {

		int length = 100;
		int max = 100;

		List<Integer> list = genList(length,max);

		System.err.println(list.stream().map(String::valueOf).collect(Collectors.joining(",")));

		List<Integer> filledList = getFilledList(list);

		System.err.println(filledList.stream().map(String::valueOf).collect(Collectors.joining(",")));

		BufferedImage image = genImage(list);

		fillGap(list, filledList, image);

		ImageIO.write(image, "png", new File("water.png"));

	}
}
