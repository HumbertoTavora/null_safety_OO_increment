package loo2.plp.orientadaObjetos2.expressao.binaria;

import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;

public class ExpElvis implements Expressao {
    private Expressao cond;
    private Expressao alt;

    public ExpElvis(Expressao cond, Expressao alt) {
        this.cond = cond;
        this.alt = alt;
    }

    @Override
    public Valor avaliar(AmbienteExecucaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
               ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        Valor v = cond.avaliar(ambiente);
        if (v instanceof ValorNull) {
            return alt.avaliar(ambiente);
        }
        return v;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        if (!cond.checaTipo(ambiente) || !alt.checaTipo(ambiente)) {
            return false;
        }
        Tipo tipoCond = cond.getTipo(ambiente);
        Tipo tipoAlt = alt.getTipo(ambiente);
        
        if (tipoCond.equals(TipoClasse.TIPO_NULL)) {
            return true;
        }
        
        if (tipoAlt.equals(TipoClasse.TIPO_NULL)) {
            return true;
        }
        
        if (tipoCond instanceof TipoClasse) {
            return tipoCond.equals(tipoAlt);
        }
        
        return tipoCond.equals(tipoAlt);
    }

    @Override
    public Tipo getTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        Tipo tipoCond = cond.getTipo(ambiente);
        if (tipoCond.equals(TipoClasse.TIPO_NULL)) {
            return alt.getTipo(ambiente);
        }
        return tipoCond;
    }

    public Expressao getCond() { return cond; }
    public Expressao getAlt() { return alt; }

    @Override
    public String toString() { return cond + " ?: " + alt; }
}
