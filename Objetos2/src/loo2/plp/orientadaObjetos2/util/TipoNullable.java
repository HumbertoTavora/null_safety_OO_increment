package loo2.plp.orientadaObjetos2.util;

import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;

/**
 * Classe que representa um tipo nullable, ou seja, um tipo que pode ser null.
 * Como Augusto explicou na apresentação parcial, apenas tipos de classe podem ser nullable (não tipos primitivos).
 */
public class TipoNullable implements Tipo {
    
    private TipoClasse tipoBase;
    
    /**
     * Construtor.
     * @param tipoBase O tipo de classe que pode ser null.
     */
    public TipoNullable(TipoClasse tipoBase) {
        this.tipoBase = tipoBase;
    }
    
    /**
     * Retorna o tipo base.
     * @return O tipo base.
     */
    public TipoClasse getTipoBase() {
        return tipoBase;
    }
    
    @Override
    public Id getTipo() {
        return tipoBase.getTipo();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TipoNullable) {
            TipoNullable outro = (TipoNullable) obj;
            return tipoBase.equals(outro.tipoBase);
        }
        if (obj instanceof TipoClasse) {
            TipoClasse outro = (TipoClasse) obj;
            return tipoBase.equals(outro) || outro.equals(TipoClasse.TIPO_NULL);
        }
        return false;
    }
    
    @Override
    public boolean eValido(AmbienteCompilacaoOO1 ambiente) throws ClasseNaoDeclaradaException {
        return tipoBase.eValido(ambiente);
    }
    
    /**
     * Verifica se um tipo é compatível com este tipo nullable.
     * Um tipo nullable aceita seu tipo base ou null.
     * @param outroTipo O tipo a ser verificado.
     * @return true se o tipo é compatível, false caso contrário.
     */
    public boolean eCompativel(Tipo outroTipo) {
        if (outroTipo instanceof TipoClasse) {
            TipoClasse tipoClasse = (TipoClasse) outroTipo;
            return tipoBase.equals(tipoClasse) || tipoClasse.equals(TipoClasse.TIPO_NULL);
        }
        if (outroTipo instanceof TipoNullable) {
            TipoNullable tipoNullable = (TipoNullable) outroTipo;
            return tipoBase.equals(tipoNullable.tipoBase);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return tipoBase.toString() + "?";
    }
}

