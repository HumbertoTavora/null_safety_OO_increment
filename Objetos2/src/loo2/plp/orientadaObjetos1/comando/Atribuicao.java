package loo2.plp.orientadaObjetos1.comando;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.AcessoAtributo;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.LeftExpression;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull; // Adicionado para checagem de ValorNull
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
/**
 * Classe que representa um comando de atribuio.
 */
public class Atribuicao implements Comando {
    /**
     * Lado esquerdo do comando de atribuio.
     */
    protected LeftExpression av;

    /**
     * Expresso cujo valor ser atribudo ao lado esquerdo.
     */
    protected Expressao expressao;

    /**
     * Construtor.
     * @param av Lado esquerdo
     * @param expressao Expresso cujo valor ser atribudo ao lado esquerdo.
     */
    public Atribuicao(LeftExpression av, Expressao expressao){
        this.av = av;
        this.expressao = expressao;
    }

    /**
     * Executa  a atribuio.
     *
     * @param ambiente o ambiente que contem o mapeamento entre identificadores
     * e valores.
     * @return o ambiente modificado pela execuo da atribuio.
     * @throws ClasseNaoDeclaradaException
     *
     */
    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente)
            throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
            ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {

        // Avalia o valor que será atribuído
        Valor valorAtribuido = expressao.avaliar(ambiente);

        // A checagem de tipos (null safety) deve ser feita em checaTipo.
        // Se quisermos lançar uma exceção de execução se a checagem de tipo falhou (o que não deveria acontecer se checaTipo foi chamado),
        // precisaríamos de uma referência ao ambiente de compilação ou do tipo.
        // Vamos remover a checagem de tipo do executar, pois ela pertence ao checaTipo.

        /*
        // ORIGINALMENTE CAUSAVA O ERRO:
        // Verifica se é nulo
        boolean valorEhNulo = (valorAtribuido == null || valorAtribuido instanceof ValorNull);

        // Recupera o tipo da variável destino (ERRO AQUI: Cast de AmbienteExecucao para AmbienteCompilacao)
        // Tipo tipoVariavel = av.getTipo((AmbienteCompilacaoOO1) ambiente);

        // Se for nulo e o tipo não aceita null, lança exceção
        // if (valorEhNulo && !tipoVariavel.aceitaNulo()) {
        //     System.out.println(
        //             "Tentativa de atribuir 'null' a um tipo não-null: " + tipoVariavel
        //     );
        // }
        */

        // Executa a atribuição normalmente
        Id idVariavel = av.getId();
        if (av instanceof AcessoAtributo) {
            Expressao expAV = ((AcessoAtributo) av).getExpressaoObjeto();
            ValorRef referencia = (ValorRef) expAV.avaliar(ambiente);
            Objeto obj = ambiente.getObjeto(referencia);
            obj.changeAtributo(idVariavel, valorAtribuido);
        } else {
            ambiente.changeValor(idVariavel, valorAtribuido);
        }

        return ambiente;
    }

    /**
     * Um comando de atribuio est bem tipado, se o tipo do identificador
     * o mesmo da expresso. O tipo de um identificador  determinado pelo
     * tipo da expresso que o inicializou (na declarao).
     *
     * @param ambiente o ambiente que contem o mapeamento entre identificadores
     * e valores.
     * @return <code>true</code> se os tipos da atribuio so vlidos;
     * <code>false</code> caso contrario.
     *
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        return expressao.checaTipo(ambiente) &&
                (av.getTipo(ambiente).equals(expressao.getTipo(ambiente))
                        || expressao.getTipo(ambiente).equals(TipoClasse.TIPO_NULL));
    }
}