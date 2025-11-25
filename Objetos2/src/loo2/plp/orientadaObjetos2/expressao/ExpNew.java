package loo2.plp.orientadaObjetos2.expressao;

import java.util.HashMap;

import loo2.plp.expressions2.memory.VariavelJaDeclaradaException;
import loo2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import loo2.plp.orientadaObjetos1.comando.ChamadaProcedimento;
import loo2.plp.orientadaObjetos1.comando.Procedimento;
import loo2.plp.orientadaObjetos1.declaracao.variavel.DecVariavel;
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
import loo2.plp.orientadaObjetos1.expressao.valor.ValorRef;
import loo2.plp.orientadaObjetos1.memoria.AmbienteCompilacaoOO1;
import loo2.plp.orientadaObjetos1.memoria.AmbienteExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.ContextoExecucaoOO1;
import loo2.plp.orientadaObjetos1.memoria.Objeto;
import loo2.plp.orientadaObjetos1.memoria.colecao.ListaValor;
import loo2.plp.orientadaObjetos2.memoria.AmbienteExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.ContextoExecucaoOO2;
import loo2.plp.orientadaObjetos2.memoria.DefClasseOO2;
import loo2.plp.orientadaObjetos1.util.Tipo;
import loo2.plp.orientadaObjetos1.util.TipoClasse;

/**
 * Expressão que cria um novo objeto e retorna sua referência.
 * Permite usar "new" como expressão, por exemplo: tvLCD2?: new TvLCD(...)
 */
public class ExpNew implements Expressao {
    
    private Id classe;
    private ListaExpressao parametrosReais;
    
    public ExpNew(Id classe, ListaExpressao parametrosReais) {
        this.classe = classe;
        this.parametrosReais = parametrosReais != null ? parametrosReais : new ListaExpressao();
    }
    
    private void extendsClasse(AmbienteExecucaoOO2 ambiente, DefClasseOO2 classe, Objeto objeto) 
            throws ClasseNaoDeclaradaException, VariavelNaoDeclaradaException, 
            VariavelJaDeclaradaException, ObjetoNaoDeclaradoException, 
            ClasseJaDeclaradaException, ObjetoJaDeclaradoException {
        if (classe.getNomeSuperClasse() != null) {
            DefClasseOO2 classeMae = (DefClasseOO2) ambiente.getDefClasse(classe.getNomeSuperClasse());
            this.extendsClasse(ambiente, classeMae, objeto);
        }
        
        DecVariavel decVariavel = classe.getDecVariavel();
        AmbienteExecucaoOO1 aux = decVariavel.elabora(new ContextoExecucaoOO1(ambiente));
        HashMap<loo2.plp.expressions2.expression.Id, Valor> estadoObj = aux.getPilha().pop();
        
        for (loo2.plp.expressions2.expression.Id id : estadoObj.keySet()) {
            // Se a variavel nao havia sido declarada adicione
            if (!objeto.getEstado().containsKey(id)) {
                objeto.getEstado().put(id, estadoObj.get(id));
            }
        }
    }
    
    @Override
    public Valor avaliar(AmbienteExecucaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException,
            ObjetoNaoDeclaradoException, ClasseNaoDeclaradaException {
        
        AmbienteExecucaoOO2 ambienteOO2 = (AmbienteExecucaoOO2) ambiente;
        
        try {
            // Recupera a definição da classe
            DefClasseOO2 defClasse = (DefClasseOO2) ambienteOO2.getDefClasse(classe);
            DecVariavel decVariavel = defClasse.getDecVariavel();
            
            // Cria uma instância auxiliar do ambiente para fazer o elabora da decVariavel
            AmbienteExecucaoOO1 aux = decVariavel.elabora(new ContextoExecucaoOO1(ambienteOO2));
            HashMap<loo2.plp.expressions2.expression.Id, Valor> estadoObj = aux.getPilha().pop();
            
            // Cria o objeto com o estado inicial
            Objeto objeto = new Objeto(classe, new loo2.plp.orientadaObjetos1.memoria.ContextoObjeto(estadoObj));
            
            // Extends classe mae
            if (defClasse.getNomeSuperClasse() != null) {
                DefClasseOO2 classeMae = (DefClasseOO2) ambienteOO2.getDefClasse(defClasse.getNomeSuperClasse());
                this.extendsClasse(ambienteOO2, classeMae, objeto);
            }
            
            // Mapeia o objeto no ambiente
            ValorRef vr = ambienteOO2.getProxRef();
            ambienteOO2.mapObjeto(vr, objeto);
            
            // Executa o construtor
            Procedimento metodo = defClasse.getConstrutor().getProcedimento();
            AmbienteExecucaoOO2 aux2 = new ContextoExecucaoOO2(ambienteOO2);
            aux2.changeValor(new Id("this"), vr);
            
            ListaValor valoresDosParametros = parametrosReais.avaliar(ambienteOO2);
            try {
                new ChamadaProcedimento(metodo, parametrosReais, valoresDosParametros).executar(aux2);
            } catch (ProcedimentoNaoDeclaradoException | ProcedimentoJaDeclaradoException | EntradaInvalidaException e) {
                throw new ClasseNaoDeclaradaException(classe);
            }
            
            return vr;
        } catch (ClasseJaDeclaradaException | ObjetoJaDeclaradoException e) {
            // Convert to ClasseNaoDeclaradaException since Expressao interface doesn't allow these
            throw new ClasseNaoDeclaradaException(classe);
        }
    }
    
    @Override
    public boolean checaTipo(AmbienteCompilacaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        try {
            // Verifica se a classe existe
            ambiente.getDefClasse(classe);
            
            // Verifica se os parâmetros são compatíveis (simplificado)
            // TODO: Verificar compatibilidade dos parâmetros com o construtor
            // Verifica cada expressão na lista individualmente
            ListaExpressao lista = parametrosReais;
            while (lista.length() > 0) {
                if (!lista.getHead().checaTipo(ambiente)) {
                    return false;
                }
                if (lista.length() > 1) {
                    lista = (ListaExpressao) lista.getTail();
                } else {
                    break;
                }
            }
            return true;
        } catch (ClasseNaoDeclaradaException e) {
            return false;
        }
    }
    
    @Override
    public Tipo getTipo(AmbienteCompilacaoOO1 ambiente)
            throws VariavelNaoDeclaradaException, ClasseNaoDeclaradaException {
        return new TipoClasse(classe);
    }
}

