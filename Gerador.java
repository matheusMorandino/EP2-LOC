package entradas_ajustadas;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Random;

public class Gerador {
	static Random rand = new Random();
	
	public static void main(String[] args) throws Exception {
		int[] n = {30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85};
		int[] k = {5, 10, 15, 20, 25, 30};
		int id = 1;
		
		for(int i = 0; i < n.length; i++) {
			for(int j = 0; j < k.length; j++) {
				geraInstancia(n[i], k[j], id++);
			}
		}
	}
	
	static void geraInstancia(int n, int k, int id) throws Exception {
		int[] c = new int[n]; 
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int nextColor = 0;
		
		for(int i = 0; i < n; i++) {
			c[i] = rand.nextInt(k);
			
			if(!map.containsKey(c[i])) {
				map.put(c[i], nextColor++);
			}
			
			c[i] = map.get(c[i]);
		}

		String id_ = id > 9 ? id + "" : "0" + id;
		String file = "entrada_" + id_ + "_" + n + "_" + nextColor + ".txt";
		FileOutputStream out = new FileOutputStream(file);
		
		out.write((n + " " + nextColor + "\n").getBytes());
		
		for(int j = 0; j < n; j++) {
			out.write((c[j] + "\n").getBytes());
		}
		
		out.close();
	}
}
