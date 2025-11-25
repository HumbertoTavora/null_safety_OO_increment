package loo2.plp.orientadaObjetos1.expressao.leftExpression;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoObjeto;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;

/**
 * Classe que representa um acesso de atributo a partir de uma expressao.
 */
public class AcessoAtributoId extends AcessoAtributo{
    /**
     * Expressao que acessa o atributo.
     */
    protected LeftExpression av;
    /**
     * Indica se o tipo da expressão é nullable (armazenado durante checaTipo)
     */
    protected boolean tipoNullable = false;
    /**
     * Construtor.
     * @param av Expressao do lado esquerdo, que acessa o atributo.
     * @param id O atributo sendo acessado.
     */
    public AcessoAtributoId(LeftExpression av, Id id){
        super(id);
        this.av = av;
    }
    /**
     * Avalia esse acesso de atributo obtendo o valor do atributo no ambiente.
     * @param ambiente o ambiente de execu��o, que apresenta o mapeamento de
     * identificadores a valores.
     * @return o valor do atributo acessado no ambiente.
     * @throws VariavelNaoDeclaradaException
     * @throws VariavelJaDeclaradaException
     * @throws ObjetoNaoDeclaradoException
     */
    public Valor avaliar(AmbienteExecucaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
               ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException{
        return obterValorDeIdNoAmbiente(ambiente);
    }
    /**
     * Obt�m a express�o acessadora do atributo.
     * @return a express�o acessadora do atributo.
     */
    public Expressao getExpressaoObjeto(){
        return av;
    }
    /**
     * Verifica se os atributos associados foram declarados e se seus tipos
     * existem no ambiente.
     * @param ambiente o ambiente de compila��o, com o mapeamento de identificadores
     * a tipos.
     * @return true, se as vari�veis acessadas j� foram declaradas e seus
     * tipos existem.
     * @throws VariavelNaoDeclaradaException
     * @throws ClasseNaoDeclaradaException
     */
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException{
        boolean resposta = false;
        if(av.checaTipo(ambiente)) {
            try{
                Tipo t = av.getTipo(ambiente);
                // Se o tipo for nullable, permitir acesso (retorna null se o objeto for null)
                if (t instanceof loo2.plp.orientadaObjetos2.util.TipoNullable) {
                    loo2.plp.orientadaObjetos2.util.TipoNullable tipoNullable = (loo2.plp.orientadaObjetos2.util.TipoNullable) t;
                    TipoClasse tipoBase = tipoNullable.getTipoBase();
                    DefClasse defClasse = ambiente.getDefClasse(tipoBase.getTipo());
                    defClasse.getTipoAtributo(super.getId());
                    this.tipoNullable = true; // Marcar que o tipo é nullable
                    resposta = true;
                } else if (t instanceof TipoClasse) {
                    DefClasse defClasse = ambiente.getDefClasse(((TipoClasse)t).getTipo());
                    defClasse.getTipoAtributo(super.getId());
                    this.tipoNullable = false; // Marcar que o tipo NÃO é nullable
                    resposta = true;
                }
            }
            catch(VariavelNaoDeclaradaException atrib){
                resposta = false;
            }
            catch(ClasseNaoDeclaradaException clas){
                resposta = false;
            }

        }
        return resposta;
    }
    /**
     * Obt�m o tipo do atributo no ambiente.
     * @param ambiente que apresenta o mapeamento de identificadores a tipos.
     * @return o tipo do atributo acessado.
     * @throws VariavelNaoDeclaradaException
     * @throws ClasseNaoDeclaradaException
     */
    public Tipo getTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException{
          //Logo abaixo obtenho a definicao da Classe (seus m�todos e atributos).
          //av.getTipo devera retornar uma instancia de TipoClasse e assim, TipoClasse.getTipo()
          //retorna o id (contendo o nome da classe) associado ao tipo dela
        Tipo tipoObjeto = av.getTipo(ambiente);
        TipoClasse tipoClasse = null;
        
        // Se o tipo for nullable, usar o tipo base
        if (tipoObjeto instanceof loo2.plp.orientadaObjetos2.util.TipoNullable) {
            loo2.plp.orientadaObjetos2.util.TipoNullable tipoNullable = (loo2.plp.orientadaObjetos2.util.TipoNullable) tipoObjeto;
            tipoClasse = tipoNullable.getTipoBase();
        } else if (tipoObjeto instanceof TipoClasse) {
            tipoClasse = (TipoClasse) tipoObjeto;
        }
        
        if (tipoClasse == null) {
            throw new ClasseNaoDeclaradaException(new Id("TipoInvalido"));
        }
        
        Id nomeClasse = tipoClasse.getTipo();
        DefClasse defClasse = ambiente.getDefClasse(nomeClasse);
        Tipo tipoAtr = defClasse.getTipoAtributo(super.getId());
        //Em seguida retorno o tipo do atributo, caso ele esteja definido na classe.
        //caso n�o esteja, uma exce��o ser� lan�ada
        return tipoAtr;
    }
    /**
     * Obt�m a expressao que acessa o atributo.
     * @return a LeftExpression que representa a express�o que acessa o atributo.
     */
    public LeftExpression getAv() {
        return av;
    }

    /**
     * Retorna o valor do Objeto representado por um certo id
     * @param ambiente � o Ambiente de Execu��o
     * @return o valor do Objeto representado por um certo id
     * @throws ClasseNaoDeclaradaException 
     */
    private Valor obterValorDeIdNoAmbiente(AmbienteExecucaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
               ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        
    	// Pegando o objeto no ambiente
        Valor valor = av.avaliar(ambiente);
        
        // Se o valor for null, verificar se o tipo é nullable
        // Se for nullable, retornar null; se não for, lançar exceção
        if (valor instanceof ValorNull) {
            if (tipoNullable) {
                // Tipo nullable: permitir acesso e retornar null
                return new ValorNull();
            } else {
                // Tipo não-nullable: lançar exceção de null pointer
                throw new NullPointerException("Cannot access attribute on null value. Use nullable type (Tipo?) or safe call operator (?.)");
            }
        }
        
        // Se não for null, proceder normalmente
        ValorRef referencia = (ValorRef) valor;
        Objeto objeto = ambiente.getObjeto(referencia);
        
        // Recuperando o mapeamento de valores do objeto (atributos do objeto)
        ContextoObjeto aux = objeto.getEstado();
        
        // Recuperando o valor do atributo "id" do objeto
        return aux.get(super.getId());
    }
}