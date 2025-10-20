package loo2.plp.orientadaObjetos1.util;

import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;

/**
 * Classe que representa os possiveis tipos de uma expressao.
 * Agora com suporte a null safety (semelhante ao Kotlin).
 */
public class TipoClasse implements Tipo {

    /**
     * Indica que a expressao associada é uma classe.
     */
    private Id tipoClasse;

    /**
     * Indica se o tipo aceita valores nulos (ex: Pessoa?).
     */
    private boolean aceitaNulo;

    /**
     * Indica que a expressao associada é nula.
     */
    public static final Id NULL = new Id("NULL");

    /**
     * Constante de tipo nulo.
     */
    public static final Tipo TIPO_NULL = new TipoClasse(NULL, true);

    /**
     * Construtor padrão (tipo não-nullable por padrão).
     *
     * @param tipoClasse o tipo da expressao associada.
     */
    public TipoClasse(Id tipoClasse) {
        this(tipoClasse, false);
    }

    /**
     * Construtor com controle de nulabilidade.
     *
     * @param tipoClasse o tipo da expressao associada.
     * @param aceitaNulo indica se o tipo aceita nulo.
     */
    public TipoClasse(Id tipoClasse, boolean aceitaNulo) {
        this.tipoClasse = tipoClasse;
        this.aceitaNulo = aceitaNulo;
    }

    /**
     * Retorna o tipo da expressao associada.
     *
     * @return o tipo da expressao associada.
     */
    @Override
    public Id getTipo() {
        return tipoClasse;
    }

    /**
     * Indica se esta classe é um tipo válido, ou seja, se já tiver sido declarada.
     *
     * @return <code>true</code> se esta classe for um tipo válido (já declarada);
     * <code>false</code> caso contrário.
     */
    @Override
    public boolean eValido(AmbienteCompilacaoOO1 ambiente)
            throws ClasseNaoDeclaradaException {
        boolean resposta;
        try {
            resposta = (tipoClasse == NULL) || (ambiente.getDefClasse(tipoClasse) != null);
        } catch (ClasseNaoDeclaradaException c) {
            resposta = false;
        }
        return resposta;
    }

    /**
     * Retorna se o tipo aceita valores nulos.
     *
     * @return true se o tipo for nullable (ex: Pessoa?), false caso contrário.
     */
    @Override
    public boolean aceitaNulo() {
        return aceitaNulo;
    }

    /**
     * Retorna uma cópia deste tipo como nullable (ex: Pessoa -> Pessoa?).
     *
     * @return novo TipoClasse com aceitaNulo = true.
     */
    @Override
    public Tipo comoNullable() {
        // Se já for nullable, retorna ele mesmo
        if (this.aceitaNulo) return this;
        return new TipoClasse(this.tipoClasse, true);
    }

    /**
     * Retorna uma versão deste tipo marcada como não-nullable (ex: Pessoa? -> Pessoa).
     * Método adicionado para facilitar a checagem da base do tipo.
     *
     * @return um novo TipoClasse com aceitaNulo = false.
     */
    @Override
    public Tipo comoNaoNullable() {
        // Se já for não-nullable, retorna ele mesmo
        if (!this.aceitaNulo) return this;
        return new TipoClasse(this.tipoClasse, false);
    }

    /**
     * Compara este tipo com o tipo dado.
     *
     * @return <code>true</code> se se tratarem do mesmo tipo (e mesma nulabilidade);
     * <code>false</code> caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TipoClasse)) {
            return false;
        }
        TipoClasse outro = (TipoClasse) obj;
        return this.tipoClasse.equals(outro.tipoClasse)
                && this.aceitaNulo == outro.aceitaNulo;
    }

    /**
     * Retorna a descrição textual do tipo.
     * Se o tipo for nullable, adiciona "?" ao final.
     *
     * @return a descrição textual do tipo.
     */
    @Override
    public String toString() {
        return tipoClasse.toString() + (aceitaNulo ? "?" : "");
    }
}