package loo2.plp.orientadaObjetos1.util;

import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;

/**
 * Representa um tipo anulável (equivalente a T? do Kotlin).
 */
public class TipoNullable implements Tipo {

    private final Tipo tipoBase;

    public TipoNullable(Tipo tipoBase) {
        this.tipoBase = tipoBase;
    }

    @Override
    public Id getTipo() {
        return tipoBase.getTipo();
    }

    @Override
    public boolean eValido(AmbienteCompilacaoOO1 ambiente)
            throws ClasseNaoDeclaradaException {
        return tipoBase.eValido(ambiente);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof TipoNullable) {
            return tipoBase.equals(((TipoNullable) obj).tipoBase);
        }
        // Permite comparar com o tipo base + null
        return tipoBase.equals(obj);
    }

    @Override
    public boolean aceitaNulo() {
        return true;
    }

    @Override
    public Tipo comoNullable() {
        return this; // Já é nullable
    }

    // --- MÉTODO ADICIONADO PARA CORRIGIR O ERRO ---
    @Override
    public Tipo comoNaoNullable() {
        // Se o tipo base já for não-anulável, retorna ele.
        // Se for uma estrutura como (int?)?, retorna int?.
        // Pela lógica de tipo, comoNaoNullable deve remover a característica nullable.

        // Assumindo que Tipo tem um método para retornar a versão não-nullable
        // ou que a própria classe base já é a versão não-nullable.
        // Se o tipo base *não* tiver o método comoNaoNullable(), use:
        // return tipoBase.comoNaoNullable();

        // Pela sua implementação anterior (onde TipoClasse e TipoPrimitivo tinham comoNaoNullable),
        // o TipoNullable deveria retornar a versão não-anulável de seu tipo base.

        // Caso simples: o TipoNullable é o próprio objeto, e o tipo base é o Tipo não-nullable.
        return this.tipoBase;
    }
    // ----------------------------------------------

    @Override
    public String toString() {
        return tipoBase.toString() + "?";
    }
}