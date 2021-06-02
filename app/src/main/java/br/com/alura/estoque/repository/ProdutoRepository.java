package br.com.alura.estoque.repository;

import android.content.Context;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.callback.CallbackComRetorno;
import br.com.alura.estoque.retrofit.callback.CallbackSemRetorno;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;

public class ProdutoRepository {

    private final ProdutoDAO dao;
    private final ProdutoService produtoService;

    public ProdutoRepository(Context context) {
        EstoqueDatabase db = EstoqueDatabase.getInstance(context);
        this.dao = db.getProdutoDAO();
        produtoService = new EstoqueRetrofit().getProdutoService();
    }



    public void buscaProdutos(DadosCarregadosCallback<List<Produto>> callback) {
        buscaProdutosInternos(callback);
    }

    public void remove(Produto produto, DadosCarregadosCallback<Void> callback) {
        removeApi(produto, callback);

    }

    private void removeApi(Produto produto, DadosCarregadosCallback<Void> callback) {
        Call call = produtoService.remove(produto.getId());
        call.enqueue(new CallbackSemRetorno(new CallbackSemRetorno.RespostaCallback() {
            @Override
            public void onSuccess() {
                removeInterno(produto, callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        }));
    }

    private void removeInterno(Produto produto, DadosCarregadosCallback<Void> callback) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produto);
            return null;
        }, callback::onSuccess)
                .execute();
    }

    public void salva(Produto produto,
                      DadosCarregadosCallback<Produto> callback) {

        salvaNaApi(produto, callback);
    }

    private void salvaNaApi(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.salva(produto);
        call.enqueue(new CallbackComRetorno(new CallbackComRetorno.RespostaCallback<Produto>() {
            @Override
            public void onSuccess(Produto produtoSalvo) {
                salvaInterno(produtoSalvo, callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        }));;
    }

    private void salvaInterno(Produto produto, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, callback::onSuccess)
                .execute();
    }

    private void buscaProdutosInternos(DadosCarregadosCallback<List<Produto>> callback) {
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    callback.onSuccess(resultado);
                    buscaProdutosNaApi(callback);
                })
                .execute();
    }

    private void buscaProdutosNaApi(DadosCarregadosCallback<List<Produto>> callback) {

        Call<List<Produto>> listCall = produtoService.buscaTodos();

        listCall.enqueue(new CallbackComRetorno<>(new CallbackComRetorno.RespostaCallback<List<Produto>>() {
            @Override
            public void onSuccess(List<Produto> produtosNovos) {
                atualizaInterno(produtosNovos, callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        }));

    }

    private void atualizaInterno(List<Produto> produtos, DadosCarregadosCallback<List<Produto>> callback) {
        new BaseAsyncTask<>(()->{
            dao.salva(produtos);
            return dao.buscaTodos();

        }, callback::onSuccess)
                .execute();
    }

    public void edita( Produto produto, DadosCarregadosCallback<Produto> callback) {
        editaNaApi(produto, callback);
    }

    private void editaNaApi(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.edita(produto.getId(), produto);
        call.enqueue(new CallbackComRetorno<>(new CallbackComRetorno.RespostaCallback<Produto>() {
            @Override
            public void onSuccess(Produto produtoAtualizadoApi) {
                editaInterno(produtoAtualizadoApi, callback);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        }));
    }

    private void editaInterno(Produto produtoAtualizadoApi, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            dao.atualiza(produtoAtualizadoApi);
            return produtoAtualizadoApi;
        }, callback::onSuccess)
                .execute();
    }

    public interface DadosCarregadosCallback<T>{
        void onSuccess(T resultado);
        void onError(String erro);
    }
}
