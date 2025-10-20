package loo2.plp.orientadaObjetos1.util;

import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
/**
 * Interface  representando um tipo.
 */
public interface Tipo {
    /**
     * Obtém o tipo, representado por um identficador.
     * @return o tipo
     */
    public Id getTipo();
    /**
     * Compara dois tipos.
     * @param obj O objeto a ser comparado com este tipo.
     * @return true, se o obj é igual a este tipo, false, caso contrário.
     */
    public boolean equals(Object obj);
    /**
     * Verifica se o tipo é válido no ambiente.
     * @param ambiente o ambiente de compilação, que apresenta o mapeamento
     * entre identificadores e tipos.
     * @return true, se o tipo for válido no ambiente, false, caso contrário.
     * @throws ClasseNaoDeclaradaException
     */
    public boolean eValido(AmbienteCompilacaoOO1 ambiente) throws ClasseNaoDeclaradaException;

    /**
     * Indica se este tipo aceita valores nulos (ex: string?).
     * Método adicionado para null-safety.
     *
     * @return true se o tipo aceita null, false caso contrário.
     */
    public default boolean aceitaNulo() {
        return false;
    }

    /**
     * Retorna uma versão deste tipo marcada como nullable (ex: string -> string?).
     * Método adicionado para facilitar parsing/semântica.
     *
     * @return um novo Tipo com aceitaNulo = true.
     */
    Tipo comoNullable();

    /**
     * Retorna uma versão deste tipo marcada como não-nullable (ex: string? -> string).
     * Método adicionado para facilitar a checagem da base do tipo.
     *
     * @return um novo Tipo com aceitaNulo = false.
     */
    Tipo comoNaoNullable();
}