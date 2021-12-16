import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import gurobi.*;

public class RecolorirCaminhoM2 {

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

		Caminho(String cenario, int num_nos, int num_cores, int[] lista_nos){
			this.cenario = cenario;
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
	
    public class MeuCallback extends GRBCallback {
        GRBVar[][] x = null; 
        static final double EPSILON = 0.01;
		int n_nos;
        int n_cores;
     
        /* Se quiser, modifique a classe Corte como quiser */
        class Corte {
			double[] mais;
			double[] menos;
			int c;

			Corte(double[] mais, double[] menos, int c) {
				this.mais = mais;
				this.menos = menos;
				this.c = c;
			}
        }
            
        public MeuCallback(GRBVar[][] x, int n_nos, int n_cores) {
            this.x = x;
			this.n_nos = n_nos;
			this.n_cores = n_cores;
        }
     
        private boolean ehHoraCertaDeIncluirCortes() throws GRBException {
            return where == GRB.CB_MIPNODE && getIntInfo(GRB.CB_MIPNODE_STATUS) == GRB.OPTIMAL;
        }
        
        private boolean ehHoraCertaDeIncluirLazyConstr() throws GRBException {
            return where == GRB.CB_MIPSOL;
        }
        
        @Override
        protected void callback() {
            try {
                if (ehHoraCertaDeIncluirCortes()) {
                    double[][] x_vals = getNodeRel(x);
                    Corte corte = separa(x_vals);
     
                    if(corte != null) {
                        adicionaCorte(corte, n_nos, 1);
                    }
                } else if (ehHoraCertaDeIncluirLazyConstr()) {
                    double[][] x_vals = getSolution(x);
                    Corte lazy = separa(x_vals);
     
                    if(lazy != null) {
                        adicionaLazy(lazy, x_vals.length, 1);
                    }         
                }
            } catch (GRBException e) {
                e.printStackTrace();
            }
        }
             
        private void montaLadoEsqIneq(Corte corte, GRBLinExpr expr, int i, int sinal) throws GRBException {
			if(sinal > 0 && i >= 0) {
				int j = argmax(cortaArray(corte.mais, 0, i));
				expr.addTerm(1, x[j][corte.c]);
				montaLadoEsqIneq(corte, expr, j-1, sinal*-1);
			}
			else if(i >= 1) {
				int j = argmax(cortaArray(corte.menos, 1, i));
				if(corte.menos[j] > 0) {
					expr.addTerm(-1, x[j][corte.c]);
					montaLadoEsqIneq(corte, expr, j-1, sinal*-1);
				}
			}
        }
                
        private void adicionaCorte(Corte corte, int i, int sinal) throws GRBException {
            GRBLinExpr expr = new GRBLinExpr();
			montaLadoEsqIneq(corte, expr, i, sinal);
            addCut(expr, GRB.LESS_EQUAL, 1);
        }
        
        private void adicionaLazy(Corte lazy, int i, int sinal) throws GRBException {
            GRBLinExpr expr = new GRBLinExpr();
			montaLadoEsqIneq(lazy, expr, i, sinal);
            addLazy(expr, GRB.LESS_EQUAL, 1);
        }
        
        private Corte separa(double[][] x_vals) {
            // roda o algoritmo de separação para cada cor
            // se encontrar desigualdade violada para alguma cor, inclui a restrição e para
            // a violação deve ser de pelo menos EPSILON = 0.01
            // se não encontrar desigualdade violada, devolve null

			for(int c=0; c < n_cores; c++) {
				double[] v = coletaNosPorCor(x_vals, c);
				double[] mais = new double[v.length];
				double[] menos = new double[v.length];

				mais[0] = v[0];
				mais[1] = v[1];
				menos[1] = v[0] - v[1];

				for(int r=2; r < v.length; r++){
					int p = argmax(cortaArray(mais, 0, r-1));
					int q = argmax(cortaArray(menos, 1, r-1));
					mais[r] = Math.max(v[r], menos[q] + v[r]);
					menos[r] = mais[p] - v[r];
				}

				if(arrayMax(mais) > 1 + EPSILON) {
					return new Corte(mais, menos, c);
				}
				else {
					return null;
				}
			}
			return null;
		}

		private double[] coletaNosPorCor(double[][] x, int c) {
			double[] nos = new double[x.length];

			for(int i=0; i < nos.length; i++) 
				nos[i] = x[i][c];
			return nos;
		}

		private int argmax(double[] array) {
			double max = array[0];
			int re = 0;
			for (int i = 1; i < array.length; i++) {
				if (array[i] > max) {
					max = array[i];
					re = i;
				}
			}
			return re;
		}

		private double[] cortaArray(double[] arr, int min, int max) {
			double[] new_arr = new double[max-min+1];
			for(int i=0; i < new_arr.length; i++) {
				new_arr[i] = arr[i+min];
			}
			return new_arr;
		}

		private double arrayMax(double[] arr) {
			double max = Double.NEGATIVE_INFINITY;
			for(double cur: arr)
				max = Math.max(max, cur);
			return max;
		}

    }

	void montaModeloEResolve(Caminho caminho) throws GRBException{
		// Criando um model 'vazio' para o gurobi
        GRBEnv env = new GRBEnv("malha.log");
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

        //Branch and cut
        model.setCallback(new MeuCallback(caminho.recolorir, caminho.num_nos, caminho.num_cores));
        model.set(GRB.IntParam.PreCrush, 1);
        model.set(GRB.IntParam.LazyConstraints,1);

		// chama o solver para resolver o modelo 
		model.optimize();

		//Retornando solução e salvando em um novo arquivo
        System.out.println("JSON solution :" + model.getJSONSolution());
		salvar_solucao(caminho.cenario, model.getJSONSolution());
	}

    void salvar_solucao(String nome_arquivo, String solucao) {
		try {
			FileWriter myWriter = new FileWriter("resultadosM2/" + nome_arquivo + ".json");
			myWriter.write(solucao);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	String extraiNomeInstancia(String caminho) {
		String[] aux = caminho.split("/", 999999);
		return aux[aux.length-1].split(".txt", 999999)[0];
	}

	void resolveInstancia(String nomeArq) throws Exception {
        Scanner in = new Scanner(new File(nomeArq));
		String cenario = extraiNomeInstancia(nomeArq);
	    int num_nos = in.nextInt();
        int num_cores = in.nextInt();
		int[] lista_nos = new int[num_nos];
        
        for(int i=0; i < num_nos; i++) {
			lista_nos[i] = in.nextInt();
		}

		Caminho caminho = new Caminho(cenario, num_nos, num_cores, lista_nos);

		montaModeloEResolve(caminho);
    }

	void resolvePacoteInstancias(String path) throws Exception {
        for (File file : new File(path).listFiles()) {
            resolveInstancia(path+"/"+file.getName());  
        }
    }

    public static void main(String[] args) throws Exception {
		RecolorirCaminhoM2 recolorirCaminho = new RecolorirCaminhoM2();
		
		String diretorioProjeto = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas";

		//recolorirCaminho.resolvePacoteInstancias(diretorioProjeto);
		
		recolorirCaminho.resolveInstancia(diretorioProjeto + "/entrada_01_25_5.txt");
	}
}
