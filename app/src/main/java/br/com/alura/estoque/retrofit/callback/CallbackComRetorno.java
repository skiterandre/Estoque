package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallback <T> implements Callback {

    private final RespostaCallback callback;

    public BaseCallback(RespostaCallback callback) {
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
            callback.onFailure("Resposta não sucedida");
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call call, Throwable t) {
        callback.onFailure("Resposta não sucedida" + t.getMessage());
    }

    public interface RespostaCallback<T>{
        void onSuccess(T resultado);
        void onFailure(String error);
    }
}
