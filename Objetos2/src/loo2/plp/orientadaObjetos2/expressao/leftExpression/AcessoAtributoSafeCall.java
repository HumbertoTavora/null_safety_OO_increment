package loo2.plp.orientadaObjetos2.expressao.leftExpression;

import loo2.plp.orientadaObjetos1.expressao.leftExpression.LeftExpression;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.AcessoAtributo;
import loo2.plp.orientadaObjetos1.expressao.Expressao;
import loo2.plp.orientadaObjetos1.expressao.valor.Valor;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorNull;
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.memoria.ContextoObjeto;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ObjetoNaoDeclaradoException;

public class AcessoAtributoSafeCall extends AcessoAtributo {
    private LeftExpression alvo;

    public AcessoAtributoSafeCall(LeftExpression alvo, Id id) {
        super(id);
        this.alvo = alvo;
    }

    @Override
    public Valor avaliar(AmbienteExecucaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
               ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        Valor v = alvo.avaliar(ambiente);
        if (v instanceof ValorNull) {
            return new ValorNull();
        }
        if (!(v instanceof ValorRef)) {
            throw new UnsupportedOperationException("Attribute access on non-object.");
        }
        ValorRef referencia = (ValorRef) v;
        Objeto objeto = ambiente.getObjeto(referencia);
        ContextoObjeto contexto = objeto.getEstado();
        Valor valorAtributo = contexto.get(super.getId());
        if (valorAtributo == null) {
            return new ValorNull();
        }
        return valorAtributo;
    }

    @Override
    public Expressao getExpressaoObjeto() {
        return alvo;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        boolean resposta = false;
        if (alvo.checaTipo(ambiente)) {
            try {
                Tipo tipo = alvo.getTipo(ambiente);
                if (tipo.equals(TipoClasse.TIPO_NULL)) {
                    return true;
                }
                if (tipo instanceof TipoClasse) {
                    DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(tipo.getTipo());

                    if (defClasse.getNomeSuperClasse() != null) {
                        resposta = this.checaTipoClasseMae(ambiente, defClasse.getNomeSuperClasse());
                    }
                    if (!resposta) {
                        defClasse.getTipoAtributo(super.getId());
                        resposta = true;
                    }
                }
            } catch (Exception e) {
                resposta = false;
            }
        }
        return resposta;
    }

    private boolean checaTipoClasseMae(AmbienteCompilacaoOO1 ambiente, Id idClasseMae) throws ClasseNaoDeclaradaException {
        boolean retorno = false;
        DefClasseOO2 defSuperClasse = (DefClasseOO2) ambiente.getDefClasse(idClasseMae);
        try {
            defSuperClasse.getTipoAtributo(super.getId());
            retorno = true;
        } catch (VariavelNaoDeclaradaException atrib) {
            if (defSuperClasse.getNomeSuperClasse() != null) {
                retorno = this.checaTipoClasseMae(ambiente, defSuperClasse.getNomeSuperClasse());
            }
        }
        return retorno;
    }

    @Override
    public Tipo getTipo(AmbienteCompilacaoOO1 ambiente)
        throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        Tipo tipo = null;
        try {
            // Try to get type using the standard approach (like AcessoAtributoId)
            Tipo tipoObjetoAcessado = this.getExpressaoObjeto().getTipo(ambiente);
            // If target is null, safe call returns null
            if (tipoObjetoAcessado.equals(TipoClasse.TIPO_NULL)) {
                return TipoClasse.TIPO_NULL;
            }
            loo2.plp.orientadaObjetos1.util.TipoClasse tipoClasse = (loo2.plp.orientadaObjetos1.util.TipoClasse) tipoObjetoAcessado;
            Id nomeClasse = tipoClasse.getTipo();
            loo2.plp.orientadaObjetos1.memoria.DefClasse defClasse = ambiente.getDefClasse(nomeClasse);
            tipo = defClasse.getTipoAtributo(super.getId());
        } catch (VariavelNaoDeclaradaException vnde) {
            // If not found, search in superclasses (like AcessoAtributoIdOO2)
            AmbienteCompilacaoOO1 ambienteOO2 = ambiente;
            Tipo tipoObjetoAcessado = this.getExpressaoObjeto().getTipo(ambienteOO2);
            // If target is null, safe call returns null
            if (tipoObjetoAcessado.equals(TipoClasse.TIPO_NULL)) {
                return TipoClasse.TIPO_NULL;
            }
            DefClasseOO2 defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(tipoObjetoAcessado.getTipo());
            if (defClasse.getNomeSuperClasse() != null) {
                defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(defClasse.getNomeSuperClasse());
            }
            while (tipo == null && defClasse != null) {
                try {
                    tipo = defClasse.getTipoAtributo(super.getId());
                } catch (VariavelNaoDeclaradaException e) {
                    // Vazio.
                }
                Id nomeSuperClasse = defClasse.getNomeSuperClasse();
                if (nomeSuperClasse != null) {
                    defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(nomeSuperClasse);
                } else {
                    defClasse = null;
                }
            }
        }
        return tipo;
    }

    public LeftExpression getAlvo() { return alvo; }

    @Override
    public String toString() { return alvo + "?." + super.getId(); }
}
