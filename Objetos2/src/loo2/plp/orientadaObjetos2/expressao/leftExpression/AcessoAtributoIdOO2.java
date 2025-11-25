package loo2.plp.orientadaObjetos2.expressao.leftExpression;

import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.excecao.declaracao.ClasseNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.AcessoAtributoId;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.Id;
import loo2.plp.orientadaObjetos1.expressao.leftExpression.LeftExpression;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;
import loo2.plp.orientadaObjetos2.memoria.AmbienteCompilacaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos2.util.TipoNullable;

public class AcessoAtributoIdOO2 extends AcessoAtributoId{

	public AcessoAtributoIdOO2(LeftExpression av, Id id) {
		super(av, id);
	}
	
	private boolean checaTipoClasseMae (AmbienteCompilacaoOO1 ambiente, Id idClasseMae) throws ClasseNaoDeclaradaException {
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
	public boolean checaTipo(AmbienteCompilacaoOO1 ambiente) throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        boolean resposta = false;
        if(av.checaTipo(ambiente)) {
            try{
                Tipo tipo = av.getTipo(ambiente);
                // Se o tipo for nullable, permitir acesso (retorna null se o objeto for null)
                if (tipo instanceof TipoNullable) {
                    TipoNullable tipoNullable = (TipoNullable) tipo;
                    TipoClasse tipoBase = tipoNullable.getTipoBase();
                    DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(tipoBase.getTipo());
                    
                    if (defClasse.getNomeSuperClasse() != null) {
                        resposta = this.checaTipoClasseMae(ambiente, defClasse.getNomeSuperClasse());
                    }
                    
                    // Se a verificacao pelo atributo nas classes maes nao encontrou nada
                    if (!resposta) {
                        defClasse.getTipoAtributo(super.getId());
                        resposta = true;  
                    }
                    // Marcar que o tipo é nullable
                    this.tipoNullable = true;
                } else if (tipo instanceof TipoClasse) {
                    DefClasseOO2 defClasse = (DefClasseOO2) ambiente.getDefClasse(tipo.getTipo());
                    
                    if (defClasse.getNomeSuperClasse() != null) {
                        resposta = this.checaTipoClasseMae(ambiente, defClasse.getNomeSuperClasse());
                    }
                    
                    // Se a verificacao pelo atributo nas classes maes nao encontrou nada
                    if (!resposta) {
                        defClasse.getTipoAtributo(super.getId());
                        resposta = true;  
                    }
                    // Marcar que o tipo NÃO é nullable
                    this.tipoNullable = false;
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
	
	@Override
	public Tipo getTipo(AmbienteCompilacaoOO1 ambiente)
			throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
		Tipo tipo = null;
		try{
			tipo = super.getTipo(ambiente);
		} catch(VariavelNaoDeclaradaException vnde){
			AmbienteCompilacaoOO2 ambienteOO2 = (AmbienteCompilacaoOO2) ambiente;
			Tipo tipoObjetoAcessado = this.getExpressaoObjeto().getTipo(ambienteOO2);
			
			// Se o tipo for nullable, usar o tipo base para buscar o atributo
			TipoClasse tipoBase = null;
			if (tipoObjetoAcessado instanceof TipoNullable) {
				TipoNullable tipoNullable = (TipoNullable) tipoObjetoAcessado;
				tipoBase = tipoNullable.getTipoBase();
			} else if (tipoObjetoAcessado instanceof TipoClasse) {
				tipoBase = (TipoClasse) tipoObjetoAcessado;
			}
			
			if (tipoBase != null) {
				DefClasseOO2 defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(tipoBase.getTipo());
				if (defClasse.getNomeSuperClasse() != null) {
					defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(defClasse.getNomeSuperClasse());
				}
				while(tipo == null && defClasse != null){
					try{
						tipo = defClasse.getTipoAtributo(getId());
					} catch(VariavelNaoDeclaradaException e){
						// Vazio.
					}
					Id nomeSuperClasse = defClasse.getNomeSuperClasse();
					if(nomeSuperClasse != null){
						defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(nomeSuperClasse);
					} else {
						defClasse = null;
					}
				}
			}
		}
		return tipo;
	
	}
}
