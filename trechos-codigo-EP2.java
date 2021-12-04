
// -------------------------------------------------------------------
// --- Trechos de código a serem inseridos onde o modelo é montado ---
// ---          OBS: só quando for colocar cortes e lazy           ---
// -------------------------------------------------------------------
modeloGurobi.setCallback(new MeuCallback(x)));
modeloGurobi.set(GRB.IntParam.PreCrush, 1);
modeloGurobi.set(GRB.IntParam.LazyConstraints,1);
// -------------------------------------------------------------------

			
public class MeuCallback extends GRBCallback {
   GRBVar[][] x = null; 

   /* Se quiser, modifique a classe Corte como quiser */
   class Corte {
      int r;
      int c;
      int t;
      int[] p;
      int[] q; 
   }
   	
   public MeuCallback(GRBVar[][] x /* provavelmente precisará de outros parâmetros */) {
      this.x = x;
   }

   private boolean ehHoraCertaDeIncluirCortes() {
      return where == GRB.CB_MIPNODE && getIntInfo(GRB.CB_MIPNODE_STATUS) == GRB.OPTIMAL;
   }
   
   private boolean ehHoraCertaDeIncluirLazyConstr() {
      return where == CB_MIPSOL;
   }
   
   @Override
   protected void callback() {
      try {
         if (ehHoraCertaDeIncluirCortes()) {
            double[][] x_vals = getNodeRel(x);
            Corte corte = separa(x_vals);

            if(corte != null) {
               adicionaCorte(corte);
            }
         } else if (ehHoraCertaDeIncluirLazyConstr()) {
            double[][] x_vals = getSolution(x);
            Corte lazy = separa(x_vals);

            if(lazy != null) {
               adicionaLazy(lazy);
            }         
         }
      } catch (GRBException e) {
         e.printStackTrace();
      }
   }
		
   private GRBLinExpr montaLadoEsqIneq(Corte corte) {
      GRBLinExpr expr = new GRBLinExpr();
	
      // para todo coeficiente não nulo no corte		
      expr.addTerm(coeficiente_de_x_vc, x[v][c]);
 
      return expr;
   }
   		
   private void adicionaCorte(Corte corte) throws GRBException {
      GRBLinExpr expr = montaLadoEsqIneq(corte);
      addCut(expr, GRB.LESS_EQUAL, rhs);
   }
   
   private void adicionaLazy(Corte lazy) throws GRBException {
      GRBLinExpr expr = montaLadoEsqIneq(lazy);
      addLazy(expr, GRB.LESS_EQUAL, rhs);
   }
   
   private Corte separa(double[][] x_vals) {
      // roda o algoritmo de separação para cada cor
      // se encontrar desigualdade violada para alguma cor, inclui a restrição e para
      // a violação deve ser de pelo menos EPSILON = 0.01
      // se não encontrar desigualdade violada, devolve null
   }
}
