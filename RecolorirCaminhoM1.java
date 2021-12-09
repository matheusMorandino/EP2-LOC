import java.io.File;
import java.util.Scanner;

import gurobi.*;

public class RecolorirCaminhoM1 {

	public class No{
		int[] cor;
		GRBVar[] recolorir;
		
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

	void montaModeloEResolve(Caminho caminho) throws GRBException{
		// Criando um model 'vazio' para o gurobi
        GRBEnv env = new GRBEnv("malha.log");
		GRBModel model = new GRBModel(env);

		//Adicionando variavel no modelo para arcos percorridos por cada veiculo
		//Definindo variável recolorir(x) como binária[Restrição 5.3]
        for(int i = 0; i < caminho.num_nos; i++){
			caminho.lista_nos[i].recolorir = new GRBVar[caminho.num_cores];
            for(int j = 0; j < caminho.num_cores; j++){
                caminho.lista_nos[i].recolorir[j] = model.addVar(0.0, 1.0, caminho.lista_nos[i].cor[j], GRB.BINARY, null);
            }
		}

		// função objetivo é de maximizacão ou minimizacão?
		model.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);

		//Restrição 5.1: Um vertice pode ser colorido apenas uma vez
		for(int i=0; i < caminho.num_nos; i++){
			GRBLinExpr expr = new GRBLinExpr();
			for(int j=0; j < caminho.num_cores; j++){
				expr.addTerm(1.0, caminho.lista_nos[i].recolorir[j]);
			}
			model.addConstr(expr, GRB.EQUAL, 1, null);
		}

		
		//Restrição 5.2: Faceta de corte
		for(int i=0; i < caminho.num_cores; i++){

			for(int p=0; p < caminho.num_nos; p++){
				for(int r=p+2; r < caminho.num_nos; r++){
					for(int q=p+1; q < r; q++){
						GRBLinExpr expr = new GRBLinExpr();
						expr.addTerm(1.0, caminho.lista_nos[p].recolorir[i]);
						expr.addTerm(-1.0, caminho.lista_nos[q].recolorir[i]);
						expr.addTerm(1.0, caminho.lista_nos[r].recolorir[i]);
						model.addConstr(expr, GRB.LESS_EQUAL, 1, null);
					}
				}
			}

		}

		// chama o solver para resolver o modelo 
		model.optimize();

        System.out.println("JSON solution :" + model.getJSONSolution());
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

	void resolvePacoteInstancias(String path) throws Exception {
        for (File file : new File(path).listFiles()) {
            resolveInstancia(path+"/"+file.getName());  
        }
    }

    public static void main(String[] args) throws Exception {
		RecolorirCaminhoM1 recolorirCaminho = new RecolorirCaminhoM1();
		
		String diretorioProjeto = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas";

		recolorirCaminho.resolvePacoteInstancias(diretorioProjeto);
		
		//recolorirCaminho.resolveInstancia(diretorioProjeto + "/entrada_01_25_5.txt");
	}
}
