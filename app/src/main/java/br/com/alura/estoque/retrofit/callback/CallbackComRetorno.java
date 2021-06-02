package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackComRetorno<T> implements Callback {

    private static final String MENSAGEM_ERRO_RESPOSTA = "Resposta não sucedida:";
    private final RespostaCallback callback;

    public CallbackComRetorno(RespostaCallback callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call call, Response response) {
        if(response.isSuccessful()){
            T result = (T)response.body();
            if(result != null){
                callback.onSuccess(result);
            }
        }else{
            callback.onFailure(
                    MENSAGEM_ERRO_RESPOSTA);
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call call, Throwable t) {
        callback.onFailure(MENSAGEM_ERRO_RESPOSTA + t.getMessage());
    }

    public interface RespostaCallback<T>{
        void onSuccess(T resultado);
        void onFailure(String error);
    }
}
