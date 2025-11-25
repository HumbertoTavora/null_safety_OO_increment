package loo2.plp.orientadaObjetos2.comando;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.ChamadaProcedimento;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoJaDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ProcedimentoNaoDeclaradoException;
import loo2.plp.orientadaObjetos1.excecao.execucao.EntradaInvalidaException;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.ListaExpressao;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.DefClasse;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;

public class ChamadaMetodoSafeCall extends ChamadaMetodoOO2 {

    public ChamadaMetodoSafeCall(Expressao expressao, Id nomeMetodo, ListaExpressao parametrosReais) {
        super(expressao, nomeMetodo, parametrosReais);
    }

    @Override
    public AmbienteExecucaoOO1 executar(AmbienteExecucaoOO1 ambiente)
        throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException,
               ProcedimentoNaoDeclaradoException, ProcedimentoJaDeclaradoException,
               ObjetoJaDeclaradoException, ObjetoNaoDeclaradoException,
               ClasseNaoDeclaradaException, ClasseJaDeclaradaException, EntradaInvalidaException {
        
        Valor v = expressao.avaliar(ambiente);
        // Safe call: if the expression evaluates to null, do nothing
        if (v instanceof ValorNull) {
            return ambiente;
        }
        
        // Otherwise, proceed with normal method call
        return super.executar(ambiente);
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException, ClasseNaoDeclaradaException {
        // Safe call allows null targets - if target is null, the call is valid (does nothing)
        Tipo tipoClasse = expressao.getTipo(ambiente);
        if (tipoClasse.equals(TipoClasse.TIPO_NULL)) {
            return true;
        }
        
        // Otherwise, check type normally
        return super.checaTipo(ambiente);
    }
}

