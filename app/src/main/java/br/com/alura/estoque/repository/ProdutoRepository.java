package br.com.alura.estoque.repository;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ProdutoRepository {

    private final ProdutoDAO dao;
    private final ProdutoService produtoService;

    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;
        produtoService = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosListener<List<Produto>> listener) {
        buscaProdutosInternos(listener);
    }

    public void salva(Produto produto,
                      DadosCarregadosCallback<Produto> callback) {

        salvaNaApi(produto, callback);
    }

    private void salvaNaApi(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.salva(produto);
        call.enqueue(new Callback<Produto>(){

            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Produto> call, Response<Produto> response) {
                if(response.isSuccessful()){
                    Produto produtoSalvo = response.body();
                    if(produtoSalvo != null)
                        salvaInterno(produtoSalvo, callback);
                }else{
                    callback.onError("Resposta não sucedida!");
                }

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Produto> call, Throwable t) {
                callback.onError("Falha de comunicação: " + t.getMessage());
            }
        });
    }

    private void salvaInterno(Produto produto, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, callback::onSuccess)
                .execute();
    }

    private void buscaProdutosInternos(DadosCarregadosListener<List<Produto>> listener) {
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    listener.onLoaded(resultado);
                    buscaProdutosNaApi(listener);
                })
                .execute();
    }

    private void buscaProdutosNaApi(DadosCarregadosListener<List<Produto>> listener) {

        Call<List<Produto>> listCall = produtoService.buscaTodos();
        new BaseAsyncTask<List<Produto>>(() -> {
            try {
                Response<List<Produto>> resposta = listCall.execute();
                List<Produto> produtosNovos = resposta.body();
                dao.salva(produtosNovos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dao.buscaTodos();
        },listener::onLoaded)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface DadosCarregadosListener<T> {
        void onLoaded(T resultado);
    }

    public interface DadosCarregadosCallback<T>{
        void onSuccess(T resultado);
        void onError(String erro);
    }
}
