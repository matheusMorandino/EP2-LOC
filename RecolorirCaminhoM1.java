import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import gurobi.*;

public class RecolorirCaminhoM1 {

	public class No{
		int[] cor;
		
		No(int[] cor){
			this.cor = cor;
		}
		
	}

	public class Caminho{
		String cenario;
		int num_nos;
		int num_cores;
		No[] lista_nos;
		GRBVar[][] recolorir;
		String local;

		Caminho(String cenario, int num_nos, int num_cores, int[] lista_nos, String local){
			this.cenario = cenario;
			this.num_nos = num_nos;
			this.num_cores = num_cores;
			this.lista_nos = criaListaDeNos(lista_nos);
			this.local = local;
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
	
	void salvar_solucao(String nome_arquivo, String solucao, String tipo, String local) {
		try {
			FileWriter myWriter = new FileWriter(local + nome_arquivo + tipo);
			myWriter.write(solucao);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	void montaModeloEResolve(Caminho caminho) throws GRBException{
		// Criando um model 'vazio' para o gurobi
        GRBEnv env = new GRBEnv(caminho.local + caminho.cenario + ".log");
		GRBModel model = new GRBModel(env);
		model.set(GRB.DoubleParam.TimeLimit, 1800.0);

		//Adicionando variavel no modelo para arcos percorridos por cada veiculo
		//Definindo variável recolorir(x) como binária[Restrição 5.3]
		caminho.recolorir = new GRBVar[caminho.num_nos][caminho.num_cores];
        for(int i = 0; i < caminho.num_nos; i++){
            for(int j = 0; j < caminho.num_cores; j++){
                caminho.recolorir[i][j] = model.addVar(0.0, 1.0, caminho.lista_nos[i].cor[j], GRB.BINARY, null);
            }
		}

		// função objetivo é de maximizacão ou minimizacão?
		model.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);

		//Restrição 5.1: Um vertice pode ser colorido apenas uma vez
		for(int i=0; i < caminho.num_nos; i++){
			GRBLinExpr expr5_1 = new GRBLinExpr();
			for(int j=0; j < caminho.num_cores; j++){
				expr5_1.addTerm(1.0, caminho.recolorir[i][j]);
			}
			model.addConstr(expr5_1, GRB.EQUAL, 1, null);
		}

		
		//Restrição 5.2: Faceta de corte
		for(int i=0; i < caminho.num_cores; i++){

			for(int p=0; p < caminho.num_nos; p++){
				for(int r=p+2; r < caminho.num_nos; r++){
					for(int q=p+1; q < r; q++){
						GRBLinExpr expr5_2 = new GRBLinExpr();
						expr5_2.addTerm(1.0, caminho.recolorir[p][i]);
						expr5_2.addTerm(-1.0, caminho.recolorir[q][i]);
						expr5_2.addTerm(1.0, caminho.recolorir[r][i]);
						model.addConstr(expr5_2, GRB.LESS_EQUAL, 1, null);
					}
				}
			}

		}

		// chama o solver para resolver o modelo 
		model.optimize();

		//Retornando solução e salvando em um novo arquivo
		String json = model.getJSONSolution();
        System.out.println("JSON solution :" + json);
		int[] resultados = desvetorizaResultado(model.get(GRB.DoubleAttr.X , caminho.recolorir));
		salvar_solucao(caminho.cenario, model.getJSONSolution(), ".json", caminho.local);
		salvar_solucao(caminho.cenario, Arrays.toString(resultados)+"_resultado", ".txt", caminho.local);
	}

	int[] desvetorizaResultado(double[][] res){
		int[] vect = new int[res.length];
		for(int i=0; i < res.length; i++){
			int n = 0;
			for(int j=0; j < res[0].length; j++){
				n += (int)Math.round(res[i][j]*(j+1));
			}
			vect[i] = n-1;
			n=0;
		}
		return vect;
	}

	String extraiNomeInstancia(String caminho) {
		String[] aux = caminho.split("/", 999999);
		return aux[aux.length-1].split(".txt", 999999)[0];
	}

	void resolveInstancia(String nomeArq, String local) throws Exception {
        Scanner in = new Scanner(new File(nomeArq));
		String cenario = extraiNomeInstancia(nomeArq);
	    int num_nos = in.nextInt();
        int num_cores = in.nextInt();
		int[] lista_nos = new int[num_nos];
        
        for(int i=0; i < num_nos; i++) {
			lista_nos[i] = in.nextInt();
		}

		Caminho caminho = new Caminho(cenario, num_nos, num_cores, lista_nos, local);

		montaModeloEResolve(caminho);
    }

	void resolvePacoteInstancias(String path, String local) throws Exception {
        for (File file : new File(path).listFiles()) {
            resolveInstancia(path+"/"+file.getName(), local);  
        }
    }

}
