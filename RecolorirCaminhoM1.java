import java.io.File;
import java.util.Scanner;


public class RecolorirCaminhoM1 {

	public class No{
		int[] cor;
		
		No(int[] cor){
			this.cor = cor;
		}
		
	}

	public class Caminho{
		int num_nos;
		int num_cores;
		No[] lista_nos;

		Caminho(int num_nos, int num_cores, int[] lista_nos){
			this.num_nos = num_nos;
			this.num_cores = num_cores;
			this.lista_nos = criaListaDeNos(lista_nos);
		}

		No[] criaListaDeNos(int[] lista_nos){
			No[] lista = new No[this.num_nos];
			for(int i=0; i < num_nos; i++)	
				lista[i] =  new No(vetorizaCor(lista_nos[i]));
			return lista;
		}

		int[] vetorizaCor(int cor){
			int[] cor_vect = new int[this.num_cores];
			for(int i=0; i < num_cores; i++)	
				cor_vect[i] = i != cor ? 0 : 1;
			return cor_vect;
		}
	}

	void montaModeloEResolve(Caminho caminho){
		
	}

	void resolveInstancia(String nomeArq) throws Exception {
        Scanner in = new Scanner(new File(nomeArq));
	    int num_nos = in.nextInt();
        int num_cores = in.nextInt();
		int[] lista_nos = new int[num_nos];
        
        for(int i=0; i < num_nos; i++) {
			lista_nos[i] = in.nextInt();
		}

		Caminho caminho = new Caminho(num_nos, num_cores, lista_nos);

		montaModeloEResolve(caminho);
    }

    public static void main(String[] args) throws Exception {
		RecolorirCaminhoM1 recolorirCaminho = new RecolorirCaminhoM1();
		
		String diretorioProjeto = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC";
		
		recolorirCaminho.resolveInstancia(diretorioProjeto + "/entradas/entrada_01_25_5.txt");
	}
}
