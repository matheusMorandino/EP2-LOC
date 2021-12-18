public class Main {
    public static void main(String[] args) throws Exception {
		RecolorirCaminhoM1 recolorirCaminhoM1 = new RecolorirCaminhoM1();
        RecolorirCaminhoM2 recolorirCaminhoM2 = new RecolorirCaminhoM2();
		
		String diretorioProjetoM1 = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas1";
		String salvarM1 = "resultadosM1/";

        String diretorioProjeto_ajustadoM1 = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas_ajustadas";
		String salvar_ajustadoM1 = "resultadosM1_ajustados/";

        String diretorioProjetoM2 = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas2";
		String salvarM2 = "resultadosM2/";

        String diretorioProjeto_ajustadoM2 = "C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas_ajustadas";
		String salvar_ajustadoM2 = "resultadosM2_ajustados/";

		recolorirCaminhoM1.resolvePacoteInstancias(diretorioProjetoM1, salvarM1);
		recolorirCaminhoM2.resolvePacoteInstancias(diretorioProjetoM2, salvarM2);
        recolorirCaminhoM1.resolvePacoteInstancias(diretorioProjeto_ajustadoM1, salvar_ajustadoM1);
        recolorirCaminhoM2.resolvePacoteInstancias(diretorioProjeto_ajustadoM2, salvar_ajustadoM2);
		
	}
}
