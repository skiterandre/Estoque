package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackSemRetorno implements Callback {
    private static final String MENSAGEM_ERRO_FALHA = "Resposta não sucedida";
    private final RespostaCallback callback;

    public CallbackSemRetorno(RespostaCallback callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call call, Response response) {
        if(response.isSuccessful()){
            callback.onSuccess();
        }else{
            callback.onFailure(MENSAGEM_ERRO_FALHA);
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call call, Throwable t) {
        callback.onFailure(MENSAGEM_ERRO_FALHA + t.getMessage());
    }

    public interface RespostaCallback{
        void onSuccess();
        void onFailure(String error);
    }
}
